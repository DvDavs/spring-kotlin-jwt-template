# Spring Boot Skill

## Description

Spring Boot best practices, configuration patterns, and conventions for this project.

## Framework Version

- **Spring Boot**: 3.2.x
- **Spring Security**: 6.2.x
- **Spring Data JPA**: 3.2.x

## Configuration

### Application Properties

Use `application.yml` over `application.properties`:

```yaml
server:
  port: ${APP_PORT:8080}

spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/template}
    username: ${DB_USER:template}
    password: ${DB_PASSWORD:secret}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Security Configuration

Follow the security configuration pattern:

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val handlerExceptionResolver: HandlerExceptionResolver
) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .session { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, ex ->
                    handlerExceptionResolver.resolveException(request, response, null, ex)
                }
            }
            .build()
    }
}
```

## Module Structure

Each feature should follow this structure:

```
modules/[feature-name]/
├── data/
│   ├── repository/
│   │   └── [Feature]Repository.kt
│   └── entity/
│       └── [Feature]Entity.kt
├── domain/
│   ├── model/
│   │   └── [Feature]Model.kt
│   └── usecase/
│       └── [Feature]UseCases.kt
└── presentation/
    ├── dto/
    │   └── [Feature]DTO.kt
    └── controller/
        └── [Feature]Controller.kt
```

## REST API Conventions

### Controller Pattern

```kotlin
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    
    @GetMapping
    fun getUsers(
        @RequestParam(required = false) role: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<UserDTO>>> {
        val users = userService.getUsers(role, page, size)
        return ResponseEntity.ok(ApiResponse.success(users))
    }
    
    @PostMapping
    fun createUser(@Valid @RequestBody dto: CreateUserDTO): ResponseEntity<ApiResponse<UserDTO>> {
        val user = userService.createUser(dto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User created successfully"))
    }
}
```

### Error Handling

Use a global exception handler:

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.message ?: "Validation failed"))
    }
    
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message))
    }
}
```

## Database Conventions

### Flyway Migrations

- migrations go in `src/main/resources/db/migration/`
- naming convention: `V1__description.sql`, `V2__description.sql`
- never modify existing migrations
- always create new migration for schema changes

### JPA Entities

```kotlin
@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,
    
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

## Auto-Invoke Rules

This skill is automatically loaded when:
- Creating Spring Boot configuration
- Working with controllers, services, repositories
- Setting up security
- Configuring database connections

## Related Skills

- `kotlin` - Kotlin language patterns
- `jwt-auth` - JWT authentication
- `flyway-migrations` - Database migrations
- `kotlin-testing` - Testing
