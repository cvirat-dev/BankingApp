[![CI](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml/badge.svg)](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml)

[![Azure CD](https://github.com/cvirat-dev/BankingApp/actions/workflows/deploy-azure.yml/badge.svg)](https://github.com/cvirat-dev/BankingApp/actions/workflows/deploy-azure.yml)

[![OpenShift CD](https://github.com/cvirat-dev/BankingApp/actions/workflows/deploy-openshift.yml/badge.svg)](https://github.com/cvirat-dev/BankingApp/actions/workflows/deploy-openshift.yml)

# Banking App – Microservices Demo

A fullstack demo project showcasing a simple banking application
built with Java/Spring Boot microservices and Angular.

## Tech Stack

- Backend: Java 21, Spring Boot 3, Spring Data JPA, H2
- Frontend: Angular 17, TypeScript, Angular HttpClient
- Infrastructure: Docker, Docker Compose

## Architecture

Two independent Spring Boot microservices communicate via REST.

```text
+-------------------+      HTTP REST      +---------------------------+
|                   | ------------------> |                           |
| Angular Frontend  |                     |  Konto Service            |
| localhost:4200    | <------------------ |  - accounts               |
|                   |                     |  - transactions           |
+-------------------+                     |  - balances               |
                                          +---------------------------+
                                                        |
                                                        | triggers events
                                                        v
                                            +---------------------------+
                                            |                           |
                                            | Benachrichtigung Service  |
                                            |  - notifications          |
                                            |  - account events         |
                                            |                           |
                                            +---------------------------+
```

- **Konto Service**: Manages bank accounts and transactions.
- **Benachrichtigung Service**: Handles notifications for account events.

## Getting Started

Prerequisites:

- Docker is running
- GNU Make is available (`make --version`)

### Option 1: Start with Makefile (recommended)

Builds both backend services and starts Docker Compose with the selected environment file.

```bash
# DEV profile
make run-dev

# PROD profile
make run-prod
```

### Option 2: Start with PowerShell script

If you prefer PowerShell, use the script in the new `scripts/` folder.

```powershell
# Interactive profile selection (DEV / PROD), then build + compose up
.\scripts\build-and-run.ps1
```

### Option 3: Manually build JARs and start Docker Compose

```bash
# Build the JARs for both services
cd konto-service && .\mvnw.cmd clean package -DskipTests
cd ..\benachrichtigung-service && .\mvnw.cmd clean package -DskipTests
# Start Docker Compose with the new images
docker compose up --build
```

## OpenAPI

Both backend services expose OpenAPI specifications through Springdoc. After starting the application, use Swagger UI to explore and try the REST endpoints:

- Konto Service: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) ([OpenAPI JSON](http://localhost:8081/v3/api-docs))
- Benachrichtigung Service: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) ([OpenAPI JSON](http://localhost:8082/v3/api-docs))

The Angular API clients are generated from these specifications with OpenAPI Generator. To regenerate both clients, run from the repository root:

```bash
make generate-api
```

This command requires Docker and GNU Make. It starts both backend services, generates the clients, and stops the services when generation finishes. Generated code is written to `banking-frontend/src/app/api/konto-service/` and `banking-frontend/src/app/api/benachrichtigung-service/`.

## Deployment Platforms

### Azure

Both backend services are deployed as Azure Container Apps.
The frontend is deployed as an Azure Static Web App.

### OpenShift Developer Sandbox

As an additional learning exercise, the app was also deployed in parallel to Kubernetes/OpenShift (Red Hat Developer Sandbox),
to gain hands-on experience with a production-grade cluster platform used in enterprise environments.

This deployment is not the primary showcase — it's a demonstration of working with the OpenShift Developer Sandbox specifically.

## Workspace Notes

- PowerShell helper scripts are located in `scripts/`
- Frontend dev script: `./scripts/run-frontend.ps1`

## Access the application locally

Open [http://localhost:4200](http://localhost:4200)
