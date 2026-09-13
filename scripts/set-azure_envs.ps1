# run: . .\scripts\set-azure_envs.ps1

# Azure-Umgebungsvariablen setzen
$RG="banking-app-rg"
$LOCATION="germanywestcentral"
$ACR="bankingappregistry"
$ENV="banking-env"

# ACR-Zugangsdaten abrufen
$ACR_USER = az acr credential show --name $ACR --query username --output tsv
$ACR_PASS = az acr credential show --name $ACR --query 'passwords[0].value' --output tsv