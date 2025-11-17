# Spring Boot + Kotlin Starter Template

A clean, production-ready Spring Boot 3 starter template with JWT authentication, role-based authorization, PostgreSQL database, and Swagger/OpenAPI documentation. Perfect for kickstarting your next Kotlin backend project.

## 📚 Quick Access

- **[Swagger UI](http://localhost:6003/swagger-ui.html)** - Interactive API documentation
- **[Quick Start Guide](docs/QUICK_START.md)** - Get up and running in 5 minutes
- **[Error Handling Guide](docs/ERROR_HANDLING.md)** - Complete error handling documentation
- **[Pagination Guide](docs/PAGINATION.md)** - Pagination implementation guide
- **[Swagger Guide](docs/SWAGGER.md)** - How to use Swagger UI
- **[Auth Endpoints](docs/AUTH_ENDPOINTS.md)** - Authentication endpoints documentation

## 🚀 Technologies

- **Spring Boot 3.4.1** - Modern Java framework
- **Kotlin 1.9.25** - Concise and expressive language
- **PostgreSQL** - Robust relational database
- **Flyway** - Database migrations
- **JWT (jjwt 0.11.5)** - Stateless authentication
- **Spring Security** - Authentication and authorization
- **Spring Mail** - Email sending capabilities
- **Swagger/OpenAPI 3** - Interactive API documentation
- **Docker & Docker Compose** - Containerization
- **Hibernate/JPA** - ORM
- **Gradle** - Build tool
- **Kotlin Coroutines** - Asynchronous programming

## 📁 Project Structure

```
project_template/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── mx/evolutiondev/template/
│   │   │       ├── TemplateApplication.kt          # Main application class
│   │   │       ├── core/                           # Core cross-cutting concerns
│   │   │       │   ├── auth/                       # Authentication system
│   │   │       │   │   ├── cors/                   # CORS configuration
│   │   │       │   │   │   └── AppCorsConfiguration.kt
│   │   │       │   │   ├── jwt/                    # JWT utilities
│   │   │       │   │   │   └── JwtUtil.kt
│   │   │       │   │   ├── ktx/                    # Kotlin extensions
│   │   │       │   │   │   └── _CustomerEntityExtensions.kt
│   │   │       │   │   ├── model/                  # Domain models
│   │   │       │   │   │   ├── Role.kt
│   │   │       │   │   │   ├── AppRole.kt
│   │   │       │   │   │   ├── CustomerDetails.kt
│   │   │       │   │   │   ├── CustomerEntity.kt
│   │   │       │   │   │   └── RefreshTokenEntity.kt
│   │   │       │   │   ├── repository/             # Data access
│   │   │       │   │   │   ├── CustomerRepository.kt
│   │   │       │   │   │   └── RefreshTokenRepository.kt
│   │   │       │   │   ├── security/               # Security config
│   │   │       │   │   │   ├── AuthEntryPointJwt.kt
│   │   │       │   │   │   ├── TemplateAccessDeniedHandler.kt
│   │   │       │   │   │   ├── JwtTokenFilter.kt
│   │   │       │   │   │   └── SecurityConfig.kt
│   │   │       │   │   └── service/                # Business logic
│   │   │       │   │       └── HierarchyValidationService.kt
│   │   │       │   ├── email/                      # Email system
│   │   │       │   │   ├── model/
│   │   │       │   │   │   ├── EmailRequest.kt
│   │   │       │   │   │   └── EmailContent.kt
│   │   │       │   │   ├── strategy/
│   │   │       │   │   │   ├── EmailSenderStrategy.kt
│   │   │       │   │   │   └── GoogleSmtpStrategy.kt
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   └── EmailExceptions.kt
│   │   │       │   │   ├── EmailService.kt
│   │   │       │   │   ├── EmailContentGenerator.kt
│   │   │       │   │   └── MailConfig.kt
│   │   │       │   ├── config/                     # Configuration
│   │   │       │   │   └── OpenAPIConfig.kt
│   │   │       │   ├── error/                      # Error handling
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   ├── BaseExceptions.kt
│   │   │       │   │   │   ├── AuthExceptions.kt
│   │   │       │   │   │   ├── ValidationExceptions.kt
│   │   │       │   │   │   └── ResourceExceptions.kt
│   │   │       │   │   ├── model/
│   │   │       │   │   │   └── ErrorMessage.kt
│   │   │       │   │   └── GeneralExceptionHandler.kt
│   │   │       │   ├── model/                      # Common models
│   │   │       │   │   ├── PaginatedResponse.kt
│   │   │       │   │   └── MessageResponse.kt
│   │   │       │   └── util/                       # Utilities
│   │   │       │       ├── formatter/
│   │   │       │       │   └── DateFormatter.kt
│   │   │       │       └── PaginationUtils.kt
│   │   │       └── modules/                        # Business modules
│   │   │           ├── auth/                       # Auth endpoints (to be implemented)
│   │   │           ├── admin/                      # Admin endpoints
│   │   │           ├── master/                     # Master endpoints
│   │   │           ├── user/                       # User endpoints
│   │   │           ├── public/                     # Public endpoints
│   │   │           └── shared/                     # Shared code
│   │   └── resources/
│   │       ├── application.yml                     # Configuration
│   │       └── db/
│   │           └── migration/
│   │               └── V1__initial_auth_schema.sql # Initial DB schema
│   └── test/
│       └── kotlin/
│           └── mx/evolutiondev/template/
├── docs/                                            # Documentation
│   ├── AUTH_ENDPOINTS.md                            # Auth endpoints guide
│   ├── ERROR_HANDLING.md                            # Error handling guide
│   ├── PAGINATION.md                                # Pagination guide
│   ├── SWAGGER.md                                   # Swagger/OpenAPI guide
│   └── QUICK_START.md                               # Quick start guide
├── build.gradle.kts                                 # Build configuration
├── settings.gradle.kts                              # Project settings
├── gradle.properties                                # Gradle properties
├── Dockerfile                                       # Docker image
├── docker-compose.yml                               # Docker orchestration
└── .gitignore                                       # Git ignore rules
```

## 🏗️ Architecture

This template follows **Clean Architecture** principles with a modular structure:

### Core Layer (`core/`)

Contains cross-cutting concerns used across the application:

- **auth/**: Complete JWT authentication and authorization system
- **config/**: Application configuration (OpenAPI, etc.)
- **email/**: Email service with template system and strategy pattern
- **error/**: Centralized error handling with custom exceptions and standardized responses
- **model/**: Common models (PaginatedResponse, MessageResponse, etc.)
- **util/**: Shared utilities (DateFormatter, PaginationUtils, etc.)

**Key Principle**: Core modules don't depend on business modules.

### Modules Layer (`modules/`)

Organized by role or functionality:

```
modules/
└── [role]/                    # admin, master, user, public, shared
    └── [feature]/             # users, products, orders, etc.
        ├── ktx/               # Kotlin extensions specific to feature
        ├── presentation/      # Controllers and DTOs
        │   ├── request/       # Request DTOs
        │   ├── response/      # Response DTOs
        │   └── [Feature]Controller.kt
        ├── service/           # Business logic
        │   ├── model/         # Internal domain models
        │   ├── filter/        # Filters and specifications
        │   └── [Feature]Service.kt
        └── repository/        # Data access (if needed)
            ├── model/         # JPA entities
            └── [Feature]Repository.kt
```

**Example Module Structure** (`modules/admin/users/`):

- **presentation/**: REST controllers and DTOs
- **service/**: Business logic and validations
- **repository/**: Database access

### Separation of Concerns

- **Presentation Layer**: HTTP handling, validation, DTOs
- **Service Layer**: Business logic, transactions
- **Repository Layer**: Data access, queries

**Dependency Flow**: `Presentation → Service → Repository`

## 🔐 Authentication & Authorization

### ✅ Endpoints Implementados

Los siguientes endpoints de autenticación están completamente implementados:

- ✅ `POST /auth/token` - Login (obtener access token)
- ✅ `POST /auth/register` - Registrar nuevo usuario
- ✅ `POST /auth/refresh-token` - Renovar access token
- ✅ `POST /auth/request-password-reset` - Solicitar reset de contraseña
- ✅ `POST /auth/reset-password` - Restablecer contraseña

**📄 Documentación completa:** Ver [docs/AUTH_ENDPOINTS.md](docs/AUTH_ENDPOINTS.md)

**📮 Colección Postman:** `Template_Auth_Endpoints.postman_collection.json`

**🔗 Swagger UI:** [http://localhost:6003/swagger-ui.html](http://localhost:6003/swagger-ui.html)

### Role Hierarchy

```
MASTER (highest authority)
  ├── Can create: MASTER, ADMIN, USER
  ├── Can access: All users
  └── Can manage: All resources

ADMIN (middle authority)
  ├── Can create: USER
  ├── Can access: Users created by them
  └── Can manage: Own resources and created users' resources

USER (lowest authority)
  ├── Can create: Nothing
  ├── Can access: Only themselves
  └── Can manage: Only own resources
```

### JWT Configuration

- **Token Expiration**: 1 hour (configurable)
- **Issuer**: evolutiondevmx
- **Secret**: Configured via `JWT_SECRET` environment variable
- **Refresh Tokens**: Stored in database with 7-day expiration

### Security Endpoints

The template includes preconfigured security rules:

```kotlin
/auth/**              -> Permit all (authentication endpoints)
/public/**            -> Permit all (public endpoints)
/admin/**             -> ADMIN, MASTER roles
/master/**            -> MASTER role only
/user/**              -> USER, ADMIN, MASTER roles
```

## 🛡️ Error Handling

La plantilla incluye un sistema robusto de manejo de errores con excepciones personalizadas y respuestas estandarizadas.

### Features

- ✅ **Excepciones específicas** por tipo (Auth, Validation, Resources)
- ✅ **Respuestas consistentes** con formato estandarizado
- ✅ **Manejo automático** en `@RestControllerAdvice`
- ✅ **Validación de DTOs** con errores detallados por campo
- ✅ **Logging automático** para debugging
- ✅ **HTTP status codes** correctos para cada tipo de error

### Quick Example

```kotlin
// En tu service
fun getUser(id: Long): User {
    return userRepository.findById(id)
        .orElseThrow { UserNotFoundException(id) }
}

// El handler convierte automáticamente a:
// HTTP 404 con:
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 404,
    "error": "Not Found",
    "message": "User with ID 1 not found",
    "path": "/api/users/1"
}
```

**📚 Documentación completa:** Ver [docs/ERROR_HANDLING.md](docs/ERROR_HANDLING.md)

---

## 📄 Pagination

Sistema completo de paginación con metadatos enriquecidos y utilities helpers.

### Features

- ✅ **Formato consistente** para todas las respuestas
- ✅ **Metadatos completos** (hasNext, hasPrevious, totalPages, etc.)
- ✅ **Utilities** para simplificar implementación
- ✅ **Extension functions** para código limpio
- ✅ **Integración con Spring Data** JPA

### Quick Example

```kotlin
@Service
class ProductService(private val productRepository: ProductRepository) {
    
    fun getAllProducts(page: Int): PaginatedResponse<Product> {
        val pageable = PaginationUtils.createPageable(page)
        val result = productRepository.findAll(pageable)
        return result.toPaginatedResponse(page) { it.toDTO() }
    }
}

// Respuesta:
{
    "content": [ /* items */ ],
    "page": {
        "number": 1,
        "size": 20,
        "totalElements": 150,
        "totalPages": 8,
        "hasNext": true,
        "hasPrevious": false,
        "isFirst": true,
        "isLast": false
    }
}
```

**📚 Documentación completa:** Ver [docs/PAGINATION.md](docs/PAGINATION.md)

---

## 📖 Swagger/OpenAPI Documentation

La plantilla incluye documentación interactiva con Swagger UI.

### Features

- ✅ **Interfaz interactiva** para explorar y probar la API
- ✅ **Documentación auto-generada** desde código
- ✅ **Soporte JWT Bearer** integrado
- ✅ **Schemas de request/response** completos
- ✅ **Try it out** - Prueba endpoints directamente
- ✅ **Exportación JSON/YAML** del spec OpenAPI

### Quick Access

- **Swagger UI**: [http://localhost:6003/swagger-ui.html](http://localhost:6003/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:6003/v3/api-docs](http://localhost:6003/v3/api-docs)
- **OpenAPI YAML**: [http://localhost:6003/v3/api-docs.yaml](http://localhost:6003/v3/api-docs.yaml)

### Uso Rápido

1. Abre [http://localhost:6003/swagger-ui.html](http://localhost:6003/swagger-ui.html)
2. Haz login con `POST /auth/token` para obtener tu access token
3. Click en **Authorize** 🔓 y pega tu token
4. ¡Prueba cualquier endpoint directamente desde el navegador!

**📚 Guía completa:** Ver [docs/SWAGGER.md](docs/SWAGGER.md)

---

## 📧 Email System

La plantilla incluye un sistema completo de envío de emails para funcionalidades como reset de contraseña:

### Features

- ✅ **Envío asíncrono**: Usa Kotlin coroutines para no bloquear el thread principal
- ✅ **Plantillas HTML**: Sistema de templates para emails profesionales
- ✅ **Strategy pattern**: Fácil de extender para otros proveedores (MailerSend, SendGrid, etc.)
- ✅ **Gmail SMTP ready**: Configurado para Gmail out-of-the-box
- ✅ **Fallback a texto plano**: Siempre envía versión HTML y texto

### Estructura

```
core/email/
├── model/
│   ├── EmailRequest.kt           # DTO para email
│   └── EmailContent.kt           # Subject + HTML + Text
├── strategy/
│   ├── EmailSenderStrategy.kt    # Interface
│   └── GoogleSmtpStrategy.kt     # Implementación Gmail
├── exception/
│   └── EmailExceptions.kt        # Custom exceptions
├── EmailService.kt               # Servicio principal
├── EmailContentGenerator.kt      # Generador de templates
└── MailConfig.kt                 # Configuración Spring Mail
```

### Uso

```kotlin
// En tu servicio
@Service
class YourService(
    private val emailService: EmailService
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun sendPasswordReset(email: String, token: String) {
        coroutineScope.launch {
            emailService.sendEmail(
                to = email,
                templateName = "password-reset",
                variables = mapOf(
                    "customerName" to "Juan",
                    "resetUrl" to "https://app.com/reset?token=$token"
                )
            )
        }
    }
}
```

### Agregar un Nuevo Template

1. Edita `EmailContentGenerator.kt`
2. Agrega tu caso en el `when`:

```kotlin
fun generate(emailType: String, variables: Map<String, Any>): EmailContent {
    return when (emailType) {
        "password-reset" -> generatePasswordResetContent(variables)
        "welcome" -> generateWelcomeContent(variables)  // ← Nuevo
        else -> throw IllegalArgumentException("Unknown email type: $emailType")
    }
}

