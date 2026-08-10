param(
    [int]$FrontendPort = 5173,
    [int]$BackendPort = 8080,
    [string]$AiBaseUrl = 'http://localhost:1234/v1',
    [string]$MavenCommand = '',
    [switch]$Https,
    [switch]$SkipDocker,
    [switch]$SkipBackend,
    [switch]$SkipFrontend
)

$ErrorActionPreference = 'Stop'

# Some Windows environments expose both Path and PATH. Start-Process treats
# environment names case-insensitively, so normalize the duplicate before spawning services.
$canonicalPath = $env:Path
Remove-Item Env:PATH -ErrorAction SilentlyContinue
$env:Path = $canonicalPath

$projectRoot = Split-Path -Parent $PSScriptRoot
$backendRoot = Join-Path $projectRoot 'backend'
$frontendRoot = Join-Path $projectRoot 'frontend'
$logsRoot = Join-Path $projectRoot 'logs'
$pidFile = Join-Path $projectRoot '.local-pids.json'

New-Item -ItemType Directory -Force -Path $logsRoot | Out-Null

function Test-TcpPort([int]$Port) {
    $client = [System.Net.Sockets.TcpClient]::new()
    try {
        $connect = $client.BeginConnect('127.0.0.1', $Port, $null, $null)
        if (-not $connect.AsyncWaitHandle.WaitOne(700)) {
            return $false
        }
        $client.EndConnect($connect)
        return $true
    } catch {
        return $false
    } finally {
        $client.Dispose()
    }
}

function Wait-ForHttp([string]$Url, [int]$TimeoutSeconds = 45) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return $true
            }
        } catch {
            Start-Sleep -Seconds 1
        }
    } while ((Get-Date) -lt $deadline)

    return $false
}

