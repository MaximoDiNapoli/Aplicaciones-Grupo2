-- v4: ajustar esquema para API Carrito/DetalleCarrito

SET @schema_name := DATABASE();

-- Carrito: permitir uso sin usuario obligatorio y agregar campos de negocio
SET @sql := 'ALTER TABLE Carrito MODIFY COLUMN id_usuario INT NULL';
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Carrito'
      AND column_name = 'nombre'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Carrito ADD COLUMN nombre VARCHAR(255) NOT NULL DEFAULT ''Carrito''',
    'SELECT ''Carrito.nombre ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Carrito'
      AND column_name = 'descripcion'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Carrito ADD COLUMN descripcion TEXT NULL',
    'SELECT ''Carrito.descripcion ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- DetalleCarrito: agregar precio_unitario requerido por la entidad
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'DetalleCarrito'
      AND column_name = 'precio_unitario'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE DetalleCarrito ADD COLUMN precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00',
    'SELECT ''DetalleCarrito.precio_unitario ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
