# 🐳 Docker - Titta API

Esta aplicación está completamente dockerizada para facilitar el desarrollo y despliegue.

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

```bash
# Copiar el template
cp .env.example .env

# Editar .env con tus valores
# IMPORTANTE: Cambia JWT_KEY_SECRET y STRIPE_API_KEY
```

### 2. Ejecutar con Docker Compose

```bash
# Construir y levantar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f titta-api
```

### 3. Acceder a la Aplicación

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/actuator/health

## 📚 Comandos Básicos

```bash
# Detener servicios
docker-compose stop

# Eliminar contenedores (datos persisten)
docker-compose down

# Ver estado
docker-compose ps

# Rebuild después de cambios
docker-compose up -d --build
```

## 📖 Documentación Completa

Ver [docker-guide.md](docker-guide.md) para:

- Comandos avanzados
- Troubleshooting
- Desarrollo con Docker
- Despliegue en producción

## 🏗️ Arquitectura

- **titta-api**: Spring Boot 3.3.1 (Java 21)
- **mysql**: MySQL 8.0
- **Network**: Bridge network para comunicación
- **Volumes**: Persistencia de datos MySQL

## ⚠️ Notas Importantes

- El archivo `.env` NO debe ser commiteado
- Primera ejecución puede tardar ~2 minutos
- MySQL necesita inicializarse antes que la app
