-- v2: Relacion Producto -> Usuario + consolidacion de tablas
-- Canonico: tabla Producto (con P mayuscula)

SET @schema_name := DATABASE();

-- 1) Asegurar columna id_usuario en tabla canonica
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'id_usuario'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Producto ADD COLUMN id_usuario INT NULL',
    'SELECT ''Producto.id_usuario ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Asegurar FK en tabla canonica
SET @fk_exists := (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND constraint_type = 'FOREIGN KEY'
      AND constraint_name = 'fk_producto_usuario'
);

SET @sql := IF(
    @fk_exists = 0,
    'ALTER TABLE Producto ADD CONSTRAINT fk_producto_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id) ON DELETE SET NULL',
    'SELECT ''fk_producto_usuario ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) Si existe tabla duplicada "producto", migrar datos faltantes y eliminarla
SET @producto_lower_exists := (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = @schema_name
      AND table_name = 'producto'
);

SET @sql := IF(
    @producto_lower_exists = 1,
    'INSERT INTO Producto (id, id_categoria, nombre, precio, descripcion, stock, imagen_url, activo, created_at, id_usuario)
     SELECT p.id,
            CASE WHEN cat.id IS NULL THEN NULL ELSE p.id_categoria END,
            p.nombre,
            p.precio,
            p.descripcion,
            p.stock,
            p.imagen_url,
            p.activo,
            p.created_at,
            CASE WHEN usr.id IS NULL THEN NULL ELSE p.id_usuario END
     FROM producto p
     LEFT JOIN Producto c ON c.id = p.id
     LEFT JOIN Categoria cat ON cat.id = p.id_categoria
     LEFT JOIN Usuario usr ON usr.id = p.id_usuario
     WHERE c.id IS NULL',
    'SELECT ''tabla producto no existe, nada para migrar'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
    @producto_lower_exists = 1,
    'DROP TABLE producto',
    'SELECT ''tabla producto no existe, nada para borrar'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
