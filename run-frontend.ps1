$ErrorActionPreference = "Stop"  # stop on any error

Push-Location .\banking-frontend
try {
    Write-Host "`n>>> Starting Banking-Frontend..." -ForegroundColor Cyan
    ng serve --open
} finally {
    Pop-Location
}
