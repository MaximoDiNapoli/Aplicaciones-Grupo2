-- Script unico de inicializacion para ecomerce_db

CREATE TABLE Categoria (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT
);

CREATE TABLE Estado (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT
);

CREATE TABLE MetodoPago (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE DireccionEnvio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(20),
    es_principal BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE Producto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_categoria INT,
    id_usuario INT NULL,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    stock INT DEFAULT 0,
    foto LONGBLOB NULL,
    descuento_porcentaje DECIMAL(5,2) NULL,
    descuento_inicio DATETIME NULL,
    descuento_fin DATETIME NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES Categoria(id) ON DELETE SET NULL,
    CONSTRAINT fk_producto_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id) ON DELETE SET NULL
);

CREATE TABLE Carrito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    nombre VARCHAR(255) NOT NULL DEFAULT 'Carrito',
    descripcion TEXT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE DetalleCarrito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_carrito INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (id_carrito) REFERENCES Carrito(id) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES Producto(id) ON DELETE CASCADE
);

CREATE TABLE Compra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_carrito INT,
    id_usuario INT NOT NULL,
    id_estado INT,
    id_metodo_pago INT,
    id_direccion_envio INT,
    total DECIMAL(10, 2) NOT NULL,
    fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_carrito) REFERENCES Carrito(id) ON DELETE SET NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id),
    FOREIGN KEY (id_estado) REFERENCES Estado(id),
    FOREIGN KEY (id_metodo_pago) REFERENCES MetodoPago(id),
    FOREIGN KEY (id_direccion_envio) REFERENCES DireccionEnvio(id)
);

CREATE TABLE DetalleCompra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_compra) REFERENCES Compra(id) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES Producto(id)
);

INSERT INTO MetodoPago (tipo, descripcion) VALUES
('Tarjeta de Crédito', 'Pago con tarjeta de crédito Visa, Mastercard, etc.'),
('Tarjeta de Débito', 'Pago con tarjeta de débito'),
('Transferencia Bancaria', 'Pago mediante transferencia bancaria'),
('PayPal', 'Pago a través de PayPal'),
('Efectivo', 'Pago en efectivo al momento de la entrega');

INSERT INTO Usuario (nombre, email, telefono, password_hash, rol)
SELECT 'Usuario Test', 'test@example.com', '1234567890', 'hash123', 'COMPRADOR'
WHERE NOT EXISTS (
    SELECT 1
    FROM Usuario
    WHERE email = 'test@example.com'
)
LIMIT 1;

SET @test_user_id := (
    SELECT id
    FROM Usuario
    WHERE email = 'test@example.com'
    ORDER BY id
    LIMIT 1
);

INSERT INTO DireccionEnvio (id_usuario, direccion, ciudad, codigo_postal, es_principal)
SELECT @test_user_id, 'Calle Principal 123, Apartamento 4B', 'Buenos Aires', '1425', true
WHERE @test_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM DireccionEnvio
      WHERE id_usuario = @test_user_id
        AND direccion = 'Calle Principal 123, Apartamento 4B'
  )
LIMIT 1;

INSERT INTO DireccionEnvio (id_usuario, direccion, ciudad, codigo_postal, es_principal)
SELECT @test_user_id, 'Avenida Secundaria 456, Casa 2', 'La Plata', '1900', false
WHERE @test_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM DireccionEnvio
      WHERE id_usuario = @test_user_id
        AND direccion = 'Avenida Secundaria 456, Casa 2'
  )
LIMIT 1;