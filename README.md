# 🛍️ Titta API

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Private-red)]()

API REST robusta y escalable para la gestión integral del sistema de ecommerce Titta, incluyendo productos, inventario multisede, ventas y autenticación segura.

## 📋 Descripción

**Titta API** es una aplicación backend empresarial desarrollada con Spring Boot que proporciona una arquitectura completa para:

- 🏪 **Gestión de productos y categorías** con imágenes múltiples
- 📦 **Control de inventario distribuido** por múltiples sedes
- 🔐 **Sistema de autenticación y autorización** con JWT y Spring Security
- 🛒 **Administración de ventas y carritos de compra** con estados transaccionales
- 👥 **Gestión de usuarios y roles** (ADMINISTRADOR, VENDEDOR, CLIENTE)
- 🏢 **Sistema multisede** con horarios de operación y stock independiente
- 💳 **Múltiples métodos de pago** y direcciones de entrega
- 📊 **Trazabilidad de movimientos** de inventario

## 🚀 Stack Tecnológico

### Backend
- **Java 21** - Última versión LTS con mejoras de rendimiento
- **Spring Boot 3.3.1** - Framework principal
- **Spring Security 6** - Seguridad y autenticación
- **Spring Data JPA** - Persistencia de datos con Hibernate
- **Spring Validation** - Validación de datos

### Base de Datos
- **MySQL 8.0+** - Sistema de gestión de base de datos relacional
- **Flyway** - Gestión de migraciones y versionado de BD

### Seguridad
- **Auth0 JWT** (4.4.0) - Generación y validación de tokens JWT
- **BCrypt** - Encriptación de contraseñas

### Documentación
- **SpringDoc OpenAPI 3** (2.3.0) - Documentación interactiva con Swagger UI

### Utilidades
- **Lombok** - Reducción de código boilerplate
- **Spring DotEnv** (3.0.0) - Gestión de variables de entorno
- **Maven** - Gestión de dependencias y build

## 📦 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

| Herramienta | Versión Mínima | Propósito |
|-------------|----------------|------------|
| JDK | 21 | Entorno de ejecución Java |
| Maven | 3.8+ | Gestión de dependencias y build |
| MySQL | 8.0+ | Base de datos relacional |
| Git | 2.0+ | Control de versiones |

### Verificar instalaciones

```bash
java -version    # Debe mostrar versión 21+
mvn -version     # Debe mostrar Maven 3.8+
mysql --version  # Debe mostrar MySQL 8.0+
```

## ⚙️ Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Eduardo-VaZu/titta-api.git
   cd titta-api
   ```

2. **Configurar variables de entorno:**
   
   Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:
   
   ```properties
   # Configuración de Base de Datos
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=db_titta
   DB_USERNAME=root
   DB_PASSWORD=tu_contraseña_segura
   DB_DRIVER=com.mysql.cj.jdbc.Driver
   
   # Configuración de JWT
   JWT_KEY_SECRET=tu_clave_secreta_jwt_minimo_256_bits
   JWT_USER_GENERATOR=TittaAPI
   ```
   
   > ⚠️ **Importante**: Nunca subas el archivo `.env` al repositorio. Ya está incluido en `.gitignore`.

3. **Crear la base de datos:**
   ```sql
   CREATE DATABASE titta_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **Instalar dependencias:**
   ```bash
   mvn clean install
   ```
   
   Esto descargará todas las dependencias y compilará el proyecto.

5. **Flyway ejecutará automáticamente las migraciones** al iniciar la aplicación por primera vez.

## 🏃 Ejecutar la Aplicación

### Modo Desarrollo

```bash
mvn spring-boot:run
```

### Modo Producción

```bash
# Compilar JAR
mvn clean package -DskipTests

# Ejecutar JAR
java -jar target/titta-api-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **`http://localhost:8080`**

### Health Check

Verifica que la aplicación esté funcionando correctamente:

```bash
curl http://localhost:8080/actuator/health
```

## 📚 Documentación API

Una vez ejecutada la aplicación, puedes acceder a la documentación interactiva:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Características de la Documentación

- 📖 **Explorador interactivo** de todos los endpoints
- 🧪 **Pruebas en vivo** desde el navegador
- 📝 **Esquemas de datos** con ejemplos
- 🔐 **Autenticación JWT** integrada en el UI

## 🔐 Endpoints Principales

