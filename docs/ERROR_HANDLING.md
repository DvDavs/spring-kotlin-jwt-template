# Error Handling Guide

Sistema completo de manejo de errores con excepciones personalizadas y respuestas estandarizadas.

## Overview

✅ **20+ excepciones específicas** organizadas por categoría  
✅ **Respuestas estandarizadas** con formato consistente  
✅ **Manejo automático** en `@RestControllerAdvice`  
✅ **Validación de DTOs** con errores detallados  
✅ **Logging automático** para debugging  
✅ **HTTP status codes correctos** automáticos  

## Exception Hierarchy

```
RuntimeException
│
├── ResourceNotFoundException (404)
├── BadRequestException (400)
├── ForbiddenException (403)
├── UnauthorizedException (401)
└── ConflictException (409)
    │
    ├── AuthExceptions.kt
    │   ├── InvalidCredentialsException
    │   ├── AccountDisabledException
    │   ├── AccountBannedException
    │   ├── InvalidTokenException
    │   ├── InvalidRefreshTokenException
    │   ├── InsufficientPermissionsException
    │   └── HierarchyViolationException
    │
    ├── ValidationExceptions.kt
    │   ├── DuplicateEmailException
    │   ├── DuplicateResourceException
    │   ├── ValidationException
    │   ├── MissingFieldException
    │   └── InvalidFormatException
    │
    └── ResourceExceptions.kt
        ├── UserNotFoundException
        ├── EmailNotFoundException
        └── EntityNotFoundException
```

## Error Response Format

### Standard Error

```json
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid email format",
    "path": "/auth/register"
}
```

### Validation Error

```json
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/auth/register",
    "fieldErrors": [
        {
            "field": "email",
            "rejectedValue": "invalid",
            "message": "must be a valid email"
        }
    ]
}
```

## Built-in Exceptions

| Exception | Status | Use Case |
|-----------|--------|----------|
| `InvalidCredentialsException` | 401 | Credenciales inválidas |
| `AccountDisabledException` | 403 | Cuenta deshabilitada |
| `InvalidTokenException` | 401 | Token inválido/expirado |
| `DuplicateEmailException` | 409 | Email ya existe |
| `UserNotFoundException` | 404 | Usuario no encontrado |
| `ValidationException` | 400 | Error de validación |

## Usage Examples

### Example 1: Service Validation

```kotlin
@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUserRequest): User {
        // Validar email duplicado
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateEmailException("Email already registered")
        }
        
        return userRepository.save(user)
    }
}
```

**Respuesta automática (409):**
```json
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 409,
    "error": "Conflict",
    "message": "Email already registered",
    "path": "/api/users"
}
```

### Example 2: Resource Not Found

```kotlin
fun getUser(id: Long): User {
    return userRepository.findById(id)
        .orElseThrow { UserNotFoundException(id) }
}
```

**Respuesta automática (404):**
```json
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 404,
    "error": "Not Found",
    "message": "User with ID 1 not found",
    "path": "/api/users/1"
}
```

### Example 3: DTO Validation

```kotlin
data class CreateUserRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    
    @field:Size(min = 8)
    val password: String
)
```

**Respuesta automática (400):**
```json
{
    "timestamp": "2024-11-17T22:30:45.123Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/users",
    "fieldErrors": [
        {
            "field": "password",
            "rejectedValue": "123",
            "message": "size must be between 8 and 2147483647"
        }
    ]
}
```

## Creating Custom Exceptions

### Step 1: Extend Base Exception

```kotlin
// En tu módulo: modules/products/exception/
class ProductNotFoundException(productId: Long) : ResourceNotFoundException(
    "Product with ID $productId not found"
)

class InsufficientStockException(
    requested: Int,
    available: Int
) : ConflictException(
    "Insufficient stock. Requested: $requested, Available: $available"
)
```

### Step 2: Use in Service

```kotlin
@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun getProduct(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
    }
    
    fun purchase(id: Long, quantity: Int): Purchase {
        val product = getProduct(id)
        
        if (product.stock < quantity) {
            throw InsufficientStockException(quantity, product.stock)
        }
        
        // Process purchase...
    }
}
```

### Step 3: It's Handled Automatically!

El `GeneralExceptionHandler` captura automáticamente la excepción por su clase base y retorna la respuesta apropiada.

## Best Practices

### ✅ DO

1. **Usar excepciones específicas**:
   ```kotlin
   // ✅ BIEN
   throw UserNotFoundException(userId)
   
   // ❌ MAL
   throw RuntimeException("Not found")
   ```

2. **Mensajes descriptivos**:
   ```kotlin
   // ✅ BIEN
   throw DuplicateEmailException("Email ${email} is already registered")
   
   // ❌ MAL
   throw DuplicateEmailException("Duplicate")
   ```

3. **Validar en service layer**:
   ```kotlin
   @Service
   class UserService {
       fun createUser(request: CreateUserRequest): User {
           // ✅ Validación de negocio aquí
           if (userRepository.existsByEmail(request.email)) {
               throw DuplicateEmailException()
           }
       }
   }
   ```

4. **Usar @Valid en DTOs**:
   ```kotlin
   @PostMapping
   fun create(@Valid @RequestBody request: CreateUserRequest): User
   ```

### ❌ DON'T

1. **No retornar null**:
   ```kotlin
   // ❌ MAL
   fun getUser(id: Long): User? {
       return try {
           userRepository.findById(id).orElse(null)
       } catch (e: Exception) {
           null
       }
   }
   
   // ✅ BIEN
   fun getUser(id: Long): User {
       return userRepository.findById(id)
           .orElseThrow { UserNotFoundException(id) }
   }
   ```

2. **No manejar manualmente**:
   ```kotlin
   // ❌ MAL
   @GetMapping("/{id}")
   fun getUser(@PathVariable id: Long): ResponseEntity<*> {
       return try {
           ResponseEntity.ok(userService.getUser(id))
       } catch (e: Exception) {
           ResponseEntity.status(404).body(mapOf("error" to e.message))
       }
   }
   
   // ✅ BIEN - Deja que el handler lo maneje
   @GetMapping("/{id}")
   fun getUser(@PathVariable id: Long): User {
       return userService.getUser(id)
   }
   ```

## Testing

```kotlin
@WebMvcTest(UserController::class)
class UserControllerTest {
    
    @Test
    fun `should return 404 when user not found`() {
        given(userService.getUser(1L))
            .willThrow(UserNotFoundException(1L))
        
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("User with ID 1 not found"))
    }
}
```

## Summary

El sistema de manejo de errores proporciona:

✅ Respuestas consistentes  
✅ Excepciones específicas para casos comunes  
✅ Fácil extensión para casos personalizados  
✅ Logging automático  
✅ Validación automática de DTOs  
✅ Separation of concerns  

Ver también:
- [PAGINATION.md](PAGINATION.md) - Sistema de paginación
- [SWAGGER.md](SWAGGER.md) - Documentación API