private fun generateWelcomeContent(variables: Map<String, Any>): EmailContent {
    val name = variables["name"] as? String ?: "Usuario"
    // ... tu lógica HTML
}
```

### Cambiar de Proveedor

Para usar otro proveedor (MailerSend, SendGrid):

1. Crea una nueva clase que implemente `EmailSenderStrategy`
2. Anota con `@ConditionalOnProperty(name = ["email.provider"], havingValue = "tuproveedor")`
3. Configura `EMAIL_PROVIDER=tuproveedor`

## ⚙️ Prerequisites

- **Java 17** or higher
- **Docker** and **Docker Compose**
- **Gradle** (wrapper included)

## 👥 Default Users

La migración inicial crea usuarios de ejemplo para pruebas:

| Email | Password | Role | Descripción |
|-------|----------|------|-------------|
| `david@davscode.com` | `4dm1n1straT0r123$` | MASTER | Usuario con máxima autoridad |
| `denisse@davscode.com` | `4dm1n1straT0r123$` | ADMIN | Administrador |
| `moises@davscode.com` | `4dm1n1straT0r123$` | ADMIN | Administrador |
| `carlos.garcia@davscode.com` | `4dm1n1straT0r123$` | USER | Usuario regular (creado por Denisse) |
| `ana.martinez@davscode.com` | `4dm1n1straT0r123$` | USER | Usuario regular (creado por Denisse) |

> ⚠️ **IMPORTANTE**: Cambia estas contraseñas en producción. Son solo para desarrollo y pruebas.

## 🚀 Quick Start

### 1. Environment Variables

Create a `.env` file in the project root (or set these variables in your environment):

```bash
# Required
JWT_SECRET=your-very-secure-secret-key-here-at-least-256-bits

