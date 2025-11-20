ALTER TABLE tbl_carrito
    ADD COLUMN id_sede BIGINT NULL,
    ADD CONSTRAINT fk_carrito_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede);

ALTER TABLE tbl_carrito
    MODIFY COLUMN estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO';