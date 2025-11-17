package mx.evolutiondev.template.core.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI (Swagger) Configuration
 * 
 * Configures Swagger UI with JWT Bearer authentication support
 * 
 * Access Swagger UI at: http://localhost:6003/swagger-ui.html
 * OpenAPI JSON at: http://localhost:6003/v3/api-docs
 */
@Configuration
class OpenAPIConfig {

    @Value("\${server.port:6003}")
    private val serverPort: String = "6003"

    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "Bearer Authentication"
        
        return OpenAPI()
            .info(apiInfo())
            .servers(listOf(
                Server().url("http://localhost:$serverPort").description("Local Development Server"),
                Server().url("https://api.example.com").description("Production Server")
            ))
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT Bearer token **_only_** (without 'Bearer ' prefix)")
                    )
            )
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Template API")
            .description("""
                ## Spring Boot + Kotlin REST API Template
                
                This is a production-ready REST API template with:
                - 🔐 JWT Authentication & Authorization
                - 👥 Role-based access control (MASTER, ADMIN, USER)
                - 📧 Email service with templates
                - 🛡️ Centralized error handling
                - 📄 Pagination support
                - 📝 Complete API documentation
                
                ### Authentication
                
                1. Login with POST `/auth/token` to get your access token
                2. Click the **Authorize** button (🔓) at the top right
                3. Enter your token (without 'Bearer ' prefix)
                4. Click **Authorize** to apply to all requests
                
                ### Default Users
                
                | Email | Password | Role |
                |-------|----------|------|
                | david@davscode.com | 4dm1n1straT0r123$ | MASTER |
                | denisse@davscode.com | 4dm1n1straT0r123$ | ADMIN |
                | carlos.garcia@davscode.com | 4dm1n1straT0r123$ | USER |
                
                ### Useful Links
                
                - [GitHub Repository](https://github.com/yourusername/template)
                - [Documentation](http://localhost:$serverPort/docs)
                - [Postman Collection](http://localhost:$serverPort/Template_Auth_Endpoints.postman_collection.json)
            """.trimIndent())
            .version("1.0.0")
            .contact(
                Contact()
                    .name("API Support")
                    .email("support@example.com")
                    .url("https://example.com")
            )
            .license(
                License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
            )
    }
}

