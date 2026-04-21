SET NAMES utf8mb4;

-- Reset total de datos (sin tocar estructura)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE detallecompra;
TRUNCATE TABLE compra;
TRUNCATE TABLE detallecarrito;
TRUNCATE TABLE carrito;
TRUNCATE TABLE direccionenvio;
TRUNCATE TABLE producto;
TRUNCATE TABLE categoria;
TRUNCATE TABLE estado;
TRUNCATE TABLE metodopago;
TRUNCATE TABLE usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- Usuarios (password para todos: Secret123!)
INSERT INTO usuario (nombre, email, telefono, password_hash, rol) VALUES
('Sofia Molina', 'admin@selvachoco.com', '1130001000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'ADMINISTRADOR'),
('Marco Rivas', 'marco@selvachoco.com', '1130002000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'VENDEDOR'),
('Luna Herrera', 'luna@selvachoco.com', '1130003000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'VENDEDOR'),
('Camila Perez', 'camila@cliente.com', '1141001000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'COMPRADOR'),
('Diego Flores', 'diego@cliente.com', '1141002000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'COMPRADOR'),
('Valentina Ruiz', 'valentina@cliente.com', '1141003000', '$2a$10$DS.n4kk3ucs5ZgoU5Q0o5eVvqTDRUHP/dkwe3apvOIZbnD0a43dOe', 'COMPRADOR');

-- Catalogo
INSERT INTO categoria (nombre) VALUES
('Figuras de Animales'),
('Coleccion Selva'),
('Regalos y Packs'),
('Edicion Pascua');

INSERT INTO estado (nombre, descripcion) VALUES
('PENDIENTE', 'Compra creada y pendiente de pago'),
('PAGADA', 'Pago acreditado'),
('EN_PREPARACION', 'Pedido en armado en cocina'),
('ENVIADA', 'Pedido despachado'),
('ENTREGADA', 'Pedido entregado al cliente');

INSERT INTO metodopago (tipo, descripcion) VALUES
('Tarjeta de credito', 'Visa, MasterCard y Amex'),
('Tarjeta de debito', 'Debito bancario'),
('Transferencia bancaria', 'CBU o alias'),
('Mercado Pago', 'Billetera virtual'),
('Efectivo en local', 'Pago presencial al retirar');

-- Direcciones de clientes
INSERT INTO direccionenvio (id_usuario, direccion, ciudad, codigo_postal, es_principal) VALUES
(4, 'Av. Libertador 1520', 'CABA', '1425', 1),
(4, 'Amenabar 2300', 'CABA', '1428', 0),
(5, 'Calle Mitre 845', 'Rosario', '2000', 1),
(6, 'Belgrano 410', 'Cordoba', '5000', 1),
(6, 'Dean Funes 920', 'Cordoba', '5004', 0);

-- Productos con descuentos activos
INSERT INTO producto (
    id_categoria,
    id_usuario,
    nombre,
    precio,
    descripcion,
    stock,
    descuento_porcentaje,
    descuento_inicio,
    descuento_fin,
    activo
) VALUES
(1, 2, 'Conejito Clasico 120g', 9.90, 'Chocolate con leche con forma de conejo', 120, 15.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(1, 2, 'Osito Crocante 90g', 8.50, 'Osito de chocolate con cereal crocante', 150, 10.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(1, 2, 'Gatito Blanco 100g', 10.20, 'Chocolate blanco con forma de gatito', 80, 12.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(2, 3, 'Elefante Relleno Avellana 150g', 14.90, 'Figura premium rellena de crema de avellanas', 60, 18.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(2, 3, 'Leon de Chocolate Amargo 130g', 13.80, 'Chocolate 70% cacao con forma de leon', 55, 20.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(2, 3, 'Mono Caramelo 110g', 11.60, 'Chocolate con leche relleno de caramelo', 90, 14.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(4, 2, 'Huevito Conejo Pascua 75g', 7.40, 'Mini figura estacional de conejo', 200, 8.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(4, 2, 'Huevito Pollito Pascua 75g', 7.40, 'Mini figura estacional de pollito', 180, 8.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(3, 3, 'Pack Selva x6 Mini Animales', 19.90, 'Caja surtida de mini figuras de animales', 40, 22.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(3, 3, 'Caja Regalo Zoo x12 Bombones', 24.50, 'Caja premium de 12 bombones tematicos', 35, 25.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(1, 2, 'Panda Dulce de Leche 95g', 9.30, 'Figura de panda rellena de dulce de leche', 70, 11.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(1, 3, 'Tortuguita Chocolate 85g', 8.90, 'Tortuguita de chocolate con leche', 95, 9.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(2, 2, 'Jirafa Frutal 105g', 10.80, 'Chocolate con notas frutales y forma de jirafa', 75, 13.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(2, 3, 'Hipopotamo Cookies 140g', 15.20, 'Figura con trozos de galleta y cacao', 50, 17.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1),
(3, 2, 'Combo Cumple Animales x10', 29.90, 'Caja para cumpleanos con 10 figuras surtidas', 25, 20.00, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY, 1);

-- Carritos activos
INSERT INTO carrito (id_usuario, nombre) VALUES
(4, 'Carrito Camila Abril'),
(5, 'Carrito Diego Cumple'),
(6, 'Carrito Vale Oficina');

INSERT INTO detallecarrito (id_carrito, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 2, 8.42),
(1, 4, 1, 12.22),
(1, 9, 1, 15.52),
(2, 10, 1, 18.38),
(2, 11, 3, 8.28),
(2, 7, 4, 6.81),
(3, 5, 2, 11.04),
(3, 15, 1, 23.92),
(3, 12, 5, 8.10);

-- Compras historicas
INSERT INTO compra (id_carrito, id_usuario, id_estado, id_metodo_pago, id_direccion_envio, total) VALUES
(1, 4, 5, 4, 1, 44.58),
(2, 5, 4, 2, 3, 70.46),
(3, 6, 3, 3, 4, 86.50);

INSERT INTO detallecompra (id_compra, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, 8.42, 16.84),
(1, 4, 1, 12.22, 12.22),
(1, 9, 1, 15.52, 15.52),
(2, 10, 1, 18.38, 18.38),
(2, 11, 3, 8.28, 24.84),
(2, 7, 4, 6.81, 27.24),
(3, 5, 2, 11.04, 22.08),
(3, 15, 1, 23.92, 23.92),
(3, 12, 5, 8.10, 40.50);
