$ErrorActionPreference = "Stop"  # stop on any error

Write-Host "`n>>> Building Konto-Service..." -ForegroundColor Cyan
Set-Location konto-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..

Write-Host "`n>>> Building Benachrichtigung-Service..." -ForegroundColor Cyan
Set-Location benachrichtigung-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..


Write-Host "`n>>> Starting Docker Compose (includes frontend)..." -ForegroundColor Cyan
docker compose up --build