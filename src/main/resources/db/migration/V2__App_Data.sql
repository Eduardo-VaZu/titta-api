-- ==================================================================================
-- V2: DATOS SEMILLA COMPLETOS (CATÁLOGOS + DATOS DE PRUEBA)
-- ==================================================================================

-- 1. CONFIGURACIÓN BÁSICA (CATÁLOGOS)
-- ----------------------------------------------------------------------------------

-- Roles
INSERT INTO tbl_rol (id_rol, nombre_rol)
VALUES (1, 'ADMINISTRADOR'),
       (2, 'CLIENTE'),
       (3, 'EMPLEADO');

-- Métodos de Pago
INSERT INTO tbl_metodo_pago (id_metodo_pago, nombre_metodo)
VALUES (1, 'EFECTIVO'),
       (2, 'TARJETA');

-- Estados de Venta
INSERT INTO tbl_estado_venta (id_estado_venta, nombre_estado)
VALUES (1, 'COMPLETADA'),
       (2, 'PENDIENTE'),
       (3, 'CANCELADA');

-- Categorías
INSERT INTO tbl_categoria (id_categoria, nombre_categoria, estado, fecha_creacion, usuario_creacion)
VALUES (1, 'Electrónica', true, NOW(), 'SYSTEM'),
       (2, 'Hogar', true, NOW(), 'SYSTEM'),
       (3, 'Ropa', true, NOW(), 'SYSTEM');

-- Permisos (RBAC)
INSERT INTO tbl_permiso (nombre)
VALUES ('GESTIONAR_PRODUCTOS'),
       ('GESTIONAR_CATEGORIAS'),
       ('GESTIONAR_SEDES'),
       ('GESTIONAR_USUARIOS'),
       ('AJUSTAR_INVENTARIO'),
       ('USAR_CARRITO'),
       ('VER_REPORTES');

-- Asignación de Permisos
-- Admin (Rol 1): Todo
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso
FROM tbl_permiso;
-- Cliente (Rol 2): Carrito
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 2, id_permiso
FROM tbl_permiso
WHERE nombre = 'USAR_CARRITO';
-- Empleado (Rol 3): Inventario
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 3, id_permiso
FROM tbl_permiso
WHERE nombre = 'AJUSTAR_INVENTARIO';
