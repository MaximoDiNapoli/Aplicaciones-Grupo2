-- v5: Insertar datos de prueba para DireccionEnvio

-- Primero, asegurar que exista al menos un usuario para las direcciones
INSERT INTO usuario (nombre, email, telefono, password_hash, rol)
SELECT 'Usuario Test', 'test@example.com', '1234567890', 'hash123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'test@example.com')
LIMIT 1;

-- Insertar direcciones de envío de prueba
INSERT INTO direccionenvio (id_usuario, direccion, ciudad, codigo_postal, es_principal) VALUES
(1, 'Calle Principal 123, Apartamento 4B', 'Buenos Aires', '1425', true),
(1, 'Avenida Secundaria 456, Casa 2', 'La Plata', '1900', false),
(1, 'Calle Tercera 789', 'Córdoba', '5000', false);

