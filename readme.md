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
An Angular SPA serves as the UI layer.
## Getting Started
```bash
# Build backend services
cd konto-service && mvn clean package -DskipTests && cd ..
cd benachrichtigung-service && mvn clean package -DskipTests && cd ..
# Start everything
docker compose up --build
```
Open http://localhost:4200