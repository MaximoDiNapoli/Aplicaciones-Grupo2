-- v6: reforzar relacion Usuario-Carrito segun DER

SET @schema_name := DATABASE();

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @schema_name
      AND table_name = 'Carrito'
      AND column_name = 'id_usuario'
);

SET @null_count := (
    SELECT IFNULL(SUM(CASE WHEN id_usuario IS NULL THEN 1 ELSE 0 END), 0)
    FROM Carrito
);

SET @sql := IF(
    @column_exists = 1 AND @null_count = 0,
    'ALTER TABLE Carrito MODIFY COLUMN id_usuario INT NOT NULL',
    'SELECT ''No se aplico NOT NULL en Carrito.id_usuario (columna faltante o hay filas con NULL)'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
