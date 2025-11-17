package mx.evolutiondev.template.modules.auth.presentation.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RequestPasswordResetRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String
)

