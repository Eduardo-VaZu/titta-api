ALTER TABLE tbl_usuario
    RENAME COLUMN fecha_registro TO fecha_creacion;

ALTER TABLE tbl_usuario
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

ALTER TABLE tbl_carrito
    RENAME COLUMN fecha_actualizacion TO fecha_modificacion;

ALTER TABLE tbl_carrito
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

ALTER TABLE tbl_producto
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

ALTER TABLE tbl_categoria
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

ALTER TABLE tbl_sede
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;

ALTER TABLE tbl_venta
    ADD COLUMN fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN fecha_modificacion DATETIME,
    ADD COLUMN usuario_creacion VARCHAR(100),
    ADD COLUMN usuario_modificacion VARCHAR(100),
    ADD COLUMN version BIGINT;