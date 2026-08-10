$ErrorActionPreference = 'SilentlyContinue'
$projectRoot = Split-Path -Parent $PSScriptRoot
$pidFile = Join-Path $projectRoot '.local-pids.json'

if (-not (Test-Path $pidFile)) {
    Write-Host 'No local process file found. Nothing to stop.'
    exit 0
}

$pids = Get-Content $pidFile -Raw | ConvertFrom-Json
foreach ($property in $pids.PSObject.Properties) {
    $process = Get-Process -Id ([int]$property.Value) -ErrorAction SilentlyContinue
    if ($process) {
        Stop-Process -Id $process.Id
        Write-Host "Stopped $($property.Name) (PID $($process.Id))."
    }
}

Remove-Item $pidFile -Force
