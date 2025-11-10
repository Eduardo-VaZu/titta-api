-- 1. Actualizando tbl_usuario
-- Renombramos 'fecha_registro' para que coincida con 'fechaCreacion' de Auditable
ALTER TABLE tbl_usuario
    RENAME COLUMN fecha_registro TO fecha_creacion;
-- Agregamos las nuevas columnas de auditoría
ALTER TABLE tbl_usuario
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- 2. Actualizando tbl_carrito
-- Renombramos 'fecha_actualizacion' para que coincida con 'fechaModificacion'
ALTER TABLE tbl_carrito
    RENAME COLUMN fecha_actualizacion TO fecha_modificacion;
-- 'fecha_creacion' ya existe, solo agregamos el resto
ALTER TABLE tbl_carrito
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- 3. Agregando columnas a tbl_producto
ALTER TABLE tbl_producto
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- 4. Agregando columnas a tbl_categoria
ALTER TABLE tbl_categoria
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- 5. Agregando columnas a tbl_sede
ALTER TABLE tbl_sede
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- 6. Agregando columnas a tbl_venta
ALTER TABLE tbl_venta
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

-- (Repite este patrón para cualquier otra entidad que hayas hecho "Auditable")