. "$PSScriptRoot\set-azure_envs.ps1"

az staticwebapp create `
--name banking-frontend `
--resource-group $RG `
--location "eastus2" `
--source https://github.com/cvirat-dev/BankingApp `
--branch main `
--app-location '/banking-frontend' `
--output-location 'dist/banking-frontend/browser' `
--login-with-github