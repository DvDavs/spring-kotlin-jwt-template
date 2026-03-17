# Kotlin Testing Skill

## Description

Testing patterns, best practices, and conventions for Kotlin/JUnit testing in this project.

## Testing Stack

- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: MockK
- **Assertions**: AssertJ
- **Integration Testing**: Spring Boot Test

## Unit Test Structure

### Basic Unit Test

```kotlin
@ExtendWith(MockKExtension::class)
class UserServiceTest {
    
    @Mock
    private lateinit var userRepository: UserRepository
    
    @InjectMockKs
    private lateinit var userService: UserService
    
    @Test
    fun `createUser should save user with encoded password`() {
        // Arrange
        val request = CreateUserRequest(
            email = "test@example.com",
            password = "plainPassword"
        )
        
        every { userRepository.save(any()) } answers { firstArg() }
        
        // Act
        val result = userService.createUser(request)
        
        // Assert
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.password).isNotEqualTo("plainPassword")
        verify { userRepository.save(any()) }
    }
    
    @Test
    fun `createUser should throw when email already exists`() {
        // Arrange
        val request = CreateUserRequest(
            email = "existing@example.com",
            password = "password123"
        )
        
        every { userRepository.existsByEmail("existing@example.com") } returns true
        
        // Act & Assert
        assertThatThrownBy { userService.createUser(request) }
            .isInstanceOf(ValidationException::class.java)
            .hasMessageContaining("Email already exists")
    }
}
```

### Testing Exceptions

```kotlin
@Test
fun `getUserById should throw when user not found`() {
    // Arrange
    every { userRepository.findById(999L) } returns Optional.empty()
    
    // Act & Assert
    assertThatThrownBy { userService.getUserById(999L) }
        .isInstanceOf(EntityNotFoundException::class)
        .hasMessage("User not found with id: 999")
}
```

## Integration Testing

### Controller Integration Test

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }
    
    @Test
    fun `login should return token when credentials are valid`() {
        // Arrange - create user first
        val user = UserEntity(
            email = "test@example.com",
            password = passwordEncoder.encode("password123"),
            role = Role.USER
        )
        userRepository.save(user)
        
        // Act & Assert
        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
            """.trimIndent()
        }
        .andExpect {
            status().isOk()
            jsonPath("$.success").value(true)
            jsonPath("$.data.access_token").exists()
        }
    }
}
```

### Test with Security

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Test
    fun `getUsers should return 401 when not authenticated`() {
        mockMvc.get("/api/v1/users")
            .andExpect {
                status().isUnauthorized()
            }
    }
    
    @Test
    @WithMockUser(authorities = ["PERM_USERS"])
    fun `getUsers should return users when authorized`() {
        mockMvc.get("/api/v1/users")
            .andExpect {
                status().isOk()
            }
    }
}
```

## MockK Patterns

### Mocking Exceptions

```kotlin
@Test
fun `service should propagate exception`() {
    every { repository.findByEmail(any()) } throws DatabaseException("Connection failed")
    
    assertThatThrownBy { service.findByEmail("test@example.com") }
        .isInstanceOf(DatabaseException::class)
}
```

### Capturing Arguments

```kotlin
@Test
fun `save should be called with correct entity`() {
    val slot = slot<UserEntity>()
    every { repository.save(capture(slot)) } answers { firstArg() }
    
    service.createUser(request)
    
    assertThat(slot.captured.email).isEqualTo("test@example.com")
}
```

### Verifying Calls

```kotlin
@Test
fun `cache should be invalidated on user update`() {
    // Act
    service.updateUser(userId, request)
    
    // Assert
    verify { cache.evict("user_$userId") }
}
}

## Test Configuration

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

### Test Class Annotation

```kotlin
@ActiveProfiles("test")
@SpringBootTest
class MyIntegrationTest
```

## Auto-Invoke Rules

This skill is automatically loaded when:
- Writing unit tests
- Writing integration tests
- Creating test data fixtures
- Using MockK or AssertJ

## Related Skills

- `kotlin` - Kotlin language patterns
- `spring-boot` - Spring Boot configuration
- `jwt-auth` - Authentication testing
