param(
    [string[]]$HostNames = @('localhost', '127.0.0.1'),
    [string]$CertificateDirectory = (Join-Path (Split-Path -Parent $PSScriptRoot) '.certs')
)

$ErrorActionPreference = 'Stop'
if (-not (Get-Command mkcert -ErrorAction SilentlyContinue)) {
    throw 'mkcert was not found. Install mkcert first, then run this script again.'
}

New-Item -ItemType Directory -Force -Path $CertificateDirectory | Out-Null
$keyPath = Join-Path $CertificateDirectory 'lurepilot-key.pem'
$certPath = Join-Path $CertificateDirectory 'lurepilot-cert.pem'

& mkcert -install
& mkcert -key-file $keyPath -cert-file $certPath @HostNames
if ($LASTEXITCODE -ne 0) {
    throw 'Could not create the local certificate.'
}

Write-Host "Certificate created for: $($HostNames -join ', ')"
Write-Host 'To use it, run: .\scripts\start-local.ps1 -Https'
Write-Host 'For Safari on iPhone, the mkcert local CA must also be trusted on the iPhone.'