function Start-LoggedProcess([string]$FilePath, [string[]]$Arguments, [string]$WorkingDirectory, [string]$LogName) {
    $stdout = Join-Path $logsRoot "$LogName.log"
    $stderr = Join-Path $logsRoot "$LogName.error.log"
    $process = Start-Process -FilePath $FilePath `
        -ArgumentList $Arguments `
        -WorkingDirectory $WorkingDirectory `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdout `
        -RedirectStandardError $stderr `
        -PassThru
    return $process.Id
}

if (-not $SkipDocker) {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        throw 'Docker was not found. Install/start Docker Desktop or run with -SkipDocker if PostgreSQL is already running.'
    }

    if (Test-TcpPort 5432) {
        Write-Host 'PostgreSQL is already listening on port 5432; reusing the running container.'
    } else {
        $dockerReady = $false
        $previousErrorActionPreference = $ErrorActionPreference
        $ErrorActionPreference = 'Continue'
        & docker info 1>$null 2>$null
        $dockerReady = $LASTEXITCODE -eq 0
        $ErrorActionPreference = $previousErrorActionPreference

        if (-not $dockerReady) {
            $dockerDesktopPath = 'C:\Program Files\Docker\Docker\Docker Desktop.exe'
            if (-not (Test-Path $dockerDesktopPath)) {
                throw 'Docker Desktop is not installed. Install/start Docker Desktop or run with -SkipDocker if PostgreSQL is already running.'
            }

            Write-Host 'Starting Docker Desktop...'
            Start-Process -FilePath $dockerDesktopPath -WindowStyle Normal | Out-Null
            $dockerDeadline = (Get-Date).AddSeconds(90)
            do {
                Start-Sleep -Seconds 2
                $previousErrorActionPreference = $ErrorActionPreference
                $ErrorActionPreference = 'Continue'
                & docker info 1>$null 2>$null
                $dockerReady = $LASTEXITCODE -eq 0
                $ErrorActionPreference = $previousErrorActionPreference
            } while (-not $dockerReady -and (Get-Date) -lt $dockerDeadline)
        }

        if (-not $dockerReady) {
            throw 'Docker Desktop did not become ready. Open Docker Desktop and retry.'
        }

        Push-Location $projectRoot
        try {
            $previousErrorActionPreference = $ErrorActionPreference
            $ErrorActionPreference = 'Continue'
            $composeOutput = & docker compose up -d 2>&1
            $composeExitCode = $LASTEXITCODE
            $ErrorActionPreference = $previousErrorActionPreference
            $composeOutput | Tee-Object -FilePath (Join-Path $logsRoot 'docker-compose.log')
            if ($composeExitCode -ne 0) {
                throw 'Could not start PostgreSQL with Docker Compose. Check logs/docker-compose.log.'
            }
        } finally {
            Pop-Location
        }
    }
}

$env:SERVER_PORT = [string]$BackendPort
$env:LUREPILOT_AI_BASE_URL = $AiBaseUrl
$env:LUREPILOT_UPLOADS_DIRECTORY = Join-Path $backendRoot 'uploads'
$env:LUREPILOT_LOG_FILE = Join-Path $logsRoot 'backend.log'
$env:LUREPILOT_LOG_LEVEL = 'INFO'
$env:VITE_HOST = '0.0.0.0'
$env:VITE_PORT = [string]$FrontendPort
$env:VITE_BACKEND_URL = "http://localhost:$BackendPort"

if ($Https) {
    $env:VITE_HTTPS = 'true'
    if (-not $env:VITE_HTTPS_KEY) { $env:VITE_HTTPS_KEY = Join-Path $projectRoot '.certs/lurepilot-key.pem' }
    if (-not $env:VITE_HTTPS_CERT) { $env:VITE_HTTPS_CERT = Join-Path $projectRoot '.certs/lurepilot-cert.pem' }
    if (-not (Test-Path $env:VITE_HTTPS_KEY) -or -not (Test-Path $env:VITE_HTTPS_CERT)) {
        throw 'HTTPS was requested but the certificate is missing. Run scripts/create-local-cert.ps1 first.'
    }
} else {
    $env:VITE_HTTPS = 'false'
}

$pids = [ordered]@{}
if (-not $SkipBackend) {
    $backendLauncher = Join-Path $backendRoot 'mvnw.cmd'
    if (-not [string]::IsNullOrWhiteSpace($MavenCommand)) {
        $backendLauncher = $MavenCommand
    } else {
        $systemMaven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
        if ($systemMaven) {
            $backendLauncher = $systemMaven.Source
        }
    }

    if (Test-TcpPort $BackendPort) {
        Write-Warning "Backend already listening on port $BackendPort. The existing process was not replaced; restart it manually if its environment variables changed."
    } else {
        $backendPid = Start-LoggedProcess $backendLauncher @('spring-boot:run') $backendRoot 'backend'
        $pids.backend = $backendPid
        Write-Host "Backend starting (PID $backendPid)."
    }

    if (-not (Wait-ForHttp "http://localhost:$BackendPort/api/health")) {
        throw "Backend did not become ready on port $BackendPort. Check logs/backend.error.log."
    }
}

if (-not $SkipFrontend) {
    if (Test-TcpPort $FrontendPort) {
        Write-Host "Frontend already listening on port $FrontendPort."
    } else {
        $frontendPid = Start-LoggedProcess 'npm.cmd' @('run', 'dev', '--', '--host', '0.0.0.0') $frontendRoot 'frontend'
        $pids.frontend = $frontendPid
        Write-Host "Frontend starting (PID $frontendPid)."
    }
}

$pids | ConvertTo-Json | Set-Content -Path $pidFile -Encoding utf8
$lmStudioReady = Wait-ForHttp "$AiBaseUrl/models" 3
$scheme = if ($Https) { 'https' } else { 'http' }
Write-Host ''
Write-Host "LurePilot is ready at ${scheme}://localhost:$FrontendPort"
Write-Host "For the iPhone, open ${scheme}://<VPN-IP-OF-COMPUTER>:$FrontendPort"
$networkAddresses = @(Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.254.*' } |
    Select-Object IPAddress, InterfaceAlias)
if ($networkAddresses.Count -gt 0) {
    Write-Host 'Available computer addresses:'
    foreach ($address in $networkAddresses) {
        Write-Host "  $($address.InterfaceAlias): $($address.IPAddress):$FrontendPort"
    }
}
if ($lmStudioReady) {
    Write-Host 'LM Studio responded on the configured URL.'
} else {
    Write-Warning "LM Studio did not respond at $AiBaseUrl. The app will still run, but AI generation will show an error until LM Studio is started."
}
Write-Host "Logs: $logsRoot"
