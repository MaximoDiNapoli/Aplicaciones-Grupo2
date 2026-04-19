# Guia para levantar la base

Este proyecto usa Spring Boot con MySQL y scripts SQL versionados en la carpeta `bdd/`.

## Objetivo

Levantar la base de datos, dejarla lista para la API y verificar que la aplicacion pueda conectarse sin errores.

## Datos del proyecto

- Base de datos: `ecomerce_db`
- Usuario: `ecomerce`
- Password: `ecomerce123`
- Puerto MySQL: `3306`
- Puerto API: `8080`
- `spring.jpa.hibernate.ddl-auto=validate`

Eso significa que la base debe existir antes de arrancar la aplicacion.

## Version recomendada de MySQL

Para usar MySQL Workbench sin problemas, la version recomendada del servidor es `8.0.x`.

Workbench `8.0.46` muestra advertencias con `8.4.8` y puede cerrar la conexion o fallar al abrir la base. Si quieres trabajar desde Workbench, baja el servidor a `8.0.x` o usa otro cliente como MySQL Shell o DBeaver.

## Opcion recomendada: MySQL local fuera de Docker

Esta es la ruta mas util si quieres ver la base desde Workbench.

1. Verifica que MySQL Server este instalado y corriendo en `localhost:3306`.
2. Crea la base `ecomerce_db` si todavia no existe.
3. Ejecuta los scripts en este orden:
   - `bdd/v1.sql`
   - `bdd/v2.sql`
   - `bdd/v3.sql`
4. Arranca la aplicacion con Maven.

Ejemplo en PowerShell:

```powershell
& 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot -e "CREATE DATABASE IF NOT EXISTS ecomerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
Get-Content .\bdd\v1.sql | & 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db
Get-Content .\bdd\v2.sql | & 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db
Get-Content .\bdd\v3.sql | & 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db
.\mvnw.cmd spring-boot:run
```

## Opcion Docker

Si Docker funciona en el entorno, el `docker-compose.yml` levanta MySQL y ejecuta el cargador de scripts.

1. Ejecuta `docker compose up -d`.
2. El contenedor usa `./bdd/run-init.sh` para aplicar automaticamente todos los archivos `v*.sql` en orden natural.
3. La aplicacion sigue apuntando a `localhost:3306`, asi que no cambies el datasource si quieres usar la base local montada por Docker.

## Que hace cada script

- `bdd/v1.sql`: crea la estructura base.
- `bdd/v2.sql`: agrega `id_usuario` a `Producto` y su llave foranea.
- `bdd/v3.sql`: agrega el campo `foto` a `Producto`.

## Validacion rapida

Despues de levantar todo, valida esto:

1. `SELECT COUNT(*) FROM producto;`
2. `SELECT * FROM producto LIMIT 5;`
3. Abre MySQL Workbench y conecta a `127.0.0.1:3306`.
4. Verifica que la tabla `Producto` exista dentro de `ecomerce_db`.
5. Si la API ya arranco, prueba `GET http://localhost:8080/api/productos`.

## Problema conocido con Workbench

Si MySQL Workbench se cierra o cancela la conexion al abrir `ecomerce_db`, revisa esto primero:

1. Usa una conexion nueva de tipo `Standard (TCP/IP)`.
2. Host: `127.0.0.1`, puerto: `3306`.
3. Usuario: `root` o `ecomerce`.
4. No pongas `ecomerce_db` como usuario; ese es el nombre de la base, no del login.
5. Si Workbench muestra `Unsupported server version: MySQL Community Server - GPL 8.4.8`, baja el servidor a `8.0.x` porque ese build de Workbench no esta soportando bien `8.4.8`.

En ese caso hay dos salidas:

1. Usar otro cliente compatible como MySQL Shell o DBeaver.
2. Instalar una version de MySQL Server `8.0.x` que sea mas compatible con Workbench `8.0.46`.

## Reglas importantes

- No ejecutes `v2.sql` antes de `v1.sql`.
- No saltes `v3.sql` si necesitas soporte de fotos.
- Si la app falla con validacion de esquema, revisa primero que la base ya tenga todas las tablas.
- Si el puerto `8080` esta ocupado, detén el proceso Java anterior antes de volver a arrancar.
- Si usas Workbench, evita `8.4.8` en el servidor y usa `8.0.x` para reducir errores de compatibilidad.

## Flujo minimo para una IA

1. Comprobar MySQL en `3306`.
2. Crear `ecomerce_db` si hace falta.
3. Aplicar `v1.sql`, `v2.sql` y `v3.sql` en ese orden.
4. Arrancar la app con `mvnw.cmd spring-boot:run`.
5. Confirmar que `GET /api/productos` responde.
6. Confirmar que la base se ve desde Workbench.
