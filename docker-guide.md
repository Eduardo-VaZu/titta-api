# 🐳 Guía de Docker - Titta API

## 📋 Requisitos Previos

- **Docker Desktop** instalado ([Descargar aquí](https://www.docker.com/products/docker-desktop))
- **Docker Compose** (incluido con Docker Desktop)
- Al menos **4GB de RAM** disponible para Docker

### Verificar instalación

```bash
docker --version
docker-compose --version
```

---

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

```bash
# Copiar el template de variables de entorno
cp .env.example .env

# Editar .env con tus valores reales
# IMPORTANTE: Cambia JWT_KEY_SECRET y STRIPE_API_KEY
```

### 2. Construir y Ejecutar

```bash
# Construir y levantar todos los servicios
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f titta-api
```

### 3. Acceder a la Aplicación

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

---

## 📚 Comandos Útiles

### Gestión de Contenedores

```bash
# Ver estado de los contenedores
docker-compose ps

# Detener todos los servicios
docker-compose stop

# Detener y eliminar contenedores (los datos persisten en volumes)
docker-compose down

# Detener y eliminar TODO (⚠️ incluye volúmenes/datos)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart titta-api
```

### Logs y Debugging

```bash
# Ver logs de todos los servicios
docker-compose logs

# Ver logs de un servicio específico
docker-compose logs titta-api
docker-compose logs mysql

# Seguir logs en tiempo real
docker-compose logs -f titta-api

# Ver últimas 100 líneas
docker-compose logs --tail=100 titta-api
```

### Rebuild y Actualización

```bash
# Reconstruir imagen después de cambios en código
docker-compose build titta-api

# Reconstruir sin usar cache
docker-compose build --no-cache titta-api

# Detener, reconstruir y levantar
docker-compose up -d --build
```

### Acceso a Contenedores

```bash
# Ejecutar comando en contenedor (shell interactivo)
docker-compose exec titta-api sh

# Acceder a MySQL
docker-compose exec mysql mysql -uroot -proot

# Ver bases de datos
docker-compose exec mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

### Gestión de Volúmenes

```bash
# Listar volúmenes
docker volume ls

# Inspeccionar volumen de MySQL
docker volume inspect titta-api_mysql_data

# Backup de base de datos
docker-compose exec mysql mysqldump -uroot -proot titta_db > backup.sql

# Restaurar base de datos
docker-compose exec -T mysql mysql -uroot -proot titta_db < backup.sql
```

---

## 🔧 Configuración Avanzada

### Puertos Personalizados

Edita `docker-compose.yml` para cambiar puertos:

```yaml
services:
  titta-api:
    ports:
      - "9090:8080" # Acceder en localhost:9090
```

### Múltiples Perfiles de Spring

```bash
# Ejecutar con perfil de producción
SPRING_PROFILES_ACTIVE=prod docker-compose up -d
```

### Variables de Entorno Override

```bash
# Sobrescribir variable específica
DB_PASSWORD=mi-password-seguro docker-compose up -d
```

---

## 🐛 Troubleshooting

### La aplicación no inicia

**Problema**: `titta-api` se reinicia constantemente

**Solución**:

```bash
# Ver logs para identificar el error
docker-compose logs titta-api

# Errores comunes:
# 1. MySQL no está listo - espera ~30 segundos
# 2. Variables de entorno incorrectas - revisa .env
# 3. Puerto 8080 ocupado - cámbialo en docker-compose.yml
```

### Error de conexión a MySQL

**Problema**: `Communications link failure`

**Solución**:

```bash
# Verificar que MySQL esté corriendo
docker-compose ps mysql

# Verificar health check
docker-compose exec mysql mysqladmin ping -h localhost -uroot -proot

# Reiniciar MySQL
docker-compose restart mysql
```

### Puerto ya en uso

**Problema**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solución**:

```bash
# Opción 1: Detener proceso que usa el puerto
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Opción 2: Cambiar puerto en docker-compose.yml
ports:
  - "9090:8080"
```

### Datos perdidos después de `docker-compose down`

**Problema**: Base de datos vacía después de reiniciar

**Causa**: Usaste `docker-compose down -v` (elimina volúmenes)

**Prevención**:

- Usa `docker-compose stop` para detener sin eliminar
- Usa `docker-compose down` SIN `-v` para mantener datos
- Haz backups regulares de la base de datos

### Build muy lento

**Solución**:

```bash
# Usar .dockerignore para excluir archivos innecesarios
# Ya está configurado en el proyecto

# Limpiar caché de Docker
docker system prune -a

# Aumentar memoria de Docker Desktop
# Docker Desktop > Settings > Resources > Memory
```

---

## 🏗️ Desarrollo con Docker

### Hot Reload (Cambios en Código)

Por defecto, Docker NO tiene hot reload. Opciones:

**Opción 1: Rebuild manual (recomendado para producción)**

```bash
docker-compose up -d --build
```

**Opción 2: Montar código como volumen (desarrollo)**

Agrega en `docker-compose.yml`:

```yaml
services:
  titta-api:
    volumes:
      - ./target:/app/target
```

Luego compila localmente con:

```bash
./mvnw clean package -DskipTests
docker-compose restart titta-api
```

### Ejecutar Tests

```bash
# Ejecutar tests dentro del contenedor
docker-compose exec titta-api sh -c "cd /app && ./mvnw test"
```

---

## 📊 Monitoreo

### Ver uso de recursos

```bash
# Ver CPU, memoria, I/O de contenedores
docker stats

# Ver solo titta-api
docker stats titta-api
```

### Health Checks

```bash
# Verificar salud de la aplicación
curl http://localhost:8080/actuator/health

# Ver métricas (si Actuator está habilitado)
curl http://localhost:8080/actuator/metrics
```

---

## 🔒 Seguridad

### Mejores Prácticas

- ✅ **NUNCA** comitees el archivo `.env` con credenciales reales
- ✅ Usa secretos diferentes para desarrollo y producción
- ✅ Genera JWT_KEY_SECRET aleatorio: `openssl rand -base64 64`
- ✅ Cambia contraseñas por defecto de MySQL
- ✅ En producción, usa `APP_COOKIE_SECURE=true`

### Rotar Secretos

```bash
# Generar nuevo JWT secret
openssl rand -base64 64

# Actualizar .env
# Reiniciar aplicación
docker-compose restart titta-api
```

---

## 📦 Despliegue en Producción

### 1. Construir para Producción

```bash
# Usar perfil de producción
SPRING_PROFILES_ACTIVE=prod docker-compose up -d

# O crear docker-compose.prod.yml separado
docker-compose -f docker-compose.prod.yml up -d
```

### 2. Usar HTTPS

Considera usar un reverse proxy como **Nginx** o **Traefik** con certificados SSL.

### 3. Backups Automáticos

```bash
# Cron job para backup diario
0 2 * * * docker-compose exec mysql mysqldump -uroot -p$DB_PASSWORD titta_db > /backups/titta_$(date +\%Y\%m\%d).sql
```

---

## 📖 Recursos Adicionales

- [Docker Docs](https://docs.docker.com/)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker)

---

## 💡 Tips

- Usa `docker-compose logs -f` durante desarrollo para ver errores inmediatamente
- Mantén tus imágenes actualizadas: `docker-compose pull`
- Limpia recursos no usados: `docker system prune`
- Para CI/CD, considera usar imágenes pre-construidas en registry
