# Aplicaciones-Grupo2

API e-commerce con Spring Boot, JWT y persistencia MySQL.

## Repositorio público

https://github.com/MaximoDiNapoli/Aplicaciones-Grupo2

## Requisitos mínimos

- Java 21
- Maven Wrapper (incluido: `mvnw` / `mvnw.cmd`)
- Docker (opcional, recomendado para MySQL)
- Puerto 8080 libre para la API

## Ejecución local (mínima)

1. Levantar MySQL con Docker Compose:

	```bash
	docker compose up -d
	```

2. Iniciar la API:

	```bash
	./mvnw spring-boot:run
	```

	En Windows PowerShell:

	```powershell
	.\mvnw.cmd spring-boot:run
	```

3. Probar healthcheck:

	```bash
	curl http://localhost:8080/api/health
	```

## Credenciales de base de datos

- URL: `jdbc:mysql://localhost:3306/ecomerce_db`
- Usuario: `ecomerce`
- Password: `ecomerce123`

#Opcional

Existen scrips para poblar la bdd para poder realizar pruebas rapidas, bdd\reset_seed_chocolateria_animales.sql


