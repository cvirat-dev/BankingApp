[![CI](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml/badge.svg)](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml)

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

## Workspace Notes

- PowerShell helper scripts are located in `scripts/`
- Frontend dev script: `./scripts/run-frontend.ps1`

## Access the application

Open [http://localhost:4200](http://localhost:4200)

<img width="2535" height="1262" alt="image" src="https://github.com/user-attachments/assets/7500de71-fb1e-48b5-8c0f-4cd72ddf7c15" />

