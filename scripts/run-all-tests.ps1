$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

$services = @(
    "konto-service",
    "benachrichtigung-service"
)

$failedServices = @()

foreach ($service in $services) {
    $servicePath = Join-Path $repoRoot $service

    if (-not (Test-Path $servicePath)) {
        Write-Host "`n>>> Skipping $service (path not found: $servicePath)" -ForegroundColor Yellow
        $failedServices += $service
        continue
    }

    Push-Location $servicePath
    try {
        Write-Host "`n>>> Running Maven tests in $service..." -ForegroundColor Cyan
        .\mvnw.cmd test

        if ($LASTEXITCODE -ne 0) {
            Write-Host ">>> Tests failed in $service" -ForegroundColor Red
            $failedServices += $service
        } else {
            Write-Host ">>> Tests passed in $service" -ForegroundColor Green
        }
    }
    finally {
        Pop-Location
    }
}

Write-Host "`n==================== TEST SUMMARY ===================="
if ($failedServices.Count -eq 0) {
    Write-Host "All Maven tests passed in all services." -ForegroundColor Green
    exit 0
}

Write-Host ("Test failures in: " + ($failedServices -join ", ")) -ForegroundColor Red
exit 1
