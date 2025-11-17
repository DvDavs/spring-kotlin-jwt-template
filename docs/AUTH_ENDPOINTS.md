# 🔐 Authentication Endpoints Documentation

## Descripción General

El proyecto incluye un sistema completo de autenticación con JWT (JSON Web Tokens) implementado con Spring Security.

## 📋 Endpoints Disponibles

### 1. **Login (Obtener Token)**
Inicia sesión y obtiene el access token y refresh token.

**Endpoint:** `POST /auth/token`

**Request Body:**
```json
{
    "email": "david@davscode.com",
    "password": "4dm1n1straT0r123$"
}
```

**Response (200 OK):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "abc123def456...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
        "id": 1,
        "name": "David",
        "lastName": "García",
        "email": "david@davscode.com",
        "role": "MASTER"
    }
}
```

---

### 2. **Register (Registrar Usuario)**
Registra un nuevo usuario con rol USER por defecto.

**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
    "name": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@davscode.com",
    "password": "MySecureP@ssw0rd"
}
```

**Response (200 OK):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "abc123def456...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
        "id": 6,
        "name": "Juan",
        "lastName": "Pérez",
        "email": "juan.perez@davscode.com",
        "role": "USER"
    }
}
```

**Validaciones:**
- Email: Debe ser válido
- Password: Mínimo 8 caracteres
- Todos los campos son requeridos

---

### 3. **Refresh Token**
Renueva el access token usando el refresh token.

**Endpoint:** `POST /auth/refresh-token`

**Request Body:**
```json
{
    "refreshToken": "abc123def456..."
}
```

**Response (200 OK):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "new_refresh_token...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
        "id": 1,
        "name": "David",
        "lastName": "García",
        "email": "david@davscode.com",
        "role": "MASTER"
    }
}
```

**Notas:**
- El refresh token tiene duración de 7 días
- Al renovar, se genera un nuevo refresh token y se elimina el anterior

---

### 4. **Request Password Reset**
Solicita un token para restablecer la contraseña. Se envía un email al usuario con el enlace de reset.

**Endpoint:** `POST /auth/request-password-reset`

**Request Body:**
```json
{
    "email": "david@davscode.com"
}
```

**Response (200 OK):**
```json
{
    "message": "Password reset email sent successfully"
}
```

**Notas:**
- 📧 Se envía un email al usuario con un enlace para restablecer la contraseña
- El enlace contiene el token y redirige al frontend configurado (`FRONTEND_URL/reset-password?token=...`)
- El token es válido por **15 minutos** por seguridad
- El email incluye el nombre del usuario y un botón/enlace para restablecer
- **Requisitos**: Debes configurar las variables `MAIL_USERNAME` y `MAIL_PASSWORD` (ver README.md)

---

### 5. **Reset Password**
Restablece la contraseña usando el token.

**Endpoint:** `POST /auth/reset-password`

**Request Body:**
```json
{
    "resetToken": "TOKEN_FROM_PREVIOUS_STEP",
    "newPassword": "NewSecureP@ssw0rd123"
}
```

**Response (200 OK):**
```json
{
    "message": "Password reset successfully"
}
```

**Validaciones:**
- Token: Debe ser válido y no expirado
- Password: Mínimo 8 caracteres

---

## 👥 Usuarios de Prueba

| Email | Password | Role |
|-------|----------|------|
| david@davscode.com | 4dm1n1straT0r123$ | MASTER |
| denisse@davscode.com | 4dm1n1straT0r123$ | ADMIN |
| moises@davscode.com | 4dm1n1straT0r123$ | ADMIN |
| carlos.garcia@davscode.com | 4dm1n1straT0r123$ | USER |
| ana.martinez@davscode.com | 4dm1n1straT0r123$ | USER |

---

## 🔒 Autenticación en Endpoints Protegidos

Para acceder a endpoints protegidos, incluye el access token en el header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Ejemplo con curl:

```bash
curl -X GET http://localhost:6003/admin/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Ejemplo con Postman:

1. Selecciona el tab "Authorization"
2. Tipo: "Bearer Token"
3. Token: Pega tu access token

---

## 📝 Manejo de Errores

### Errores Comunes

**400 Bad Request:**
```json
{
    "timestamp": "17/11/2024 22:30:45",
    "statusCode": 400,
    "errors": {
        "email": "Email must be valid",
        "password": "Password is required"
    }
}
```

**401 Unauthorized:**
```json
{
    "timestamp": "17/11/2024 22:30:45",
    "statusCode": 401,
    "errors": {
        "url": "Invalid email or password"
    }
}
```

**403 Forbidden:**
```json
{
    "timestamp": "17/11/2024 22:30:45",
    "statusCode": 403,
    "errors": {
        "url": "No tienes permisos para acceder a esta URL"
    }
}
```

---

## 🧪 Probar con Postman

### Importar Colección

1. Abre Postman
2. Click en "Import"
3. Selecciona el archivo `Template_Auth_Endpoints.postman_collection.json`
4. La colección incluye:
   - ✅ Todos los endpoints de autenticación
   - ✅ Variables de entorno pre-configuradas
   - ✅ Script automático para guardar tokens
   - ✅ Ejemplos de endpoints protegidos

### Variables de Entorno

La colección usa estas variables:
- `base_url`: http://localhost:6003 (puedes cambiarla)
- `access_token`: Se guarda automáticamente al hacer login
- `refresh_token`: Se guarda automáticamente al hacer login

### Script Automático

La colección incluye un script que automáticamente:
- Guarda el `access_token` al hacer login o refresh
- Guarda el `refresh_token` al hacer login o refresh
- No necesitas copiar/pegar tokens manualmente

---

## 🔄 Flujo Completo de Autenticación

```
1. Usuario hace login
   └─> POST /auth/token
       └─> Recibe: accessToken + refreshToken

2. Usuario accede a recursos protegidos
   └─> GET /admin/users (con Bearer token)
       └─> 200 OK (si tiene permisos)

3. Access token expira (1 hora)
   └─> 401 Unauthorized

4. Usuario renueva el token
   └─> POST /auth/refresh-token
       └─> Recibe: nuevo accessToken + refreshToken

5. Continúa usando la aplicación
```

---

## 🛠️ Configuración JWT

En `application.yml`:

```yaml
template:
  jwt:
    secret: ${JWT_SECRET}        # Variable de entorno
    issuer: evolutiondevmx
    expiration: 3600000          # 1 hora en milisegundos
```

**Duración de Tokens:**
- Access Token: 1 hora
- Refresh Token: 7 días
- Reset Token: 1 hora

---

## 📚 Próximos Pasos

Una vez autenticado, puedes:

1. **Crear tus propios endpoints protegidos** siguiendo el ejemplo del README
2. **Usar `@PreAuthorize`** para proteger endpoints por rol:
   ```kotlin
   @PreAuthorize("hasRole('ADMIN')")
   fun adminEndpoint() { ... }
   ```
3. **Obtener el usuario autenticado** en tus controladores:
   ```kotlin
   fun myEndpoint(authentication: Authentication) {
       val userDetails = authentication.principal as CustomerDetails
       val userId = userDetails.id
       // ...
   }
   ```

---

¡Los endpoints están listos para usar! 🚀

