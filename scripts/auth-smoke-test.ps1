# Smoke-Test für die Authentifizierung der Backends.
# Liest DEMO_USER und DEMO_PASSWORD aus .env.local (nicht eingecheckt), damit kein Passwort im Skript steht.
#
# Aufruf (Backends müssen laufen):
#   .\scripts\auth-smoke-test.ps1
#   .\scripts\auth-smoke-test.ps1 -EnvFile pfad\zu\anderer.env

param(
    [string]$EnvFile = (Join-Path $PSScriptRoot "..\.env.local"),
    [string]$KontoUrl = "http://localhost:8081",
    [string]$BenachrichtigungUrl = "http://localhost:8082"
)

# --- Zugangsdaten aus der .env-Datei lesen ---------------------------------
if (-not (Test-Path $EnvFile)) {
    Write-Error "Datei $EnvFile nicht gefunden."
    exit 1
}
$envVars = @{}
Get-Content $EnvFile |
    Where-Object { $_ -match '^\s*([^#][^=]*)=(.*)$' } |
    ForEach-Object { $envVars[$Matches[1].Trim()] = $Matches[2].Trim() }

$user = if ($envVars['DEMO_USER']) { $envVars['DEMO_USER'] } else { 'demo' }
$pass = $envVars['DEMO_PASSWORD']
if (-not $pass) {
    Write-Error "DEMO_PASSWORD fehlt in $EnvFile."
    exit 1
}
$auth = "${user}:${pass}"

# --- Hilfsfunktionen ---------------------------------------------------------
$script:failed = 0

function Check-Status {
    param([string]$Name, [int]$Expected, [string[]]$CurlArgs)
    $status = & curl.exe -s -o NUL -w "%{http_code}" @CurlArgs
    if ($status -eq "$Expected") {
        Write-Host "PASS  $Name ($status)" -ForegroundColor Green
    } else {
        Write-Host "FAIL  $Name (erwartet $Expected, erhalten $status)" -ForegroundColor Red
        $script:failed++
    }
}

function Check-NoBrowserPopup {
    param([string]$Name, [string]$Url)
    $headers = & curl.exe -s -D - -o NUL $Url
    if ($headers -match 'WWW-Authenticate') {
        Write-Host "FAIL  $Name (Header WWW-Authenticate vorhanden -> Browser-Popup)" -ForegroundColor Red
        $script:failed++
    } else {
        Write-Host "PASS  $Name (kein WWW-Authenticate)" -ForegroundColor Green
    }
}

# --- Ab Kapitel 4: SecurityConfig ------------------------------------------
Write-Host "`n== konto-service ==" -ForegroundColor Cyan
Check-Status "Ohne Login gesperrt"  401 @("$KontoUrl/api/konten")
Check-Status "Mit Login erlaubt"    200 @("-u", $auth, "$KontoUrl/api/konten")
Check-Status "Falsches Passwort"    401 @("-u", "${user}:falsch", "$KontoUrl/api/konten")
Check-Status "Health ohne Login"    200 @("$KontoUrl/actuator/health")
Check-NoBrowserPopup "Kein Browser-Popup" "$KontoUrl/api/konten"

Write-Host "`n== benachrichtigung-service ==" -ForegroundColor Cyan
# GET /api/benachrichtigungen (BenachrichtigungController#all), liefert alle Benachrichtigungen
$benachrichtigungEndpoint = "$BenachrichtigungUrl/api/benachrichtigungen"
Check-Status "Ohne Login gesperrt"  401 @($benachrichtigungEndpoint)
Check-Status "Mit Login erlaubt"    200 @("-u", $auth, $benachrichtigungEndpoint)
Check-Status "Health ohne Login"    200 @("$BenachrichtigungUrl/actuator/health")

# --- Ab Kapitel 5: Login-Check (einkommentieren, wenn umgesetzt) -----------
# Check-Status "/api/me mit Login"   200 @("-u", $auth, "$KontoUrl/api/me")
# Check-Status "/api/me ohne Login"  401 @("$KontoUrl/api/me")

# --- Ab Kapitel 6: WebSocket-Ticket (einkommentieren, wenn umgesetzt) ------
# Check-Status "Ticket mit Login"    200 @("-u", $auth, "$BenachrichtigungUrl/api/ws-ticket")
# Check-Status "Ticket ohne Login"   401 @("$BenachrichtigungUrl/api/ws-ticket")

# --- Ab Kapitel 7: Rate-Limit (einkommentieren, wenn umgesetzt) ------------
# Achtung: sperrt danach den Service für bis zu eine Minute, daher als letzter Test.
# $codes = 1..25 | ForEach-Object {
#     & curl.exe -s -o NUL -w "%{http_code}" -u "${user}:falsch" "$KontoUrl/api/konten"
# }
# if ($codes -contains "429") {
#     Write-Host "PASS  Rate-Limit greift (429 erhalten)" -ForegroundColor Green
# } else {
#     Write-Host "FAIL  Rate-Limit greift nicht" -ForegroundColor Red
#     $script:failed++
# }

# --- Ergebnis ----------------------------------------------------------------
Write-Host ""
if ($script:failed -eq 0) {
    Write-Host "Alle Checks bestanden." -ForegroundColor Green
} else {
    Write-Host "$script:failed Check(s) fehlgeschlagen." -ForegroundColor Red
}
exit $script:failed