-- v5: descuentos de producto

SET @schema_name := DATABASE();

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'descuento_porcentaje'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Producto ADD COLUMN descuento_porcentaje DECIMAL(5,2) NULL',
    'SELECT ''Producto.descuento_porcentaje ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'descuento_inicio'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Producto ADD COLUMN descuento_inicio DATETIME NULL',
    'SELECT ''Producto.descuento_inicio ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'descuento_fin'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE Producto ADD COLUMN descuento_fin DATETIME NULL',
    'SELECT ''Producto.descuento_fin ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
