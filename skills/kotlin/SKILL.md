# Kotlin Skill

## Description

Patterns and best practices for Kotlin development in this project. This skill is auto-loaded for all Kotlin-related work.

## Language Version

- **Kotlin**: 1.9.x
- **Java**: 17+

## Coding Standards

### Data Classes

Use `data class` for DTOs and entities that hold data:

```kotlin
// ✅ Correcto - data class para DTOs
data class UserDTO(
    val id: Long,
    val email: String,
    val role: String
)

// ✅ Correcto - regular class para entidades JPA
@Entity
class UserEntity {
    @Id @GeneratedValue
    var id: Long = 0
    
    @Column(unique = true)
    lateinit var email: String
    
    @Enumerated(EnumType.STRING)
    lateinit var role: Role
}
```

### Null Safety

- NEVER use `!!` operator - it defeats the purpose of Kotlin's null safety
- Use safe calls (`?.`) and elvis operator (`?:`) instead
- Prefer `lateinit` over nullable types for dependency-injected properties

```kotlin
// ❌ Incorrecto
val name: String? = user?.name ?: throw IllegalStateException("Name required")

// ✅ Correcto
val name: String = user.name ?: "Unknown"
```

### Repository Pattern

Follow the repository pattern with Spring Data:

```kotlin
@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun existsByEmail(email: String): Boolean
}
```

### Service Layer

Services should be transactional and use constructor injection:

```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(dto: CreateUserDTO): UserEntity {
        // implementation
    }
}
```

## Project Structure

```
src/main/kotlin/mx/dvscode/template/
├── core/                    # Core infrastructure
│   ├── auth/               # Authentication (JWT, Security)
│   ├── config/             # Configuration classes
│   └── exception/          # Custom exceptions
├── modules/                # Feature modules
│   └── [module-name]/
│       ├── data/           # Repository, Entity
│       ├── domain/         # Use cases, Models
│       └── presentation/   # Controllers, DTOs
└── TemplateApplication.kt
```

## Common Patterns

### DTOs with Validation

```kotlin
data class CreateUserRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String
)
```

### Response Wrapper

Always wrap API responses consistently:

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
    val timestamp: Long = System.currentTimeMillis()
)
```

## Auto-Invoke Rules

This skill is automatically loaded when:
- Creating new Kotlin files
- Modifying existing Kotlin code
- Working with Spring Data repositories
- Implementing services or controllers

## Related Skills

- `spring-boot` - Spring Boot specific patterns
- `jwt-auth` - JWT authentication and RBAC
- `flyway-migrations` - Database migrations
- `kotlin-testing` - Testing patterns