# Email Configuration (Required for password reset functionality)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Optional
FRONTEND_URL=http://localhost:3000  # URL of your frontend application
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
EMAIL_PROVIDER=google

# Database (defaults provided in docker-compose.yml)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/template_db
SPRING_DATASOURCE_USERNAME=evolution
SPRING_DATASOURCE_PASSWORD=evolution123
```

#### 📧 Configuración de Email (Gmail)

Para usar Gmail como proveedor de email:

1. **Habilita la verificación en 2 pasos** en tu cuenta de Google
2. **Genera una contraseña de aplicación**:
   - Ve a: https://myaccount.google.com/apppasswords
   - Selecciona "Correo" y el dispositivo
   - Copia la contraseña generada (16 caracteres)
3. **Configura las variables de entorno**:
   - `MAIL_USERNAME`: Tu dirección de Gmail completa
   - `MAIL_PASSWORD`: La contraseña de aplicación generada (sin espacios)

> ⚠️ **Nota**: Nunca uses tu contraseña real de Gmail. Siempre usa una contraseña de aplicación.

### 2. Start with Docker Compose (Recomendado)

**Opción A: Con construcción desde cero**
```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

**Opción B: Sin reconstruir (si ya construiste antes)**
```bash
docker-compose up
```

**Ver logs:**
```bash
docker-compose logs -f spring-app
```