### 🔑 Autenticación (`/api/v1/auth`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|--------------|
| POST | `/register` | Registrar nuevo usuario | No |
| POST | `/login` | Iniciar sesión y obtener JWT | No |

**Ejemplo de Login:**
```json
POST /api/v1/auth/login
{
  "email": "usuario@example.com",
  "password": "contraseña123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "email": "usuario@example.com",
    "rol": "CLIENTE"
  }
}
```

### 🏪 Productos (`/api/v1/productos`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/` | Listar todos los productos | Público |
| GET | `/{id}` | Obtener producto por ID | Público |
| POST | `/` | Crear nuevo producto | ADMINISTRADOR |
| PUT | `/{id}` | Actualizar producto | ADMINISTRADOR |
| DELETE | `/{id}` | Eliminar producto | ADMINISTRADOR |

### 📦 Categorías (`/api/v1/categorias`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/` | Listar todas las categorías | Público |
| GET | `/{id}` | Obtener categoría por ID | Público |
| POST | `/` | Crear nueva categoría | ADMINISTRADOR |
| PUT | `/{id}` | Actualizar categoría | ADMINISTRADOR |
| DELETE | `/{id}` | Eliminar categoría | ADMINISTRADOR |

### 🏢 Sedes (`/api/v1/sedes`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/` | Listar todas las sedes | Público |
| GET | `/{id}` | Obtener sede por ID | Público |
| POST | `/` | Crear nueva sede | ADMINISTRADOR |
| PUT | `/{id}` | Actualizar sede | ADMINISTRADOR |
| DELETE | `/{id}` | Eliminar sede | ADMINISTRADOR |

### 🔒 Autenticación en Requests

Para endpoints protegidos, incluye el token JWT en el header:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🗂️ Estructura del Proyecto

```
src/main/java/com/titta/api/
├── 📋 TittaApiApplication.java          # Clase principal
│
├── 🔧 config/                           # Configuraciones
│   ├── SecurityConfig.java             # Configuración de Spring Security
│   └── filter/
│       └── JwtTokenValidator.java      # Filtro de validación JWT
│
├── 🎮 controller/                       # Controladores REST
│   ├── AuthenticationController.java   # Endpoints de autenticación
│   ├── ProductoController.java         # CRUD de productos
│   ├── CategoriaController.java        # CRUD de categorías
│   └── SedeController.java             # CRUD de sedes
│
├── 📦 dto/                              # Data Transfer Objects
│   ├── auth/                           # DTOs de autenticación
│   ├── request/                        # DTOs de request
│   └── response/                       # DTOs de response
│
├── ⚠️ exception/                        # Manejo de excepciones
│   ├── GlobalExceptionHandler.java     # Handler global
│   ├── ResourceNotFoundException.java  # Excepción 404
│   └── DuplicateResourceException.java # Excepción duplicados
│
├── 🔄 mapper/                           # Conversión Entity ↔ DTO
│   ├── ProductoMapper.java
│   ├── CategoriaMapper.java
│   └── SedeMapper.java
│
├── 📊 model/                            # Entidades JPA
│   ├── Usuario.java                    # Usuarios del sistema
│   ├── Rol.java                        # Roles (ADMIN, VENDEDOR, CLIENTE)
│   ├── Producto.java                   # Productos
│   ├── Categoria.java                  # Categorías de productos
│   ├── Sede.java                       # Sedes/sucursales
│   ├── StockSede.java                  # Inventario por sede
│   ├── Carrito.java                    # Carrito de compras
│   ├── Venta.java                      # Ventas realizadas
│   ├── DetalleVenta.java               # Items de cada venta
│   └── MovimientoInventario.java       # Trazabilidad de stock
│
├── 💾 repository/                       # Repositorios JPA
│   ├── ProductoRepository.java
│   ├── CategoriaRepository.java
│   ├── SedeRepository.java
│   └── RolRepository.java
│
├── 💼 service/                          # Lógica de negocio
│   ├── IProductoService.java
│   ├── ICategoriaService.java
│   ├── ISedeService.java
│   └── impl/                           # Implementaciones
│
└── 🛠️ util/                            # Utilidades
    └── JwtUtils.java                   # Utilidades JWT

src/main/resources/
├── application.properties              # Configuración de la app
├── db/migration/                       # Migraciones Flyway
│   ├── V1__initial_schema.sql
│   ├── V2__add_roles.sql
│   └── V3__add_constraints.sql
└── static/                             # Recursos estáticos
```

