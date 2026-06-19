.PHONY: build run-dev run-prod clean

# OS-Erkennung: Windows_NT = Windows, sonst Linux/Mac
ifeq ($(OS),Windows_NT)
    MVNW = mvnw.cmd
else
    MVNW = ./mvnw
endif

build:
	cd konto-service && $(MVNW) clean package -DskipTests
	cd benachrichtigung-service && $(MVNW) clean package -DskipTests

run-dev: build
	docker compose --env-file .env.dev up --build

run-prod: build
	docker compose --env-file .env.prod up --build

clean:
	docker compose down -v