# Evidencias de entrega

Guardar en esta carpeta las capturas pedidas para la presentacion/entrega.

## Nombres sugeridos de archivos

1. 01-workbench-tablas.png
2. 02-workbench-datos-usuario-producto-compra.png
3. 03-login-jwt.png
4. 04-endpoint-protegido-con-token-200.png
5. 05-endpoint-sin-token-401-403.png
6. 06-endpoint-rol-insuficiente-403.png

## Que debe verse en cada captura

### 01-workbench-tablas.png

- Schema ecomerce_db.
- Listado de tablas: usuario, categoria, producto, carrito, detallecarrito, compra, detallecompra, direccionenvio, estado, metodopago.

### 02-workbench-datos-usuario-producto-compra.png

- Al menos una fila en usuario, producto, compra y detallecompra.
- Si es posible, incluir tambien carrito y detallecarrito en la misma captura o en una segunda captura adicional.

### 03-login-jwt.png

- Request a POST /api/auth/login.
- Response 200 con accessToken.

### 04-endpoint-protegido-con-token-200.png

- Request autenticado (Bearer token), por ejemplo GET /api/users.
- Response 200.

### 05-endpoint-sin-token-401-403.png

- Mismo endpoint protegido sin token.
- Response 401 o 403 (segun configuracion del filtro).

### 06-endpoint-rol-insuficiente-403.png

- Request con token de VENDEDOR a POST /api/metodos-pago.
- Response 403.
