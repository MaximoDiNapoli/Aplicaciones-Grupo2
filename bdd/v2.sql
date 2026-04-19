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
