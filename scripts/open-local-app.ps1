param(
    [int]$FrontendPort = 5173,
    [int]$BackendPort = 8080,
    [string]$AiBaseUrl = 'http://localhost:1234/v1',
    [string]$MavenCommand = '',
    [switch]$Https,
    [string]$BrowserUrl = ''
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$startScript = Join-Path $PSScriptRoot 'start-local.ps1'

try {
    $startArguments = @(
        '-NoProfile',
        '-ExecutionPolicy', 'Bypass',
        '-File', $startScript,
        '-FrontendPort', $FrontendPort,
        '-BackendPort', $BackendPort,
        '-AiBaseUrl', $AiBaseUrl
    )

    if (-not [string]::IsNullOrWhiteSpace($MavenCommand)) {
        $startArguments += @('-MavenCommand', $MavenCommand)
    }
    if ($Https) {
        $startArguments += '-Https'
    }

    & powershell.exe @startArguments
    if ($LASTEXITCODE -ne 0) {
        throw "The local stack could not be started. Check the logs folder in $projectRoot."
    }

    $scheme = if ($Https) { 'https' } else { 'http' }
    $url = if ([string]::IsNullOrWhiteSpace($BrowserUrl)) {
        "${scheme}://localhost:$FrontendPort"
    } else {
        $BrowserUrl
    }

    Start-Process $url
    Write-Host "LurePilot opened at $url"
} catch {
    $message = $_.Exception.Message
    $logsRoot = Join-Path $projectRoot 'logs'
    New-Item -ItemType Directory -Force -Path $logsRoot | Out-Null
    $message | Set-Content -Path (Join-Path $logsRoot 'launcher-error.log') -Encoding utf8
    try {
        Add-Type -AssemblyName PresentationFramework
        [System.Windows.MessageBox]::Show($message, 'LurePilot AI') | Out-Null
    } catch {
        Write-Error $message
    }
    exit 1
}