**Detener servicios:**
```bash
docker-compose down

# Para eliminar también los volúmenes (base de datos)
docker-compose down -v
```

**La aplicación estará disponible en:** `http://localhost:6003`

**Base de datos PostgreSQL:**
- Host: `localhost:5501`
- Database: `template_db`
- Usuario: `evolution`
- Password: `evolution123`

### 3. Local Development (sin Docker para la app)

**Paso 1: Iniciar solo PostgreSQL:**
```bash
docker-compose up postgres-db
```

**Paso 2: Configurar variables de entorno:**

**En Unix/Linux/Mac:**
```bash
export JWT_SECRET="your-very-secure-secret-key-here-at-least-256-bits"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5501/template_db"
export SPRING_DATASOURCE_USERNAME="evolution"
export SPRING_DATASOURCE_PASSWORD="evolution123"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export FRONTEND_URL="http://localhost:3000"
```

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

**Paso 3: Ejecutar la aplicación:**
```bash
# Unix/Linux/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### 4. Verificar que está funcionando

**Opción 1: Usar curl**
```bash
curl http://localhost:6003/actuator/health
```

**Opción 2: Probar un endpoint (debería dar 401 Unauthorized sin autenticación)**
```bash
curl http://localhost:6003/admin/users
```

### 5. Probar Autenticación ✅

Los endpoints de autenticación ya están implementados y listos para usar:

**Con curl:**
```bash
# Login
curl -X POST http://localhost:6003/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "david@davscode.com",
    "password": "4dm1n1straT0r123$"
  }'

