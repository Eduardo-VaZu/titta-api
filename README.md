# 🛍️ Titta API

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Private-red)](https://github.com/Eduardo-VaZu/titta-api)

> API REST empresarial para sistema de ecommerce multisede con autenticación JWT, gestión de inventario distribuido y arquitectura modular basada en features.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Stack Tecnológico](#-stack-tecnológico)
- [Características Principales](#-características-principales)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#️-instalación-y-configuración)
- [Ejecución](#-ejecución)
- [Documentación API](#-documentación-api)
- [Estructura del Proyecto](#️-estructura-del-proyecto)
- [Endpoints Principales](#-endpoints-principales)
- [Autenticación y Seguridad](#-autenticación-y-seguridad)
- [Testing](#-testing)
- [Migraciones de Base de Datos](#-migraciones-de-base-de-datos)
- [Roadmap](#-roadmap)

## 📋 Descripción

**Titta API** es una aplicación backend empresarial desarrollada con Spring Boot 3 que implementa una arquitectura moderna orientada a features (feature-based architecture) para gestionar un sistema de ecommerce completo.

### Funcionalidades Core

- 🏪 **Gestión de Productos y Categorías** - CRUD completo con soporte para múltiples imágenes
- 📦 **Control de Inventario Multisede** - Stock independiente por sucursal con trazabilidad
- 🔐 **Autenticación y Autorización** - JWT con Spring Security 6 y roles jerárquicos
- 🛒 **Sistema de Ventas** - Carritos de compra, órdenes y estados transaccionales
- 👥 **Gestión de Usuarios** - Sistema de roles (ADMINISTRADOR, VENDEDOR, CLIENTE)
- 🏢 **Gestión de Sedes** - Múltiples sucursales con horarios y stock independiente
- 💳 **Métodos de Pago** - Múltiples opciones de pago y direcciones de entrega
- 📊 **Trazabilidad** - Historial completo de movimientos de inventario

## 🚀 Stack Tecnológico

### Backend Framework
- **Java 21** - LTS con mejoras de rendimiento y Virtual Threads
- **Spring Boot 3.3.1** - Framework principal con configuración auto-mágica
- **Spring Security 6** - Autenticación y autorización robusta
- **Spring Data JPA** - Capa de persistencia con Hibernate 6
- **Spring Validation** - Validación declarativa de datos

### Base de Datos
- **MySQL 8.0+** - Sistema de gestión de base de datos relacional
- **Flyway** - Versionado y migraciones de esquema

### Seguridad y Tokens
- **Auth0 JWT 4.4.0** - Generación y validación de tokens JWT
- **BCrypt** - Encriptación segura de contraseñas con salt

### Documentación
- **SpringDoc OpenAPI 3 (2.3.0)** - Documentación interactiva con Swagger UI

### Utilidades
- **Lombok** - Reducción de código boilerplate
- **Spring DotEnv 3.0.0** - Gestión segura de variables de entorno
- **Maven** - Gestión de dependencias y construcción del proyecto

## ✨ Características Principales

### Arquitectura y Diseño
- ✅ **Arquitectura Feature-Based** - Módulos independientes por funcionalidad
- ✅ **Domain-Driven Design** - Lógica de dominio separada por capas
- ✅ **DTOs y Mappers** - Separación entre capa de presentación y dominio
- ✅ **Exception Handling Global** - Respuestas de error estandarizadas
- ✅ **Validación de Datos** - Bean Validation con mensajes personalizados

### Rendimiento y Optimización
- ✅ **Lazy Loading Optimizado** - Carga bajo demanda en relaciones JPA
- ✅ **Queries Personalizadas** - JPQL optimizado para casos específicos
- ✅ **Transaccionalidad** - Manejo de transacciones en operaciones críticas

### Seguridad
- ✅ **JWT Stateless** - Autenticación sin sesiones en servidor
- ✅ **Role-Based Access Control** - Control granular de permisos
- ✅ **Password Hashing** - BCrypt con factor de costo configurable
- ✅ **CORS Configurado** - Control de acceso desde origen

### DevOps y Mantenimiento
- ✅ **Migraciones Versionadas** - Flyway para control de esquema
- ✅ **Documentación Auto-generada** - OpenAPI 3.0 actualizada automáticamente
- ✅ **Health Checks** - Endpoints de monitoreo con Spring Actuator
- ✅ **Logs Estructurados** - Logging configurable por nivel

## 📦 Requisitos Previos

| Herramienta | Versión Mínima | Descarga |
|-------------|----------------|----------|
| JDK | 21+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/) |
| Maven | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [MySQL Downloads](https://dev.mysql.com/downloads/mysql/) |
| Git | 2.0+ | [Git SCM](https://git-scm.com/downloads) |

### Verificar Instalaciones

```bash
java -version    # Debe mostrar versión 21 o superior
mvn -version     # Debe mostrar Maven 3.8 o superior
mysql --version  # Debe mostrar MySQL 8.0 o superior
git --version    # Cualquier versión reciente
```

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Eduardo-VaZu/titta-api.git
cd titta-api
```

### 2. Configurar Base de Datos

Conectarse a MySQL y crear la base de datos:

```sql
CREATE DATABASE db_titta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar Variables de Entorno

Crear un archivo `.env` en la raíz del proyecto:

```properties
# ============================================
# CONFIGURACIÓN DE BASE DE DATOS
# ============================================
DB_HOST=localhost
DB_PORT=3306
DB_NAME=db_titta
DB_USERNAME=root
DB_PASSWORD=tu_contraseña_mysql
DB_DRIVER=com.mysql.cj.jdbc.Driver

# ============================================
# CONFIGURACIÓN DE SEGURIDAD JWT
# ============================================
JWT_KEY_SECRET=tu_clave_secreta_jwt_minimo_256_bits_recomendado_512
JWT_USER_GENERATOR=TittaAPI
```

> ⚠️ **Importante**: 
> - El archivo `.env` ya está incluido en `.gitignore` por seguridad
> - La clave JWT debe tener mínimo 256 bits (32 caracteres)
> - Para producción, usa claves más largas (512 bits recomendado)

### 4. Generar Clave JWT Segura

```bash
# Generar clave aleatoria de 64 caracteres (512 bits)
openssl rand -base64 64 | tr -d '\n'
```

### 5. Instalar Dependencias

```bash
mvn clean install
```

Este comando:
- Descarga todas las dependencias del proyecto
- Compila el código fuente
- Ejecuta las pruebas unitarias
- Genera el archivo JAR ejecutable

## 🏃 Ejecución

### Modo Desarrollo (con Hot Reload)

```bash
mvn spring-boot:run
```

Spring DevTools habilitado para recarga automática de cambios.

### Modo Producción

```bash
# Compilar JAR optimizado
mvn clean package -DskipTests

# Ejecutar JAR
java -jar target/titta-api-0.0.1-SNAPSHOT.jar
```

### Configurar Puerto Personalizado

```bash
# Opción 1: Variable de entorno
export SERVER_PORT=8081
mvn spring-boot:run

# Opción 2: Parámetro en ejecución
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Opción 3: Con JAR
java -jar target/titta-api-0.0.1-SNAPSHOT.jar --server.port=8081
```

### Verificar Estado de la Aplicación

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Respuesta esperada
{"status":"UP"}
```

La aplicación estará disponible en: **`http://localhost:8080`**

## 📚 Documentación API

### Swagger UI (Interfaz Interactiva)

Accede a la documentación completa con interfaz visual en:

🔗 **http://localhost:8080/swagger-ui.html**

#### Características de Swagger UI:
- 📖 Explorador interactivo de todos los endpoints
- 🧪 Probar requests directamente desde el navegador
- 📝 Esquemas de datos con ejemplos
- 🔐 Autenticación JWT integrada
- 📥 Descargar especificación OpenAPI

### OpenAPI JSON

Especificación en formato JSON disponible en:

🔗 **http://localhost:8080/v3/api-docs**

Útil para:
- Generar clientes automáticos (Postman, Insomnia, etc.)
- Integración con herramientas de testing
- Documentación externa

### Colección de Postman

Para importar la colección en Postman:

1. Abrir Postman
2. Ir a **Import** → **Link**
3. Pegar: `http://localhost:8080/v3/api-docs`
4. Confirmar importación

## 🗂️ Estructura del Proyecto

```
titta-api/
│
├── 📁 src/main/java/com/titta/api/
│   ├── 📄 TittaApiApplication.java          # Clase principal de Spring Boot
│   │
│   ├── 📁 config/                            # Configuraciones globales
│   │   ├── SecurityConfig.java              # Spring Security + JWT
│   │   ├── exception/                       # Exception Handlers
│   │   │   └── GlobalExceptionHandler.java  # Handler global de errores
│   │   └── filter/                          # Filtros HTTP
│   │       └── JwtTokenValidator.java       # Validación de tokens JWT
│   │
│   ├── 📁 domain/                            # Capa de dominio (DDD)
│   │   ├── model/                           # Entidades JPA
│   │   │   ├── Usuario.java                # Usuarios del sistema
│   │   │   ├── Rol.java                    # Roles de autorización
│   │   │   ├── Producto.java               # Productos del catálogo
│   │   │   ├── Categoria.java              # Categorías
│   │   │   ├── ImagenProducto.java         # Imágenes de productos
│   │   │   ├── Sede.java                   # Sucursales/sedes
│   │   │   ├── StockSede.java              # Inventario por sede
│   │   │   ├── Carrito.java                # Carritos de compra
│   │   │   ├── ItemCarrito.java            # Items del carrito
│   │   │   ├── Venta.java                  # Órdenes de venta
│   │   │   ├── DetalleVenta.java           # Line items de venta
│   │   │   ├── MetodoPago.java             # Métodos de pago
│   │   │   ├── Direccion.java              # Direcciones de envío
│   │   │   └── MovimientoInventario.java   # Trazabilidad de stock
│   │   │
│   │   └── repository/                      # Repositorios JPA
│   │       ├── UsuarioRepository.java
│   │       ├── RolRepository.java
│   │       ├── ProductoRepository.java
│   │       ├── CategoriaRepository.java
│   │       ├── SedeRepository.java
│   │       ├── StockSedeRepository.java
│   │       ├── CarritoRepository.java
│   │       └── VentaRepository.java
│   │
│   └── 📁 features/                          # Módulos por funcionalidad
│       │
│       ├── 📁 auth/                         # Módulo de autenticación
│       │   ├── controller/
│       │   │   └── AuthenticationController.java
│       │   ├── dto/
│       │   │   ├── request/
│       │   │   │   ├── AuthLoginRequest.java
│       │   │   │   └── AuthRegisterRequest.java
│       │   │   └── response/
│       │   │       └── AuthResponse.java
│       │   ├── service/
│       │   │   ├── UserDetailServiceImpl.java
│       │   │   └── IAuthService.java
│       │   └── util/
│       │       └── JwtUtils.java
│       │
│       ├── 📁 category/                     # Módulo de categorías
│       │   ├── controller/
│       │   │   └── CategoriaController.java
│       │   ├── dto/
│       │   │   ├── CategoriaRequest.java
│       │   │   └── CategoriaResponse.java
│       │   ├── mapper/
│       │   │   └── CategoriaMapper.java
│       │   └── service/
│       │       ├── ICategoriaService.java
│       │       └── impl/
│       │           └── CategoriaServiceImpl.java
│       │
│       ├── 📁 product/                      # Módulo de productos
│       │   ├── controller/
│       │   │   └── ProductoController.java
│       │   ├── dto/
│       │   │   ├── ProductoRequest.java
│       │   │   └── ProductoResponse.java
│       │   ├── mapper/
│       │   │   └── ProductoMapper.java
│       │   └── service/
│       │       ├── IProductoService.java
│       │       └── impl/
│       │           └── ProductoServiceImpl.java
│       │
│       └── 📁 sede/                         # Módulo de sedes
│           ├── controller/
│           │   └── SedeController.java
│           ├── dto/
│           │   ├── SedeRequest.java
│           │   └── SedeResponse.java
│           ├── mapper/
│           │   └── SedeMapper.java
│           └── service/
│               ├── ISedeService.java
│               └── impl/
│                   └── SedeServiceImpl.java
│
├── 📁 src/main/resources/
│   ├── application.properties              # Configuración principal
│   ├── .env                                # Variables de entorno (NO subir a Git)
│   └── db/migration/                       # Migraciones Flyway
│       ├── V1__Crear_esquema_inicial.sql
│       └── V2__Modificar_tabla_rol_imagen.sql
│
├── 📁 src/test/java/                        # Tests
│   └── com/titta/api/
│       ├── controller/                      # Tests de controladores
│       ├── service/                         # Tests de servicios
│       └── repository/                      # Tests de repositorios
│
├── .gitignore                               # Archivos ignorados por Git
├── pom.xml                                  # Configuración Maven
└── README.md                                # Este archivo
```

## 🔌 Endpoints Principales

### 🔑 Autenticación (`/api/v1/auth`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/sign-up` | Registrar nuevo usuario | ❌ No |
| POST | `/log-in` | Iniciar sesión (obtener JWT) | ❌ No |

**Ejemplo - Registro:**
```json
POST /api/v1/auth/sign-up
Content-Type: application/json

{
  "nombre": "Juan",
  "apellidoPaterno": "Pérez",
  "apellidoMaterno": "García",
  "email": "juan.perez@example.com",
  "password": "Password123!"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Usuario registrado exitosamente",
  "username": "juan.perez@example.com"
}
```

**Ejemplo - Login:**
```json
POST /api/v1/auth/log-in
Content-Type: application/json

{
  "username": "juan.perez@example.com",
  "password": "Password123!"
}
```

### 🏪 Productos (`/api/v1/productos`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/` | Listar todos los productos | 🌐 Público |
| GET | `/{id}` | Obtener producto por ID | 🌐 Público |
| POST | `/` | Crear nuevo producto | 👑 ADMIN |
| PUT | `/{id}` | Actualizar producto | 👑 ADMIN |
| DELETE | `/{id}` | Eliminar producto | 👑 ADMIN |

### 📦 Categorías (`/api/v1/categorias`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/` | Listar categorías | 🌐 Público |
| GET | `/{id}` | Obtener categoría | 🌐 Público |
| POST | `/` | Crear categoría | 👑 ADMIN |
| PUT | `/{id}` | Actualizar categoría | 👑 ADMIN |
| DELETE | `/{id}` | Eliminar categoría | 👑 ADMIN |

### 🏢 Sedes (`/api/v1/sedes`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/` | Listar sedes | 🌐 Público |
| GET | `/{id}` | Obtener sede | 🌐 Público |
| POST | `/` | Crear sede | 👑 ADMIN |
| PUT | `/{id}` | Actualizar sede | 👑 ADMIN |
| DELETE | `/{id}` | Eliminar sede | 👑 ADMIN |

## 🔐 Autenticación y Seguridad

### JWT (JSON Web Token)

El sistema utiliza JWT para autenticación stateless. Cada token contiene:

- **Subject**: Email del usuario
- **Claims**: Rol y permisos
- **Issuer**: TittaAPI
- **Expiration**: Configurable (por defecto 24 horas)

### Cómo Usar JWT en Requests

Una vez autenticado, incluye el token en el header de cada request:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/productos \
  -H "Authorization: Bearer TU_TOKEN_JWT_AQUI"
```

**Ejemplo con JavaScript (Fetch):**
```javascript
fetch('http://localhost:8080/api/v1/productos', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| 👑 **ADMINISTRADOR** | Control total del sistema | CRUD en todas las entidades, gestión de usuarios |
| 💼 **VENDEDOR** | Personal de punto de venta | Gestión de ventas, consulta de inventario |
| 🛒 **CLIENTE** | Usuario final | Navegación, carrito, compras |

### Matriz de Permisos Detallada

| Recurso | ADMINISTRADOR | VENDEDOR | CLIENTE |
|---------|---------------|----------|---------|
| Productos | ✅ CRUD | 👁️ Read | 👁️ Read |
| Categorías | ✅ CRUD | 👁️ Read | 👁️ Read |
| Sedes | ✅ CRUD | 👁️ Read | 👁️ Read |
| Inventario | ✅ CRUD | ✏️ Update | ❌ - |
| Ventas | ✅ CRUD | ✅ Create/Read | 👁️ Read (propias) |
| Usuarios | ✅ CRUD | ❌ - | 👁️ Read (propio) |
| Carritos | ✅ CRUD | ✅ CRUD | ✏️ CRUD (propio) |

## 🧪 Testing

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar con reporte de cobertura
mvn clean test jacoco:report

# Ejecutar una clase específica
mvn test -Dtest=CategoriaServiceImplTest

# Ejecutar un método específico
mvn test -Dtest=CategoriaServiceImplTest#testFindById

# Ejecutar en modo verbose
mvn test -X
```

### Ver Reporte de Cobertura

Después de ejecutar `mvn clean test jacoco:report`:

```bash
# Abrir reporte HTML
open target/site/jacoco/index.html  # macOS
start target/site/jacoco/index.html # Windows
xdg-open target/site/jacoco/index.html # Linux
```

### Estructura de Tests

```
src/test/java/com/titta/api/
├── controller/          # Tests de controladores (MockMvc)
├── service/
│   └── impl/
│       └── CategoriaServiceImplTest.java
├── repository/          # Tests de repositorios (DataJpaTest)
└── integration/         # Tests de integración completos
```

### Ejemplo de Test Unitario

```java
@Test
void testFindById_Success() {
    // Arrange
    Long id = 1L;
    Categoria categoria = new Categoria();
    categoria.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(categoria));
    
    // Act
    CategoriaResponse result = service.findById(id);
    
    // Assert
    assertNotNull(result);
    assertEquals(id, result.getId());
}
```

## 🔄 Migraciones de Base de Datos

El proyecto utiliza **Flyway** para gestionar el versionado del esquema.

### Ubicación de Migraciones

```
src/main/resources/db/migration/
├── V1__Crear_esquema_inicial.sql
└── V2__Modificar_tabla_rol_imagen.sql
```

### Convención de Nombres

Formato: `V{VERSION}__{DESCRIPCION}.sql`

**Ejemplos válidos:**
- ✅ `V1__Crear_esquema_inicial.sql`
- ✅ `V2__Agregar_tabla_notificaciones.sql`
- ✅ `V3__Modificar_columna_precio.sql`

**Ejemplos inválidos:**
- ❌ `V1_Crear_esquema.sql` (un solo guion bajo)
- ❌ `v1__Crear_esquema.sql` (V minúscula)
- ❌ `1__Crear_esquema.sql` (falta V)

### Comandos Flyway Útiles

```bash
# Ver estado de migraciones
mvn flyway:info

# Aplicar migraciones pendientes
mvn flyway:migrate

# Validar migraciones aplicadas
mvn flyway:validate

# Limpiar base de datos (⚠️ PELIGROSO - solo desarrollo)
mvn flyway:clean

# Reparar tabla de historial
mvn flyway:repair
```

### Estado de Migraciones

Al ejecutar `mvn flyway:info`, verás:

```
+-----------+---------+------------------------------+----------+
| Category  | Version | Description                  | State    |
+-----------+---------+------------------------------+----------+
| Versioned | 1       | Crear esquema inicial        | Success  |
| Versioned | 2       | Modificar tabla rol imagen   | Success  |
+-----------+---------+------------------------------+----------+
```

> ⚠️ **Importante**: Las migraciones se ejecutan automáticamente al iniciar la aplicación.

## 🚧 Roadmap

### 🎯 Próximas Implementaciones

#### Fase 1 - Core Features
- [ ] **Paginación y Filtros**
  - [ ] Implementar paginación en listados de productos
  - [ ] Filtros avanzados (precio, categoría, disponibilidad)
  - [ ] Ordenamiento dinámico (precio, nombre, fecha)
  
- [ ] **Sistema de Carritos Completo**
  - [ ] Agregar/eliminar productos al carrito
  - [ ] Actualizar cantidades
  - [ ] Calcular totales con descuentos
  
- [ ] **Gestión de Órdenes**
  - [ ] Crear orden desde carrito
  - [ ] Estados de orden (pendiente, procesando, enviado, entregado)
  - [ ] Historial de órdenes por usuario

#### Fase 2 - Features Avanzadas
- [ ] **Sistema de Reviews y Calificaciones**
  - [ ] Reseñas de productos
  - [ ] Sistema de estrellas (1-5)
  - [ ] Moderación de comentarios
  
- [ ] **Dashboard Administrativo**
  - [ ] Reportes de ventas
  - [ ] Gráficas de inventario
  - [ ] Estadísticas de usuarios
  
- [ ] **Notificaciones**
  - [ ] Notificaciones en tiempo real (WebSocket)
  - [ ] Emails transaccionales (confirmación de orden)
  - [ ] Notificaciones push

#### Fase 3 - Integraciones
- [ ] **Pasarelas de Pago**
  - [ ] Integración con Stripe
  - [ ] Integración con PayPal
  - [ ] Mercado Pago (LATAM)
  
- [ ] **Logística y Envíos**
  - [ ] Integración con API de correos
  - [ ] Tracking de envíos
  - [ ] Cálculo de costos de envío

#### Fase 4 - Optimización
- [ ] **Performance**
  - [ ] Implementar cache con Redis
  - [ ] Optimización de queries N+1
  - [ ] CDN para imágenes
  
- [ ] **DevOps**
  - [ ] Containerización con Docker
  - [ ] CI/CD con GitHub Actions
  - [ ] Monitoreo con Prometheus/Grafana
  - [ ] Despliegue en AWS/Azure

#### Fase 5 - Features Premium
- [ ] Sistema de descuentos y cupones
- [ ] Programa de fidelización
- [ ] Recomendaciones personalizadas (ML)
- [ ] Multi-idioma (i18n)
- [ ] Multi-moneda

## 🤝 Contribuciones

Este proyecto está en desarrollo activo. Para contribuir:

### 1. Fork y Clone
```bash
git clone https://github.com/TU_USUARIO/titta-api.git
cd titta-api
```

### 2. Crear Rama Feature
```bash
git checkout -b feature/nombre-feature
```

### 3. Hacer Cambios y Commit
```bash
git add .
git commit -m "feat: agregar nueva funcionalidad"
```

### 4. Push y Pull Request
```bash
git push origin feature/nombre-feature
```

### Convención de Commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Formato, sin cambios en código
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Tareas de mantenimiento
- `perf:` Mejoras de rendimiento

**Ejemplos:**
```bash
feat: agregar endpoint de búsqueda de productos
fix: corregir validación de email en registro
docs: actualizar README con ejemplos de JWT
refactor: simplificar lógica de cálculo de totales
test: agregar tests para CategoriaService
```

## 📝 Licencia

Este proyecto es **privado** y está en desarrollo activo.

**Todos los derechos reservados © 2025 Titta**

## 👨‍💻 Autor

**Eduardo Vargas**

- 🐙 GitHub: [@Eduardo-VaZu](https://github.com/Eduardo-VaZu)
- 💼 LinkedIn: [Eduardo Vargas](https://linkedin.com/in/eduardo-vazu)

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Comunidad de Java por las herramientas open source
- Todos los contribuidores del proyecto

---

<div align="center">

### ⭐ Si te gusta este proyecto, considera darle una estrella en GitHub ⭐

*Desarrollado con ❤️ usando Spring Boot 3 y Java 21*

**[Documentación API](http://localhost:8080/swagger-ui.html)** • **[Reportar Bug](https://github.com/Eduardo-VaZu/titta-api/issues)** • **[Solicitar Feature](https://github.com/Eduardo-VaZu/titta-api/issues)**

</div>
