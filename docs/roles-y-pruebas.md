# Roles y pruebas manuales

## Roles del sistema

- `COMPRADOR`: puede autenticarse, consultar recursos y crear datos de compra/carro, pero no publicar productos ni administrar métodos de pago.
- `VENDEDOR`: puede publicar y actualizar productos propios.
- `ADMINISTRADOR`: puede publicar productos y también administrar métodos de pago; además puede publicar en nombre de otro usuario si el flujo de negocio lo requiere.

## Cómo se resuelve la autenticación

1. `POST /api/auth/register` crea un usuario en la tabla `Usuario` con contraseña cifrada.
2. `POST /api/auth/login` valida credenciales contra la base y devuelve un JWT.
3. El JWT se envía en `Authorization: Bearer <token>`.
4. El filtro JWT carga el usuario autenticado en el `SecurityContext`.

## Qué significa "seller/admin"

En esta entrega, "seller/admin" es una forma informal de decir:

- `seller` = usuario con rol `VENDEDOR`
- `admin` = usuario con rol `ADMINISTRADOR`

No es un endpoint especial. Es una restricción de autorización basada en rol y en el usuario autenticado.

## Pruebas manuales paso a paso

### 1. Salud del servicio

1. Hacer `GET /api/health`.
2. Esperar `200` y un body con `status: UP`.

### 2. Registro y login

1. Hacer `POST /api/auth/register` con `nombre`, `email`, `password` y `telefono`.
2. Guardar el `accessToken`.
3. Hacer `POST /api/auth/login` con el mismo `email` y `password`.
4. Verificar que el login también devuelve `accessToken`.

### 3. Consulta de usuarios

1. Usar el token en `GET /api/users`.
2. Confirmar que responde `200`.
3. Buscar el usuario recién creado por email.

### 4. Elevar rol de prueba

1. Hacer `PUT /api/users/{id}` con `rol: VENDEDOR` para un usuario de prueba vendedor.
2. Hacer `PUT /api/users/{id}` con `rol: ADMINISTRADOR` para un usuario de prueba admin.
3. Reautenticar con login para usar el token fresco si el flujo de pruebas lo requiere.

### 5. Publicación de producto

1. Con un token de `COMPRADOR`, intentar `POST /api/productos`.
2. Esperar `403`.
3. Con un token de `VENDEDOR`, hacer `POST /api/productos` con `usuarioId` igual al mismo usuario autenticado.
4. Esperar `201`.
5. Con un token de `VENDEDOR`, intentar publicar con `usuarioId` de otro vendedor.
6. Esperar `403`.
7. Con un token de `ADMINISTRADOR`, publicar producto para cualquier `usuarioId` permitido.
8. Esperar `201`.

### 6. Métodos de pago

1. Con un token de `ADMINISTRADOR`, hacer `POST /api/metodos-pago`.
2. Esperar `201`.
3. Con un token de `COMPRADOR`, intentar el mismo POST.
4. Esperar `403`.

### 7. Direcciones y carrito

1. Con un token válido, hacer `POST /api/direcciones`.
2. Esperar `201`.
3. Hacer `POST /api/carrito` con `usuarioId` del usuario autenticado.
4. Esperar `201`.
5. Verificar `GET /api/users/{id}/direcciones`.
6. Verificar `GET /api/users/{id}/carrito`.

## Casos borde que conviene probar

- Token ausente o inválido: `403` o `401` según el punto de fallo.
- `usuarioId` inexistente al crear producto: `404`.
- `descuento_fin` anterior a `descuento_inicio`: `400`.
- Crear método de pago sin ser admin: `403`.
- Publicar producto con rol `COMPRADOR`: `403`.

## Resumen práctico

Si quieres validar rápido la diferencia entre roles:

1. `COMPRADOR` puede entrar, leer y crear datos de su flujo, pero no publicar productos ni métodos de pago.
2. `VENDEDOR` puede publicar productos propios.
3. `ADMINISTRADOR` puede publicar productos y administrar métodos de pago.