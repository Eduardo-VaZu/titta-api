ALTER TABLE tbl_categoria
    ADD COLUMN estado BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE tbl_categoria
SET estado = TRUE
WHERE estado IS NULL;