# Registrar nuevo usuario
curl -X POST http://localhost:6003/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@davscode.com",
    "password": "MySecurePassword123"
  }'
```

**Con Postman:**
1. Importa el archivo `Template_Auth_Endpoints.postman_collection.json`
2. La colección incluye todos los endpoints con ejemplos
3. Los tokens se guardan automáticamente (no necesitas copiar/pegar)

**Ver documentación completa:** [AUTH_ENDPOINTS.md](AUTH_ENDPOINTS.md)

### 6. Run Tests

```bash
# Unix/Linux/Mac
./gradlew test

# Windows
gradlew.bat test
```

### 7. Comandos Útiles

**Limpiar y reconstruir:**
```bash
# Unix/Linux/Mac
./gradlew clean build

# Windows
gradlew.bat clean build
```

**Ver todas las tareas disponibles:**
```bash
./gradlew tasks
```

**Verificar dependencias:**
```bash
./gradlew dependencies
```

## 📊 Database Management

### Flyway Migrations

Migrations are located in `src/main/resources/db/migration/`

**Create a new migration:**

1. Create a new file: `V2__your_description.sql`
2. Add your SQL:

```sql
CREATE TABLE products (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    price double precision NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone
);

ALTER TABLE products ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
```

3. Restart the application - Flyway will automatically run new migrations

**Flyway Commands:**
```bash
# Check migration status
./gradlew flywayInfo

# Migrate database
./gradlew flywayMigrate

# Validate migrations
./gradlew flywayValidate
```

### Database Schema

The initial schema includes:

- **customer**: User accounts with authentication fields
- **refresh_tokens**: JWT refresh tokens

## 🛠️ Extending the Template

### Adding a New Module

**Example**: Creating a Products module for ADMIN users

#### 1. Create Directory Structure

```
modules/admin/products/
├── ktx/
├── presentation/
│   ├── request/
│   ├── response/
│   └── ProductController.kt
├── service/
│   └── ProductService.kt
└── repository/
    ├── model/
    │   └── ProductEntity.kt
    └── ProductRepository.kt
```

#### 2. Create Entity

```kotlin
// repository/model/ProductEntity.kt
package mx.evolutiondev.template.modules.admin.products.repository.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    
    var name: String = "",
    var price: Double = 0.0,
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant? = null,
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null
)
```

#### 3. Create Repository

```kotlin
// repository/ProductRepository.kt
package mx.evolutiondev.template.modules.admin.products.repository

