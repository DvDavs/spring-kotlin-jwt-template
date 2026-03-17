# JWT Authentication & RBAC Skill

## Description

JWT authentication patterns, RBAC (Role-Based Access Control) implementation, and security best practices for this project.

## Authentication Flow

```
User Login
    │
    ▼
AuthController.login(credentials)
    │
    ▼
UserService.authenticate(email, password)
    │
    ▼ (if valid)
JwtService.generateToken(user)
    │
    ▼
Return: { access_token, expires_in, token_type }
    │
    ▼
Subsequent Requests
    │
    ▼
JwtAuthFilter.extractAndValidateToken
    │
    ▼
SecurityContextHolder.setAuthentication
    │
    ▼
Controller receives authenticated request
```

## JWT Implementation

### Token Generation

```kotlin
@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String,
    
    @Value("\${jwt.expiration}")
    private val expiration: Long
) {
    
    fun generateToken(user: UserEntity): String {
        val now = Instant.now()
        val expiry = now.plusMillis(expiration)
        
        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role.name)
            .claim("permissions", getPermissions(user.role))
            .issuedAt(now)
            .expiration(Date.from(expiry))
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), Jwts.SIG.HS256)
            .compact()
    }
    
    private fun getPermissions(role: Role): List<String> {
        return when (role) {
            Role.MASTER -> listOf("users", "rbac", "admin", "audit")
            Role.ADMIN -> listOf("users", "rbac")
            Role.USER -> emptyList()
        }
    }
}
```

### Token Validation Filter

```kotlin
@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CachedUserService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader?.startsWith("Bearer ") != true) {
            filterChain.doFilter(request, response)
            return
        }
        
        val token = authHeader.substring(7)
        
        try {
            val claims = jwtService.validateAndGetClaims(token)
            val userId = claims.subject.toLong()
            
            val user = userDetailsService.getUserById(userId)
            val authorities = user.role.permissions.map { 
                SimpleGrantedAuthority("PERM_${it.name}") 
            }
            
            val auth = UsernamePasswordAuthenticationToken(
                user, null, authorities
            )
            SecurityContextHolder.getContext().authentication = auth
            
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("""{"error": "Invalid or expired token"}""")
        }
    }
}
```

## RBAC System

### Permission Model

Permissions are the finest-grained access control:

```kotlin
enum class Permission {
    USERS,    // Manage users (create, read, update)
    RBAC,     // Manage roles and permissions
    ADMIN,    // Administrative functions
    AUDIT     // View audit logs
}
```

### Role Model

Roles group multiple permissions:

```kotlin
enum class Role {
    USER,     // No permissions by default
    ADMIN,    // USERS, RBAC permissions
    MASTER    // All permissions
}
```

### Role-Permission Mapping

| Role   | USERS | RBAC | ADMIN | AUDIT |
|--------|-------|------|-------|-------|
| USER   | ❌    | ❌   | ❌    | ❌    |
| ADMIN  | ✅    | ✅   | ❌    | ❌    |
| MASTER | ✅    | ✅   | ✅    | ✅    |

## Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/auth/login` | User login | No |
| POST | `/api/v1/auth/register` | User registration | No |
| POST | `/api/v1/auth/refresh` | Refresh token | Yes |

### User Management

| Method | Endpoint | Description | Required Permission |
|--------|----------|-------------|-------------------|
| GET | `/api/v1/users` | List users | USERS |
| GET | `/api/v1/users/{id}` | Get user | USERS |
| POST | `/api/v1/users` | Create user | USERS |
| PUT | `/api/v1/users/{id}` | Update user | USERS |
| DELETE | `/api/v1/users/{id}` | Delete user | USERS |

### RBAC Management

| Method | Endpoint | Description | Required Permission |
|--------|----------|-------------|-------------------|
| POST | `/api/v1/rbac/users/{userId}/role` | Assign role | RBAC |
| GET | `/api/v1/rbac/users/{userId}/role` | Get user role | RBAC |
| GET | `/api/v1/rbac/users?role=ADMIN` | List users by role | RBAC |

## Cached User Service

For performance, user details are cached:

```kotlin
@Service
class CachedUserService(
    private val userRepository: UserRepository,
    private val cache: CacheManager
) {
    
    fun getUserById(id: Long): UserEntity {
        return cache.get("user_$id") ?: run {
            val user = userRepository.findById(id)
                .orElseThrow { EntityNotFoundException("User not found") }
            cache.put("user_$id", user)
            user
        }
    }
    
    fun invalidateCache(userId: Long) {
        cache.evict("user_$userId")
    }
}
```

## Security Configuration

Permissions are checked at the security filter level:

```kotlin
.authorizeHttpRequests { auth ->
    auth
        .requestMatchers("/api/v1/auth/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasAuthority("PERM_ADMIN")
        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAuthority("PERM_USERS")
        .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAuthority("PERM_USERS")
        .requestMatchers("/api/v1/rbac/**").hasAuthority("PERM_RBAC")
        .anyRequest().authenticated()
}
```

## Auto-Invoke Rules

This skill is automatically loaded when:
- Working with authentication endpoints
- Implementing JWT token generation/validation
- Creating RBAC-related endpoints
- Modifying security configuration

## Related Skills

- `kotlin` - Kotlin language patterns
- `spring-boot` - Spring Boot configuration
- `flyway-migrations` - Database schema
