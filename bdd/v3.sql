-- v3: agregar foto al producto sin tocar v1/v2

SET @schema_name := DATABASE();

SET @foto_column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'foto'
);

SET @sql := IF(
    @foto_column_exists = 0,
    'ALTER TABLE Producto ADD COLUMN foto LONGBLOB NULL',
    'SELECT ''Producto.foto ya existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @imagen_column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Producto'
      AND column_name = 'imagen_url'
);

SET @sql := IF(
    @imagen_column_exists = 1,
    'ALTER TABLE Producto DROP COLUMN imagen_url',
    'SELECT ''Producto.imagen_url no existe'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;