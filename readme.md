[![CI](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml/badge.svg)](https://github.com/cvirat-dev/BankingApp/actions/workflows/ci.yml)

# Banking App – Microservices Demo
A fullstack demo project showcasing a simple banking application
built with Java/Spring Boot microservices and Angular.

> **Note:** The Angular frontend is not yet implemented.
## Tech Stack
- Backend: Java 21, Spring Boot 3, Spring Data JPA, H2
- Frontend: Angular 17, TypeScript, Angular HttpClient _(not yet implemented)_
- Infrastructure: Docker, Docker Compose
## Architecture
Two independent Spring Boot microservices communicate via REST.
An Angular SPA is planned as the UI layer but is not yet implemented.
## Getting Started
```powershell
# Build both services and start Docker Compose
.\build-and-run.ps1
```
Open http://localhost:4200