-- Insertar permiso de reportes
INSERT INTO tbl_permiso (nombre)
VALUES ('VER_REPORTES');

-- Asignar permiso al ROL: ADMINISTRADOR (ID 1)
INSERT INTO tbl_rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso
FROM tbl_permiso
WHERE nombre = 'VER_REPORTES';