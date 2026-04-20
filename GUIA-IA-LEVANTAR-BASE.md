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
   - `bdd/v4.sql`
4. Arranca la aplicacion con Maven.

Ejemplo en PowerShell:

```powershell
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot -e "CREATE DATABASE IF NOT EXISTS ecomerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db < .\bdd\v1.sql
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db < .\bdd\v2.sql
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db < .\bdd\v3.sql
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --host=127.0.0.1 --port=3306 -uroot ecomerce_db < .\bdd\v4.sql
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
- `bdd/v3.sql`: agrega el campo `foto` a `Producto` y elimina `imagen_url`.
- `bdd/v4.sql`: ajusta `Carrito` y `DetalleCarrito` para los endpoints de carrito.

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
- No saltes `v4.sql` si usas los endpoints de carrito y detalle de carrito.
- Si la app falla con validacion de esquema, revisa primero que la base ya tenga todas las tablas.
- Si el puerto `8080` esta ocupado, detén el proceso Java anterior antes de volver a arrancar.
- Si usas Workbench, evita `8.4.8` en el servidor y usa `8.0.x` para reducir errores de compatibilidad.

## Flujo minimo para una IA

1. Comprobar MySQL en `3306`.
2. Crear `ecomerce_db` si hace falta.
3. Aplicar `v1.sql`, `v2.sql`, `v3.sql` y `v4.sql` en ese orden.
4. Arrancar la app con `mvnw.cmd spring-boot:run`.
5. Confirmar que `GET /api/productos` responde.
6. Confirmar que la base se ve desde Workbench.

## Informe de pruebas

Prueba ejecutada contra `http://127.0.0.1:8080` con MySQL 8.0.45 en `3306`.

### Salud

- `GET /api/health` -> `200`
- Respuesta: `{"status":"UP"}`

### Categorias

- `POST /api/categorias` -> `201` para tres altas.
- `GET /api/categorias` -> `200`, devolvio `2` registros al final de la corrida.
- `GET /api/categorias/1` -> `200`.
- `PUT /api/categorias/1` -> `200`.
- `DELETE /api/categorias/3` -> `204`.
- Estado final en base: `Electronica Actualizada` y `Hogar`.

### Estados

- `POST /api/estados` -> `201` para dos altas.
- `GET /api/estados` -> `200`, devolvio `2` registros al final.
- `PUT /api/estados/1` -> `200`.
- `DELETE /api/estados/2` -> `204`.
- Estado final en base: `Pendiente actualizado` y `Eliminado`.

### Productos

- `POST /api/productos` JSON -> `201` para `Teclado Mecanico`.
- `POST /api/productos` JSON -> `201` para `Mouse Gamer`.
- `POST /api/productos` multipart con imagen -> `201` para `Camara Web`.
- `GET /api/productos/1` -> `200`.
- `PUT /api/productos/1` JSON -> `200`.
- `PUT /api/productos/3` multipart con nueva imagen -> `200`.
- `GET /api/productos` -> `200`, devolvio `3` registros.
- Filtros probados:
   - `?categoria=1` -> `2` resultados.
   - `?usuario=1` -> `3` resultados.
   - `?search=teclado` -> `1` resultado.
   - `?minPrecio=10&maxPrecio=40` -> `2` resultados.
- `DELETE /api/productos/2` -> `204`.
- Persistencia verificada: el producto 3 quedo con foto guardada (`foto_len = 23`).

### Carrito

- `POST /api/carrito` -> `201`.
- `GET /api/carrito/1` -> `200`.
- `PUT /api/carrito/1` -> `200`.
- `POST /api/carrito/items` -> `201`.
- `GET /api/carrito/1/items` -> `200`.
- `PUT /api/carrito/1/items/1` -> `200`.
- `DELETE /api/carrito/items/1` -> `204`.
- `DELETE /api/carrito/1` -> `204`.

### Compra

- `POST /api/compras/2` -> `201`.
- `GET /api/compra/1` -> `200`.
- `GET /api/compras/1` -> `200`, devolvio `1` compra.
- `GET /api/compras/1/detalle` -> `200`, devolvio `1` item.
- `PUT /api/compra/1` -> `200`.
- Persistencia verificada:
   - `Compra.total = 110.00`
   - `DetalleCompra.subtotal = 110.00`

### Verificacion directa en base

- `Categoria` -> `2` filas activas.
- `Estado` -> `2` filas.
- `Producto` -> `3` filas, con una foto almacenada.
- `Carrito` -> `1` carrito final activo.
- `DetalleCarrito` -> `1` fila antes de borrar el carrito de pruebas.
- `Compra` -> `1` fila.
- `DetalleCompra` -> `1` fila.
- `Usuario`, `MetodoPago`, `DireccionEnvio` -> `1` fila cada una, usadas para la compra.

## Flujo completo de la app

1. Levantar MySQL 8.0.45 y aplicar `v1.sql`, `v2.sql`, `v3.sql` y `v4.sql`.
2. Arrancar la API Spring Boot con `mvnw.cmd spring-boot:run`.
3. Validar salud con `GET /api/health`.
4. Crear categorias y estados necesarios para catalogo y operaciones.
5. Crear productos:
    - por JSON cuando no hay imagen,
    - por multipart cuando se carga o actualiza foto.
6. Consultar productos con filtros por categoria, usuario, busqueda y precio.
7. Crear un carrito con sus items.
8. Actualizar o borrar items del carrito si cambia la seleccion.
9. Generar la compra desde un carrito con usuario, metodo de pago y direccion.
10. Consultar la compra creada y su detalle.
11. Verificar en MySQL que los `GET` devuelven lo mismo que quedo guardado.

## Orden sugerido de uso real

1. Categorias.
2. Estados.
3. Productos.
4. Carrito.
5. Compra.
6. Consulta final en base y en Workbench.
