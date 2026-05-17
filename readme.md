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

- **Konto Service**: Manages bank accounts and transactions.
- **Benachrichtigung Service**: Handles notifications for account events.

## Getting Started

Prerequisites: Docker is running

### Option 1: Run the build-and-run script (PowerShell)

```powershell
# Build both services and start Docker Compose
.\build-and-run.ps1
```

### Option 2: Manually build JARs and start Docker Compose

```bash
# Build the JARs for both services
cd konto-service && .\mvnw.cmd clean package -DskipTests
cd ..\benachrichtigung-service && .\mvnw.cmd clean package -DskipTests
# Start Docker Compose with the new images
docker compose up --build
```

## Access the application

Open [http://localhost:4200](http://localhost:4200)
