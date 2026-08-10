param(
    [int]$FrontendPort = 5173,
    [int]$BackendPort = 8080,
    [string]$RemoteAddress = 'Any',
    [switch]$IncludeBackend
)

$ErrorActionPreference = 'Stop'
$rules = @(
    @{ Name = 'LurePilot Frontend'; Port = $FrontendPort }
)
if ($IncludeBackend) {
    $rules += @{ Name = 'LurePilot Backend'; Port = $BackendPort }
}

foreach ($rule in $rules) {
    $displayName = "$($rule.Name) ($($rule.Port))"
    Get-NetFirewallRule -DisplayName $displayName -ErrorAction SilentlyContinue | Remove-NetFirewallRule
    New-NetFirewallRule -DisplayName $displayName -Direction Inbound -Action Allow -Protocol TCP -LocalPort $rule.Port -Profile Private -RemoteAddress $RemoteAddress | Out-Null
    Write-Host "Allowed TCP $($rule.Port) for $RemoteAddress on the Private profile."
}

Write-Host 'Run this script from an elevated PowerShell window. Prefer a VPN subnet or the iPhone VPN IP instead of Any.'
