-- =================================================================
-- 7. ACTUALIZAR TBL_CARRITO
-- =================================================================

-- Añadimos la columna id_sede para vincular el carrito a una tienda
ALTER TABLE tbl_carrito
    ADD COLUMN id_sede BIGINT NULL,
    ADD CONSTRAINT fk_carrito_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede);

-- Actualizamos el estado para que 'ACTIVO' sea el valor por defecto
ALTER TABLE tbl_carrito
    MODIFY COLUMN estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO';