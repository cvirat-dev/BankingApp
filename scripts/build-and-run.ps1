$ErrorActionPreference = "Stop"  # stop on any error

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

# ─── Lokale Zugangsdaten prüfen ───────────────────────────────
if (-not (Test-Path (Join-Path $repoRoot ".env.local"))) {
    Write-Host "`nFEHLER: .env.local fehlt." -ForegroundColor Red
    Write-Host "Bitte .env.local.example nach .env.local kopieren und DEMO_PASSWORD setzen."
    exit 1
}

# ─── Profil-Auswahl ───────────────────────────────────────────
Write-Host "`nUmgebung auswählen:" -ForegroundColor Yellow
Write-Host "  [1] DEV  (H2 In-Memory, Debug-Logging)"
Write-Host "  [2] PROD (H2 File-Mode, minimales Logging)"
Write-Host ""

do {
    $choice = Read-Host "Eingabe (1 oder 2)"
} while ($choice -notin @("1", "2"))

$selectedProfile = if ($choice -eq "1") { "dev" } else { "prod" }
Write-Host "`n>>> Profil: $($selectedProfile.ToUpper())" -ForegroundColor Green

# ─── Build ────────────────────────────────────────────────────
Write-Host "`n>>> Building Konto-Service..." -ForegroundColor Cyan
Set-Location (Join-Path $repoRoot "konto-service")
.\mvnw.cmd clean package -DskipTests
$buildExitCode = $LASTEXITCODE
Set-Location $repoRoot
if ($buildExitCode -ne 0) {
    Write-Host "`nFEHLER: Build von konto-service fehlgeschlagen (Exit-Code $buildExitCode)." -ForegroundColor Red
    exit $buildExitCode
}

Write-Host "`n>>> Building Benachrichtigung-Service..." -ForegroundColor Cyan
Set-Location (Join-Path $repoRoot "benachrichtigung-service")
.\mvnw.cmd clean package -DskipTests
$buildExitCode = $LASTEXITCODE
Set-Location $repoRoot
if ($buildExitCode -ne 0) {
    Write-Host "`nFEHLER: Build von benachrichtigung-service fehlgeschlagen (Exit-Code $buildExitCode)." -ForegroundColor Red
    exit $buildExitCode
}

# ─── Docker Compose mit Profil ────────────────────────────────
Write-Host "`n>>> Starting Docker Compose ($($selectedProfile.ToUpper()))..." -ForegroundColor Cyan
docker compose --env-file ".env.$selectedProfile" up --build