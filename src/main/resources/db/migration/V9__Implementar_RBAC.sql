CREATE TABLE tbl_permiso (
                             id_permiso BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE tbl_rol_permiso (
                                 id_rol BIGINT NOT NULL,
                                 id_permiso BIGINT NOT NULL,
                                 PRIMARY KEY (id_rol, id_permiso),
                                 CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (id_rol) REFERENCES tbl_rol(id_rol),
                                 CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (id_permiso) REFERENCES tbl_permiso(id_permiso)
);

INSERT INTO tbl_permiso (nombre) VALUES
                                     ('GESTIONAR_PRODUCTOS'),  -- Incluye crear, editar, eliminar productos
                                     ('GESTIONAR_CATEGORIAS'), -- Incluye crear, editar, eliminar categorías
                                     ('GESTIONAR_SEDES'),      -- Incluye crear, editar, eliminar sedes
                                     ('GESTIONAR_USUARIOS'),   -- Incluye cambiar roles y estados
                                     ('AJUSTAR_INVENTARIO'),   -- Permiso operativo para empleados
                                     ('USAR_CARRITO');         -- Permiso para clientes

-- =================================================================
-- IDs asumiendo: 1=ADMINISTRADOR, 2=CLIENTE, 3=EMPLEADO
-- =================================================================

-- ROL: ADMINISTRADOR (Tiene gestión total)
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso FROM tbl_permiso
WHERE nombre IN (
                 'GESTIONAR_PRODUCTOS',
                 'GESTIONAR_CATEGORIAS',
                 'GESTIONAR_SEDES',
                 'GESTIONAR_USUARIOS',
                 'AJUSTAR_INVENTARIO'
    );

-- ROL: EMPLEADO (Solo puede ajustar stock)
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 3, id_permiso FROM tbl_permiso
WHERE nombre = 'AJUSTAR_INVENTARIO';

-- ROL: CLIENTE (Solo puede comprar)
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 2, id_permiso FROM tbl_permiso
WHERE nombre = 'USAR_CARRITO';

INSERT INTO tbl_estado_venta (nombre_estado) VALUES ('COMPLETADA'), ('PENDIENTE');
INSERT INTO tbl_metodo_pago (nombre_metodo) VALUES ('EFECTIVO'), ('TARJETA');
