. "$PSScriptRoot\set-azure_envs.ps1"

az containerapp create `
  --name benachrichtigung-service `
  --resource-group $RG `
  --environment $ENV `
  --image "$ACR.azurecr.io/benachrichtigung-service:latest" `
  --target-port 8082 `
  --ingress external `
  --registry-server "$ACR.azurecr.io" `
  --registry-username $ACR_USER `
  --registry-password $ACR_PASS `
  --cpu 0.5 `
  --memory 1.0Gi `
  --env-vars SPRING_PROFILES_ACTIVE=prod `
  --min-replicas 0 `
  --max-replicas 1