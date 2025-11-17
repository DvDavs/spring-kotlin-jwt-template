# Swagger/OpenAPI Documentation Guide

Esta guía explica cómo usar Swagger UI para explorar y probar la API.

## 📋 Table of Contents

- [Acceso a Swagger UI](#acceso-a-swagger-ui)
- [Autenticación con JWT](#autenticación-con-jwt)
- [Explorando Endpoints](#explorando-endpoints)
- [Probando Endpoints](#probando-endpoints)
- [Anotaciones Swagger](#anotaciones-swagger)
- [Personalización](#personalización)

## Acceso a Swagger UI

Una vez que la aplicación esté corriendo, puedes acceder a Swagger UI en:

### URLs Principales

| URL | Descripción |
|-----|-------------|
| [http://localhost:6003/swagger-ui.html](http://localhost:6003/swagger-ui.html) | Swagger UI (interfaz gráfica) |
| [http://localhost:6003/v3/api-docs](http://localhost:6003/v3/api-docs) | OpenAPI JSON spec |
| [http://localhost:6003/v3/api-docs.yaml](http://localhost:6003/v3/api-docs.yaml) | OpenAPI YAML spec |

### Primera Visita

1. Abre [http://localhost:6003/swagger-ui.html](http://localhost:6003/swagger-ui.html) en tu navegador
2. Verás la documentación completa de la API organizada por tags
3. Los endpoints públicos (auth) no requieren autenticación
4. Los endpoints protegidos requieren un JWT token

## Autenticación con JWT

### Paso 1: Obtener un Access Token

1. **Localiza el endpoint de login** en la sección "Authentication"
2. **Expande** `POST /auth/token`
3. **Click en "Try it out"**
4. **Edita el Request Body**:
   ```json
   {
     "email": "david@davscode.com",
     "password": "4dm1n1straT0r123$"
   }
   ```
5. **Click en "Execute"**
6. **Copia el `accessToken`** de la respuesta

### Paso 2: Autorizar en Swagger

1. **Click en el botón "Authorize"** 🔓 (esquina superior derecha)
2. **Pega tu access token** en el campo `Value` (sin el prefijo 'Bearer ')
3. **Click en "Authorize"**
4. **Click en "Close"**

¡Listo! Ahora todos tus requests incluirán automáticamente el token JWT en el header `Authorization`.

### Paso 3: Probar Endpoints Protegidos

Ahora puedes probar cualquier endpoint protegido:

1. Expande cualquier endpoint (ej: `GET /api/users`)
2. Click en "Try it out"
3. Completa los parámetros necesarios
4. Click en "Execute"
5. Verás la respuesta abajo

### Cerrar Sesión en Swagger

Para remover el token:
1. Click en "Authorize" 🔓
2. Click en "Logout"
3. Click en "Close"

## Explorando Endpoints

### Estructura de la Documentación

Swagger organiza los endpoints por **Tags**:

```
📁 Authentication
  ├── POST /auth/token          # Login
  ├── POST /auth/register       # Registro
  ├── POST /auth/refresh-token  # Renovar token
  ├── POST /auth/request-password-reset
  └── POST /auth/reset-password

📁 Admin (requiere rol ADMIN o MASTER)
  └── GET /admin/...

📁 Master (requiere rol MASTER)
  └── GET /master/...

📁 User (requiere rol USER, ADMIN, o MASTER)
  └── GET /user/...
```

### Información de cada Endpoint

Para cada endpoint, Swagger muestra:

1. **Method y Path**: `POST /auth/token`
2. **Summary**: Descripción corta
3. **Description**: Descripción detallada
4. **Parameters**: Query params, path params, headers
5. **Request Body**: Esquema del body con ejemplos
6. **Responses**: Posibles respuestas con códigos HTTP
7. **Security**: Requerimientos de autenticación

### Schemas (Modelos)

Al final de la página encontrarás la sección **Schemas** con todos los modelos de datos:

- **AuthResponse**: Respuesta de autenticación
- **ErrorResponse**: Respuesta de error
- **PaginatedResponse**: Respuesta paginada
- **MessageResponse**: Respuesta simple
- Y más...

## Probando Endpoints

### Ejemplo 1: Login y Usar Token

```
1. POST /auth/token
   Request Body:
   {
     "email": "david@davscode.com",
     "password": "4dm1n1straT0r123$"
   }
   
   Response (200):
   {
     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "refreshToken": "abc123...",
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

2. Copiar accessToken

3. Click "Authorize" 🔓 y pegar token

4. Ahora puedes probar endpoints protegidos
```

### Ejemplo 2: Registrar Usuario

```
POST /auth/register

Request Body:
{
  "name": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "password": "SecurePass123"
}

Response (200):
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    "id": 6,
    "name": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "role": "USER"
  }
}
```

### Ejemplo 3: Renovar Token

```
POST /auth/refresh-token

Request Body:
{
  "refreshToken": "tu-refresh-token-aqui"
}

Response (200):
{
  "accessToken": "nuevo-access-token...",
  "refreshToken": "nuevo-refresh-token...",
  ...
}
```

### Ejemplo 4: Reset de Contraseña

```
1. POST /auth/request-password-reset
   Request Body:
   {
     "email": "david@davscode.com"
   }
   
   Response (200):
   {
     "message": "Password reset email sent successfully"
   }

2. Revisar email y copiar token

3. POST /auth/reset-password
   Request Body:
   {
     "resetToken": "token-from-email",
     "newPassword": "NewSecurePass123"
   }
   
   Response (200):
   {
     "message": "Password reset successfully"
   }
```

## Anotaciones Swagger

Si estás desarrollando nuevos endpoints, usa estas anotaciones para documentarlos:

### Controller Level

```kotlin
@Tag(name = "Products", description = "Product management endpoints")
@RestController
@RequestMapping("/api/products")
class ProductController {
    // ...
}
```

### Endpoint Level

```kotlin
@Operation(
    summary = "Get all products",
    description = "Returns a paginated list of all products"
)
@ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
    ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
])
@GetMapping
fun getAllProducts(
    @Parameter(description = "Page number (1-indexed)", example = "1")
    @RequestParam(defaultValue = "1") page: Int,
    
    @Parameter(description = "Page size", example = "20")
    @RequestParam(defaultValue = "20") size: Int
): ResponseEntity<PaginatedResponse<Product>> {
    // ...
}
```

### DTO/Model Level

```kotlin
@Schema(description = "Product creation request")
data class CreateProductRequest(
    
    @field:Schema(
        description = "Product name",
        example = "Laptop HP",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    @field:NotBlank
    @field:Size(min = 3, max = 100)
    val name: String,
    
    @field:Schema(
        description = "Product price in USD",
        example = "999.99",
        required = true,
        minimum = "0.01"
    )
    @field:NotNull
    @field:Positive
    val price: Double
)
```

### Endpoint sin Autenticación

Para endpoints públicos que no requieren JWT:

```kotlin
@SecurityRequirements // Removes the Bearer Auth requirement
@GetMapping("/public/info")
fun getPublicInfo(): PublicInfo {
    // ...
}
```

## Personalización

### Modificar la Configuración

Edita `OpenAPIConfig.kt` para personalizar:

```kotlin
@Configuration
class OpenAPIConfig {
    
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Mi API") // ← Título
                    .description("Descripción de mi API") // ← Descripción
                    .version("2.0.0") // ← Versión
                    .contact(
                        Contact()
                            .name("Tu Nombre")
                            .email("tu@email.com")
                    )
            )
            .servers(listOf(
                Server().url("http://localhost:6003").description("Dev"),
                Server().url("https://api.tu-dominio.com").description("Prod")
            ))
            // ... security config
    }
}
```

### Cambiar URL de Swagger

En `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /api-docs  # Cambiar de /v3/api-docs
  swagger-ui:
    path: /docs      # Cambiar de /swagger-ui.html
    enabled: true
```

### Deshabilitar en Producción

En `application-prod.yml`:

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

O con variable de entorno:

```bash
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

### Agrupar por Paquetes

```kotlin
@Configuration
class OpenAPIConfig {
    
    @Bean
    fun groupedOpenApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("admin")
            .pathsToMatch("/admin/**")
            .build()
    }
    
    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/auth/**", "/public/**")
            .build()
    }
}
```

## Tips y Best Practices

### ✅ DO

1. **Documenta todos los endpoints** con `@Operation`
2. **Especifica todas las respuestas posibles** con `@ApiResponses`
3. **Usa `@Schema`** en los DTOs para describir campos
4. **Proporciona ejemplos** en las anotaciones
5. **Organiza con Tags** para mejor navegación
6. **Marca endpoints públicos** con `@SecurityRequirements`

### ❌ DON'T

1. **No expongas Swagger en producción** sin protección
2. **No incluyas datos sensibles** en los ejemplos
3. **No dejes endpoints sin documentar**
4. **No uses descripciones vagas** como "Get data"

### Ejemplos Completos

**Endpoint con Paginación:**

```kotlin
@Operation(summary = "Get all users (paginated)")
@ApiResponses(value = [
    ApiResponse(
        responseCode = "200",
        description = "Users retrieved",
        content = [Content(schema = Schema(implementation = PaginatedUserResponse::class))]
    )
])
@GetMapping
fun getUsers(
    @Parameter(description = "Page number", example = "1")
    @RequestParam(defaultValue = "1") page: Int
): ResponseEntity<PaginatedResponse<User>>
```

**Endpoint con PathVariable:**

```kotlin
@Operation(summary = "Get user by ID")
@GetMapping("/{id}")
fun getUser(
    @Parameter(description = "User ID", required = true, example = "1")
    @PathVariable id: Long
): ResponseEntity<User>
```

**Endpoint con Query Params:**

```kotlin
@Operation(summary = "Search products")
@GetMapping("/search")
fun search(
    @Parameter(description = "Search query", example = "laptop")
    @RequestParam query: String,
    
    @Parameter(description = "Minimum price", example = "100.0")
    @RequestParam(required = false) minPrice: Double?
): ResponseEntity<List<Product>>
```

## Troubleshooting

### Swagger UI no carga

1. Verifica que la aplicación esté corriendo en el puerto correcto
2. Asegúrate de que Swagger no está deshabilitado en configuración
3. Revisa los logs por errores de SpringDoc

### Endpoints no aparecen

1. Verifica que el controller tenga `@RestController`
2. Asegúrate de que el paquete esté siendo escaneado
3. Revisa que los paths estén correctos en `SecurityConfig`

### Autenticación no funciona

1. Verifica que copiaste el token **completo**
2. **No incluyas** el prefijo "Bearer " al pegar el token
3. Asegúrate de que el token no haya expirado (1 hora de validez)
4. Verifica que el usuario tenga el rol necesario

### Cambios no se reflejan

1. Limpia y reconstruye: `./gradlew clean build`
2. Refresca el navegador (Ctrl+F5 o Cmd+Shift+R)
3. Limpia caché del navegador

---

## Summary

Swagger/OpenAPI proporciona:

✅ Documentación interactiva y actualizada  
✅ Prueba de endpoints sin herramientas externas  
✅ Generación automática desde código  
✅ Soporte JWT Bearer out-of-the-box  
✅ Schemas de request/response  
✅ Exportación a JSON/YAML  

**Next:** Ver los demás archivos de documentación en `/docs`.

