# Maven-Build für beide Services
# Azure-Login-Prüfung
# Anmeldung an der Azure Container Registry
# Docker-Images bauen für:
    # konto-service
    # benachrichtigung-service
# Zwei Tags pro Image:
    # Git-Commit-Tag, zum Beispiel a1b2c3d
    # latest
# Push beider Tags nach ACR
# Aktualisierung der bestehenden Azure Container Apps mit dem Commit-Tag

[CmdletBinding()]
param(
    [switch]$SkipMavenBuild
)

$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\set-azure_envs.ps1"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$services = @(
    @{ Name = 'konto-service'; Directory = 'konto-service' },
    @{ Name = 'benachrichtigung-service'; Directory = 'benachrichtigung-service' }
)

function Invoke-CheckedCommand {
    param(
        [Parameter(Mandatory)]
        [string]$FilePath,
        [Parameter(Mandatory)]
        [string[]]$ArgumentList
    )

    & $FilePath @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        throw "Befehl fehlgeschlagen ($LASTEXITCODE): $FilePath $($ArgumentList -join ' ')"
    }
}

if (-not (Get-Command az -ErrorAction SilentlyContinue)) {
    throw 'Azure CLI (az) wurde nicht gefunden.'
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker wurde nicht gefunden.'
}

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw 'Git wurde nicht gefunden.'
}

if (-not $SkipMavenBuild) {
    foreach ($service in $services) {
        $servicePath = Join-Path $repoRoot $service.Directory
        Write-Host "Baue $($service.Name) ..." -ForegroundColor Cyan
        Push-Location $servicePath
        try {
            Invoke-CheckedCommand -FilePath '.\mvnw.cmd' -ArgumentList @('clean', 'package', '-DskipTests')
        }
        finally {
            Pop-Location
        }
    }
}

Write-Host 'Pruefe Azure-Anmeldung ...' -ForegroundColor Cyan
Invoke-CheckedCommand -FilePath 'az' -ArgumentList @('account', 'show', '--output', 'none')

$commitTag = (& git -C $repoRoot rev-parse --short HEAD 2>$null).Trim()
if ([string]::IsNullOrWhiteSpace($commitTag)) {
    $commitTag = Get-Date -Format 'yyyyMMddHHmmss'
}

$registry = "$ACR.azurecr.io"
$imageTag = $commitTag

Write-Host "Melde mich an der Container Registry $registry an ..." -ForegroundColor Cyan
Invoke-CheckedCommand -FilePath 'az' -ArgumentList @('acr', 'login', '--name', $ACR)

foreach ($service in $services) {
    $image = "$registry/$($service.Name)"
    $versionedImage = "${image}:$imageTag"
    $latestImage = "${image}:latest"

    Write-Host "Baue Image $versionedImage ..." -ForegroundColor Cyan
    Invoke-CheckedCommand -FilePath 'docker' -ArgumentList @('build', '--tag', $versionedImage, '--tag', $latestImage, (Join-Path $repoRoot $service.Directory))

    Write-Host "Pushe $versionedImage und $latestImage ..." -ForegroundColor Cyan
    Invoke-CheckedCommand -FilePath 'docker' -ArgumentList @('push', $versionedImage)
    Invoke-CheckedCommand -FilePath 'docker' -ArgumentList @('push', $latestImage)

    Write-Host "Aktualisiere Container App $($service.Name) ..." -ForegroundColor Cyan
    Invoke-CheckedCommand -FilePath 'az' -ArgumentList @(
        'containerapp', 'update',
        '--name', $service.Name,
        '--resource-group', $RG,
        '--image', $versionedImage
    )
}

Write-Host "Deployment abgeschlossen. Image-Tag: $imageTag" -ForegroundColor Green