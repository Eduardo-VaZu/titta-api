-- =================================================================
-- 1. TABLAS SIN DEPENDENCIAS EXTERNAS
-- =================================================================

-- Entidad: Rol
CREATE TABLE tbl_rol (
                         id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre_rol ENUM('ADMINISTRADOR', 'CLIENTE', 'EMPLEADO') NOT NULL UNIQUE
);

-- Entidad: Categoria
CREATE TABLE tbl_categoria (
                               id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
                               nombre_categoria VARCHAR(50) NOT NULL UNIQUE
);

-- Entidad: Direccion
CREATE TABLE tbl_direccion (
                               id_direccion BIGINT AUTO_INCREMENT PRIMARY KEY,
                               calle VARCHAR(255),
                               numero_exterior VARCHAR(20),
                               codigo_postal VARCHAR(20),
                               ciudad VARCHAR(255) NOT NULL,
                               estado_provincial VARCHAR(255) NOT NULL
);

-- Entidad: MetodoPago
CREATE TABLE tbl_metodo_pago (
                                 id_metodo_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 nombre_metodo VARCHAR(50) NOT NULL UNIQUE
);

-- Entidad: EstadoVenta
CREATE TABLE tbl_estado_venta (
                                  id_estado_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  nombre_estado VARCHAR(50) NOT NULL UNIQUE
);

-- =================================================================
-- 2. TABLAS CON DEPENDENCIAS
-- =================================================================

-- Entidad: Usuario (depende de Rol)
CREATE TABLE tbl_usuario (
                             id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(100) NOT NULL,
                             apellido_paterno VARCHAR(100) NOT NULL,
                             apellido_materno VARCHAR(100) NOT NULL,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             fecha_registro DATE NOT NULL,
                             estado_usuario BOOLEAN NOT NULL,
                             id_rol BIGINT NOT NULL,
                             CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES tbl_rol(id_rol)
);

-- Entidad: Sede (depende de Direccion)
CREATE TABLE tbl_sede (
                          id_sede BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre_sede VARCHAR(100) NOT NULL UNIQUE,
                          telefono VARCHAR(20) UNIQUE,
                          estado BOOLEAN NOT NULL,
                          id_direccion BIGINT NOT NULL,
                          CONSTRAINT fk_sede_direccion FOREIGN KEY (id_direccion) REFERENCES tbl_direccion(id_direccion)
);

-- Entidad: Producto (depende de Categoria)
CREATE TABLE tbl_producto (
                              id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
                              nombre_producto VARCHAR(100) NOT NULL,
                              sku VARCHAR(50) NOT NULL UNIQUE,
                              descripcion TEXT,
                              precio DECIMAL(10, 2) NOT NULL,
                              estado_producto BOOLEAN NOT NULL,
                              id_categoria BIGINT NOT NULL,
                              CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES tbl_categoria(id_categoria)
);

-- Entidad: Carrito (depende de Usuario)
CREATE TABLE tbl_carrito (
                             id_carrito BIGINT AUTO_INCREMENT PRIMARY KEY,
                             id_usuario BIGINT NOT NULL,
                             fecha_creacion DATETIME NOT NULL,
                             fecha_actualizacion DATETIME NOT NULL,
                             estado VARCHAR(50) NOT NULL,
                             CONSTRAINT fk_carrito_usuario FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario)
);


-- =================================================================
-- 3. TABLAS DE RELACIONES Y OTRAS DEPENDENCIAS
-- =================================================================

-- Entidad: CredencialTradicional (depende de Usuario)
CREATE TABLE tbl_credencial_tradicional (
                                            id_usuario BIGINT PRIMARY KEY,
                                            password_hash VARCHAR(255) NOT NULL,
                                            CONSTRAINT fk_credencial_usuario FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario)
);

-- Entidad: ImagenProducto (depende de Producto)
CREATE TABLE tbl_imagen_producto (
                                     id_imagen BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     id_producto BIGINT NOT NULL,
                                     imagen_url VARCHAR(255) NOT NULL,
                                     CONSTRAINT fk_imagen_producto FOREIGN KEY (id_producto) REFERENCES tbl_producto(id_producto)
);

-- Entidad: HorarioOperacionSede (depende de Sede)
CREATE TABLE tbl_horario_operacion_sede (
                                            id_horario BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            id_sede BIGINT NOT NULL,
                                            dia_semana VARCHAR(15) NOT NULL,
                                            hora_apertura TIME,
                                            hora_cierre TIME,
                                            CONSTRAINT fk_horario_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede)
);

