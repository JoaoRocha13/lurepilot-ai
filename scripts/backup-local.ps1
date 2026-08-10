param(
    [string]$BackupRoot = (Join-Path (Split-Path -Parent $PSScriptRoot) 'backups'),
    [int]$KeepLast = 7
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$uploadsRoot = Join-Path $projectRoot 'backend/uploads'
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$backupDirectory = Join-Path $BackupRoot $timestamp
$databaseBackup = Join-Path $backupDirectory 'lurepilot.dump'
$imagesBackup = Join-Path $backupDirectory 'uploads.zip'

New-Item -ItemType Directory -Force -Path $backupDirectory | Out-Null

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker was not found. Start Docker Desktop before creating a PostgreSQL backup.'
}

$containerDump = "/tmp/lurepilot-$timestamp.dump"
& docker exec lurepilot-postgres pg_dump -U postgres -d lurepilot -Fc -f $containerDump
if ($LASTEXITCODE -ne 0) {
    throw 'PostgreSQL backup failed. Check that the lurepilot-postgres container is running.'
}

& docker cp "lurepilot-postgres:$containerDump" $databaseBackup
& docker exec lurepilot-postgres rm -f $containerDump
if ($LASTEXITCODE -ne 0) {
    throw 'The PostgreSQL dump was created but could not be copied to the backup folder.'
}

if (Test-Path $uploadsRoot) {
    Compress-Archive -Path $uploadsRoot -DestinationPath $imagesBackup -CompressionLevel Optimal
} else {
    Write-Warning 'The uploads directory does not exist yet; only the PostgreSQL dump was created.'
}

Get-ChildItem -Path $BackupRoot -Directory | Sort-Object LastWriteTime -Descending | Select-Object -Skip $KeepLast | Remove-Item -Recurse -Force
Write-Host "Backup created at $backupDirectory"
Write-Host 'Restore database with: docker cp <dump> lurepilot-postgres:/tmp/restore.dump; docker exec lurepilot-postgres pg_restore -U postgres -d lurepilot --clean --if-exists /tmp/restore.dump'
