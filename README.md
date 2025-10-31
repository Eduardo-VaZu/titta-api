# 🛍️ Titta API

API REST para la gestión de productos, inventario, ventas y usuarios del sistema Titta.

## 📋 Descripción

Titta API es una aplicación backend desarrollada con Spring Boot que proporciona servicios para:
- Gestión de productos y categorías
- Control de inventario por sedes
- Sistema de autenticación y autorización con JWT
- Administración de ventas y carritos de compra
- Gestión de usuarios y roles

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.3.1**
- **Spring Security** con JWT (Auth0)
- **Spring Data JPA**
- **MySQL** (base de datos)
- **Flyway** (migraciones)
- **SpringDoc OpenAPI** (documentación)
- **Lombok**
- **Maven**

## 📦 Requisitos Previos

- JDK 21 o superior
- Maven 3.6+
- MySQL 8.0+
- Variables de entorno configuradas (ver sección de configuración)

## ⚙️ Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Eduardo-VaZu/titta-api.git
   cd titta-api
   ```

2. **Configurar variables de entorno:**
   
   Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/titta_db
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_contraseña
   JWT_SECRET_KEY=tu_clave_secreta_jwt
   ```

3. **Crear la base de datos:**
   ```sql
   CREATE DATABASE titta_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **Compilar el proyecto:**
   ```bash
   mvn clean install
   ```

## 🏃 Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación API

Una vez ejecutada la aplicación, la documentación interactiva de Swagger estará disponible en:

```
http://localhost:8080/swagger-ui.html
```

## 🔐 Endpoints Principales

### Autenticación
- `POST /api/v1/auth/register` - Registrar nuevo usuario
- `POST /api/v1/auth/login` - Iniciar sesión

### Productos
- `GET /api/v1/productos` - Obtener todos los productos
- `GET /api/v1/productos/{id}` - Obtener producto por ID
- `POST /api/v1/productos` - Crear producto (requiere rol ADMINISTRADOR)

### Categorías
- `GET /api/v1/categorias` - Obtener todas las categorías
- `POST /api/v1/categorias` - Crear categoría

### Sedes
- `GET /api/v1/sedes` - Obtener todas las sedes
- `GET /api/v1/sedes/{id}` - Obtener sede por ID
- `POST /api/v1/sedes` - Crear sede (requiere rol ADMINISTRADOR)

## 🗂️ Estructura del Proyecto

```
src/main/java/com/titta/api/
├── config/          # Configuraciones (Security, JWT)
├── controller/      # Controladores REST
├── dto/             # Data Transfer Objects
├── exception/       # Manejo de excepciones
├── mapper/          # Mappers de entidades a DTOs
├── model/           # Entidades JPA
├── repository/      # Repositorios JPA
├── service/         # Lógica de negocio
└── util/            # Utilidades (JWT, etc.)
```

## 🔄 Migraciones de Base de Datos

Las migraciones se gestionan con Flyway y se encuentran en:
```
src/main/resources/db/migration/
```

## 🧪 Pruebas

Ejecutar las pruebas unitarias:
```bash
mvn test
```

## 👥 Roles del Sistema

- **ADMINISTRADOR** - Acceso completo al sistema
- **VENDEDOR** - Gestión de ventas e inventario
- **CLIENTE** - Acceso a productos y realización de compras

## 📝 Licencia

Este proyecto es privado y está en desarrollo.

## 👨‍💻 Autor

Eduardo Vázquez - [GitHub](https://github.com/Eduardo-VaZu)

---

⚡ Desarrollado con Spring Boot