## 🔄 Migraciones de Base de Datos

El proyecto utiliza **Flyway** para gestionar el versionado del esquema de base de datos.

### Ubicación de Migraciones

```
src/main/resources/db/migration/
├── V1__initial_schema.sql      # Esquema inicial (tablas base)
├── V2__add_roles.sql           # Inserción de roles
└── V3__add_constraints.sql     # Constraints adicionales
```

### Convención de Nombres

- Formato: `V{VERSION}__{DESCRIPTION}.sql`
- Ejemplo: `V4__add_payment_methods.sql`

### Comandos Útiles

```bash
# Ver estado de migraciones
mvn flyway:info

# Aplicar migraciones pendientes
mvn flyway:migrate

# Validar migraciones
mvn flyway:validate
```

> 📌 **Nota**: Las migraciones se ejecutan automáticamente al iniciar la aplicación.

## 🧪 Testing

### Ejecutar Tests

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con reporte de cobertura
mvn clean test jacoco:report

# Ejecutar una clase de test específica
mvn test -Dtest=ProductoServiceTest
```

### Estructura de Tests

```
src/test/java/com/titta/api/
├── controller/          # Tests de controladores
├── service/            # Tests de servicios
├── repository/         # Tests de repositorios
└── integration/        # Tests de integración
```

## 👥 Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| 👑 **ADMINISTRADOR** | Control total del sistema | CRUD completo en todas las entidades, gestión de usuarios, configuración del sistema |
| 💼 **VENDEDOR** | Personal de punto de venta | Gestión de ventas, consulta de inventario, actualización de stock, gestión de carritos |
| 🛒 **CLIENTE** | Usuario final | Navegación de productos, creación de carritos, realización de compras, consulta de órdenes |

### Matriz de Permisos

| Recurso | ADMINISTRADOR | VENDEDOR | CLIENTE |
|---------|---------------|----------|---------|
| Productos | CRUD | Read | Read |
| Categorías | CRUD | Read | Read |
| Sedes | CRUD | Read | Read |
| Inventario | CRUD | Update | - |
| Ventas | CRUD | Create/Read | Create/Read (propias) |
| Usuarios | CRUD | - | Read (propio) |
| Carritos | CRUD | CRUD | CRUD (propio) |

## 🌟 Características Técnicas Destacadas

- ✅ **Arquitectura en capas** (Controller → Service → Repository)
- ✅ **DTOs** para separación de lógica de negocio
- ✅ **Exception Handling global** con respuestas estandarizadas
- ✅ **Validación de datos** con Bean Validation
- ✅ **Transaccionalidad** en operaciones críticas
- ✅ **Lazy Loading** optimizado en relaciones JPA
- ✅ **Queries personalizadas** con JPQL
- ✅ **Migraciones versionadas** con Flyway
- ✅ **Seguridad robusta** con JWT y Spring Security
- ✅ **Documentación automática** con OpenAPI 3.0

## 🚧 Roadmap

- [ ] Implementar paginación en listados
- [ ] Agregar filtros avanzados de búsqueda
- [ ] Sistema de notificaciones en tiempo real
- [ ] Reportes y dashboard de administración
- [ ] Integración con pasarelas de pago
- [ ] Sistema de reviews y calificaciones
- [ ] API de terceros para logística
- [ ] Cache con Redis
- [ ] Containerización con Docker
- [ ] CI/CD con GitHub Actions

## 🤝 Contribuciones

Este es un proyecto privado en desarrollo activo. Si deseas contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Convención de Commits

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Formateo, sin cambios en código
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Mantenimiento

## 📝 Licencia

Este proyecto es **privado** y está en desarrollo activo. 

Todos los derechos reservados © 2025 Titta

## 👨‍💻 Autor

**Eduardo Vargas**

- 🐙 GitHub: [@Eduardo-VaZu](https://github.com/Eduardo-VaZu)
- 💼 LinkedIn: [Eduardo Vargas](https://linkedin.com/in/eduardo-vazu)

---

<div align="center">

⭐ **Si te gusta este proyecto, considera darle una estrella en GitHub** ⭐

*Desarrollado con ❤️ usando Spring Boot*

</div>
