# 🚀 Quick Start - Guía Rápida

## Opción 1: Con Docker (Más Fácil) ⭐

### 1. Ir al directorio del proyecto
```bash
cd project_template
```

### 2. Iniciar todo con Docker
```bash
docker-compose up --build
```

### 3. ¡Listo! 
La aplicación estará corriendo en: **http://localhost:6003**

---

## Opción 2: Desarrollo Local (sin Docker para la app)

### 1. Iniciar solo la base de datos
```bash
cd project_template
docker-compose up postgres-db
```

### 2. Configurar variables de entorno

**En Windows (PowerShell):**
```powershell
$env:JWT_SECRET="your-very-secure-secret-key-here-at-least-256-bits"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5501/template_db"
$env:SPRING_DATASOURCE_USERNAME="evolution"
$env:SPRING_DATASOURCE_PASSWORD="evolution123"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:FRONTEND_URL="http://localhost:3000"
```

**En Linux/Mac:**
```bash
export JWT_SECRET="your-very-secure-secret-key-here-at-least-256-bits"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5501/template_db"
export SPRING_DATASOURCE_USERNAME="evolution"
export SPRING_DATASOURCE_PASSWORD="evolution123"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export FRONTEND_URL="http://localhost:3000"
```

> 📧 **Nota sobre email**: Las variables de email son necesarias para la funcionalidad de reset de contraseña. Ver sección de "Configuración de Email" más abajo.

### 3. Ejecutar la aplicación

**Windows:**
```bash
gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

---

## 👥 Usuarios de Prueba

Usa estos usuarios para probar (todos tienen password: `4dm1n1straT0r123$`):

| Email | Role | Descripción |
|-------|------|-------------|
| david@davscode.com | MASTER | Súper administrador |
| denisse@davscode.com | ADMIN | Administrador |
| moises@davscode.com | ADMIN | Administrador |
| carlos.garcia@davscode.com | USER | Usuario regular |
| ana.martinez@davscode.com | USER | Usuario regular |

---

## ✅ Verificar que funciona

**Probar autenticación:**
```bash
# Login
curl -X POST http://localhost:6003/auth/token \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"david@davscode.com\",\"password\":\"4dm1n1straT0r123\$\"}"

# Deberías recibir un JSON con accessToken, refreshToken y datos del usuario
```

**Probar endpoint protegido (debería dar 401 sin token):**
```bash
curl http://localhost:6003/admin/users
```

**Con Postman:**
1. Importa `Template_Auth_Endpoints.postman_collection.json`
2. Usa el request "Login (Get Token)"
3. El token se guarda automáticamente

---

## 🛑 Detener la aplicación

**Con Docker:**
```bash
docker-compose down

# Para eliminar también la base de datos:
docker-compose down -v
```

**Local:**
```
Ctrl + C
```

---

## 📧 Configuración de Email (Opcional pero Recomendado)

Para que funcione el reset de contraseña por email, necesitas configurar Gmail:

### Pasos rápidos:
1. **Activa verificación en 2 pasos** en tu cuenta de Google
2. **Genera contraseña de aplicación**: https://myaccount.google.com/apppasswords
3. **Configura las variables**:
   - `MAIL_USERNAME`: Tu email de Gmail
   - `MAIL_PASSWORD`: La contraseña de aplicación (16 caracteres)

> ⚠️ Usa contraseña de aplicación, NO tu contraseña de Gmail real

---

## 📝 Notas Importantes

1. **Puerto 6003**: La aplicación corre en el puerto 6003
2. **Base de datos**: PostgreSQL en puerto 5501
3. **Primera vez**: Docker tardará más porque descarga imágenes y construye todo
4. **Siguientes veces**: Usa `docker-compose up` (sin `--build`) para ser más rápido
5. **Email**: El reset de contraseña requiere configuración de email (ver sección anterior)

---

## 🆘 Problemas Comunes

### Puerto ya en uso
```bash
# Ver qué está usando el puerto 6003
# Windows
netstat -ano | findstr :6003

# Linux/Mac
lsof -i :6003

# Matar el proceso o cambiar el puerto en docker-compose.yml
```

### Docker no funciona
```bash
# Verificar que Docker esté corriendo
docker ps

# Limpiar todo y empezar de nuevo
docker-compose down -v
docker system prune -a
docker-compose up --build
```

### Base de datos no se conecta
```bash
# Verificar que el contenedor de postgres esté corriendo
docker ps | grep postgres

# Ver logs de postgres
docker-compose logs postgres-db
```

---

Para más detalles, ver **[README.md](README.md)**

