.PHONY: build generate-api run-dev run-prod clean check-env-local

# OS-Erkennung: Windows_NT = Windows, sonst Linux/Mac
ifeq ($(OS),Windows_NT)
    MVNW = mvnw.cmd
else
    MVNW = ./mvnw
endif

generate-api:
	docker compose up -d konto-service benachrichtigung-service
	docker compose --profile codegen run --rm api-generator
	docker compose stop konto-service benachrichtigung-service

build:
	cd konto-service && $(MVNW) clean package
	cd benachrichtigung-service && $(MVNW) clean package

check-env-local:
	$(if $(wildcard .env.local),,$(error .env.local fehlt. Bitte .env.local.example nach .env.local kopieren und DEMO_PASSWORD setzen))

run-dev: check-env-local build
	docker compose --env-file .env.dev up --build

run-prod: check-env-local build
	docker compose --env-file .env.prod up --build

test-auth:
	pwsh -File scripts/auth-smoke-test.ps1

clean:
	docker compose down -v