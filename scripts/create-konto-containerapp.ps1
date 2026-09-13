. "$PSScriptRoot\set-azure_envs.ps1"

az containerapp create `
  --name konto-service `
  --resource-group $RG `
  --environment $ENV `
  --image "$ACR.azurecr.io/konto-service:latest" `
  --target-port 8081 `
  --ingress external `
  --registry-server "$ACR.azurecr.io" `
  --registry-username $ACR_USER `
  --registry-password $ACR_PASS `
  --cpu 0.5 `
  --memory 1.0Gi `
  --min-replicas 0 `
  --max-replicas 1