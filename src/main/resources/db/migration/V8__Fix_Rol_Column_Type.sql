-- -- =================================================================
-- -- 1. MODIFICACION DE TABLA TBL_ROL
-- -- =================================================================
--
-- Entidad: Rol
ALTER TABLE tbl_rol
    MODIFY COLUMN nombre_rol VARCHAR(50) NOT NULL UNIQUE;