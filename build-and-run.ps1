$ErrorActionPreference = "Stop"  # stop on any error

# ─── Profil-Auswahl ───────────────────────────────────────────
Write-Host "`nUmgebung auswählen:" -ForegroundColor Yellow
Write-Host "  [1] DEV  (H2 In-Memory, Debug-Logging)"
Write-Host "  [2] PROD (H2 File-Mode, minimales Logging)"
Write-Host ""

do {
    $input = Read-Host "Eingabe (1 oder 2)"
} while ($input -notin @("1", "2"))

$profile = if ($input -eq "1") { "dev" } else { "prod" }
Write-Host "`n>>> Profil: $($profile.ToUpper())" -ForegroundColor Green

# ─── Build ────────────────────────────────────────────────────
Write-Host "`n>>> Building Konto-Service..." -ForegroundColor Cyan
Set-Location konto-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..

Write-Host "`n>>> Building Benachrichtigung-Service..." -ForegroundColor Cyan
Set-Location benachrichtigung-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..

# ─── Docker Compose mit Profil ────────────────────────────────
Write-Host "`n>>> Starting Docker Compose ($($profile.ToUpper()))..." -ForegroundColor Cyan
$env:SPRING_PROFILES_ACTIVE = $profile
docker compose up --build