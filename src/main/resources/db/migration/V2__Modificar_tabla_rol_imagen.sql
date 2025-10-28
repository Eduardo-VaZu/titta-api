
-- =================================================================
-- 1. MODIFICACION DE TABLA TBL_ROL
-- =================================================================

-- Entidad: Rol
ALTER TABLE tbl_rol
    MODIFY COLUMN nombre_rol VARCHAR(50) NOT NULL UNIQUE;

-- =================================================================
-- 2. MODIFICACION DE TABLA TBL_IMAGEN_PRODUCTO
-- =================================================================

-- Entidad: ImagenProducto
ALTER TABLE tbl_imagen_producto
    ADD alt_text VARCHAR(255);
