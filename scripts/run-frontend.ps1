$ErrorActionPreference = "Stop"  # stop on any error

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$frontendPath = Join-Path $repoRoot "banking-frontend"

Push-Location $frontendPath
try {
    Write-Host "`n>>> Starting Banking-Frontend..." -ForegroundColor Cyan
    ng serve --open
} finally {
    Pop-Location
}