import mx.evolutiondev.template.modules.admin.products.repository.model.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByName(name: String): ProductEntity?
}
```

#### 4. Create DTOs

```kotlin
// presentation/request/CreateProductRequest.kt
package mx.evolutiondev.template.modules.admin.products.presentation.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateProductRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    
    @field:Positive(message = "Price must be positive")
    val price: Double
)

// presentation/response/ProductResponse.kt
package mx.evolutiondev.template.modules.admin.products.presentation.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Double
)
```

#### 5. Create Service

```kotlin
// service/ProductService.kt
package mx.evolutiondev.template.modules.admin.products.service

import mx.evolutiondev.template.modules.admin.products.presentation.request.CreateProductRequest
import mx.evolutiondev.template.modules.admin.products.repository.ProductRepository
import mx.evolutiondev.template.modules.admin.products.repository.model.ProductEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    @Transactional
    fun createProduct(request: CreateProductRequest): ProductEntity {
        val product = ProductEntity(
            name = request.name,
            price = request.price
        )
        return productRepository.save(product)
    }
    
    fun getProduct(id: Long): ProductEntity? {
        return productRepository.findById(id).orElse(null)
    }
    
    fun getAllProducts(): List<ProductEntity> {
        return productRepository.findAll()
    }
}
```

#### 6. Create Controller

```kotlin
// presentation/ProductController.kt
package mx.evolutiondev.template.modules.admin.products.presentation

import jakarta.validation.Valid
import mx.evolutiondev.template.modules.admin.products.presentation.request.CreateProductRequest
import mx.evolutiondev.template.modules.admin.products.presentation.response.ProductResponse
import mx.evolutiondev.template.modules.admin.products.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/products")
class ProductController(
    private val productService: ProductService
) {
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        val response = ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProduct(id) ?: return ResponseEntity.notFound().build()
        val response = ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
        val responses = products.map { ProductResponse(it.id, it.name, it.price) }
        return ResponseEntity.ok(responses)
    }
}
```

#### 7. Create Database Migration

Create `V2__create_products_table.sql`:

```sql
CREATE TABLE public.products (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    price double precision NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone
);

ALTER TABLE public.products ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
```

## 📝 Best Practices

### Naming Conventions

- **Controllers**: `[Feature]Controller.kt`
- **Services**: `[Feature]Service.kt`
- **Repositories**: `[Feature]Repository.kt`
- **Entities**: `[Feature]Entity.kt`
- **DTOs**: `[Action][Feature]Request/Response.kt`
- **Extensions**: `_[EntityName]Extensions.kt`

### Package Organization

- Group by **role** first (`admin`, `master`, `user`, `public`)
- Then by **feature** (`users`, `products`, `orders`)
- Use `shared/` for code common across roles
- Use `ktx/` for Kotlin extensions

### Security

- Always use `@PreAuthorize` on controller methods
- Validate role hierarchies in services using `HierarchyValidationService`
- Never trust client data - validate everything
- Use DTOs instead of exposing entities directly

### Validation

- Use Bean Validation annotations in DTOs (`@NotBlank`, `@Positive`, etc.)
- Implement business validation in services
- Return clear, consistent error messages

### Transactions

- Use `@Transactional` on service methods that modify data
- Don't use `@Transactional` on controllers
- Keep transactions short and focused

### Testing

- Write unit tests for services
- Write integration tests for controllers
- Use H2 for test database
- Mock external dependencies

## 🔧 Configuration

### application.yml

Key configurations:

```yaml
server:
  port: 6003

template:
  jwt:
    secret: ${JWT_SECRET}           # Set via environment variable
    issuer: evolutiondevmx
    expiration: 3600000             # 1 hour in milliseconds

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate            # Never use 'update' in production
    show-sql: true                  # Set to false in production
  
  flyway:
    enabled: true
    baseline-on-migrate: true
```

### Docker Configuration

#### Dockerfile

Multi-stage build for optimized image size:

- **Stage 1**: Build with Gradle
- **Stage 2**: Runtime with JRE only

#### docker-compose.yml

Services:

- **postgres-db**: PostgreSQL 13
- **spring-app**: Spring Boot application

Volumes:

- `postgres_data`: Database persistence
- `gradle-cache`: Build cache

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Spring Security](https://spring.io/projects/spring-security)
- [Flyway](https://flywaydb.org/documentation/)
- [JWT Introduction](https://jwt.io/introduction)

## 🤝 Contributing

Feel free to fork this template and adapt it to your needs!

## 📄 License

This template is open source and available under the MIT License.

---

**Happy Coding! 🚀**

