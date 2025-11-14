-- V7__Add_Estado_To_Categoria.sql
ALTER TABLE tbl_categoria
    ADD COLUMN estado BOOLEAN NOT NULL DEFAULT TRUE;

-- Opcional: Asegurarse de que todos los registros existentes queden como activos
UPDATE tbl_categoria
SET estado = TRUE
WHERE estado IS NULL;