-- Entidad: Venta (depende de Usuario, MetodoPago, EstadoVenta, Sede, Carrito)
CREATE TABLE tbl_venta (
                           id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
                           id_usuario BIGINT NOT NULL,
                           fecha_venta DATETIME NOT NULL,
                           total DECIMAL(10, 2) NOT NULL,
                           id_metodo_pago BIGINT NOT NULL,
                           id_estado_venta BIGINT NOT NULL,
                           id_sede BIGINT NOT NULL,
                           id_carrito BIGINT NOT NULL,
                           CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario),
                           CONSTRAINT fk_venta_metodo_pago FOREIGN KEY (id_metodo_pago) REFERENCES tbl_metodo_pago(id_metodo_pago),
                           CONSTRAINT fk_venta_estado_venta FOREIGN KEY (id_estado_venta) REFERENCES tbl_estado_venta(id_estado_venta),
                           CONSTRAINT fk_venta_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede),
                           CONSTRAINT fk_venta_carrito FOREIGN KEY (id_carrito) REFERENCES tbl_carrito(id_carrito)
);

-- Entidad: MovimientoInventario (depende de Producto, Sede)
CREATE TABLE tbl_movimiento_inventario (
                                           id_movimiento_inventario BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           id_producto BIGINT NOT NULL,
                                           id_sede BIGINT NOT NULL,
                                           tipo_movimiento VARCHAR(20) NOT NULL,
                                           cantidad INT NOT NULL,
                                           fecha_movimiento DATE NOT NULL,
                                           razon TEXT,
                                           CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES tbl_producto(id_producto),
                                           CONSTRAINT fk_movimiento_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede)
);

-- =================================================================
-- 4. TABLAS CON CLAVES PRIMARIAS COMPUESTAS
-- =================================================================

-- Entidad: UsuarioSede (relación entre Usuario y Sede)
CREATE TABLE tbl_usuario_sede (
                                  id_usuario BIGINT NOT NULL,
                                  id_sede BIGINT NOT NULL,
                                  rol_en_sede VARCHAR(50),
                                  PRIMARY KEY (id_usuario, id_sede),
                                  CONSTRAINT fk_usuariosede_usuario FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario),
                                  CONSTRAINT fk_usuariosede_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede)
);

-- Entidad: StockSede (relación entre Producto y Sede)
CREATE TABLE tbl_stock_sede (
                                id_producto BIGINT NOT NULL,
                                id_sede BIGINT NOT NULL,
                                cantidad INT NOT NULL,
                                PRIMARY KEY (id_producto, id_sede),
                                CONSTRAINT fk_stocksede_producto FOREIGN KEY (id_producto) REFERENCES tbl_producto(id_producto),
                                CONSTRAINT fk_stocksede_sede FOREIGN KEY (id_sede) REFERENCES tbl_sede(id_sede)
);

-- Entidad: ItemCarrito (relación entre Carrito y Producto)
CREATE TABLE tbl_items_carrito (
                                   id_carrito BIGINT NOT NULL,
                                   id_producto BIGINT NOT NULL,
                                   cantidad INT NOT NULL,
                                   precio_unitario DECIMAL(10, 2) NOT NULL,
                                   PRIMARY KEY (id_carrito, id_producto),
                                   CONSTRAINT fk_itemcarrito_carrito FOREIGN KEY (id_carrito) REFERENCES tbl_carrito(id_carrito),
                                   CONSTRAINT fk_itemcarrito_producto FOREIGN KEY (id_producto) REFERENCES tbl_producto(id_producto)
);

-- Entidad: DetalleVenta (relación entre Venta y Producto)
CREATE TABLE tbl_detalle_venta (
                                   id_venta BIGINT NOT NULL,
                                   id_producto BIGINT NOT NULL,
                                   cantidad INT NOT NULL,
                                   precio_unitario DECIMAL(10, 2) NOT NULL,
                                   PRIMARY KEY (id_venta, id_producto),
                                   CONSTRAINT fk_detalleventa_venta FOREIGN KEY (id_venta) REFERENCES tbl_venta(id_venta),
                                   CONSTRAINT fk_detalleventa_producto FOREIGN KEY (id_producto) REFERENCES tbl_producto(id_producto)
);