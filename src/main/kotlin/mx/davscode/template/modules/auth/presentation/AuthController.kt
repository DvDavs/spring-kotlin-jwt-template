package mx.evolutiondev.template.modules.auth.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mx.evolutiondev.template.modules.auth.presentation.request.*
import mx.evolutiondev.template.modules.auth.presentation.response.AuthResponse
import mx.evolutiondev.template.modules.auth.presentation.response.MessageResponse
import mx.evolutiondev.template.modules.auth.service.AuthService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(
        summary = "User login",
        description = "Authenticate user and get JWT access token and refresh token"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Account disabled or banned",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        )
    ])
    @SecurityRequirements // No security required for login
    @PostMapping("/token")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Register new user",
        description = "Register a new user account with USER role by default"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully registered"),
        ApiResponse(responseCode = "409", description = "Email already exists"),
        ApiResponse(responseCode = "400", description = "Validation failed")
    ])
    @SecurityRequirements // No security required for registration
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Refresh access token",
        description = "Get a new access token using a valid refresh token. The old refresh token is invalidated."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    ])
    @SecurityRequirements // No security required for token refresh
    @PostMapping("/refresh-token")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Request password reset",
        description = "Send a password reset email with a secure token. Token is valid for 15 minutes."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Password reset email sent"),
        ApiResponse(responseCode = "404", description = "Email not found")
    ])
    @SecurityRequirements // No security required for password reset request
    @PostMapping("/request-password-reset")
    fun requestPasswordReset(@Valid @RequestBody request: RequestPasswordResetRequest): ResponseEntity<MessageResponse> {
        val message = authService.requestPasswordReset(request.email)
        return ResponseEntity.ok(MessageResponse(message))
    }

    @Operation(
        summary = "Reset password",
        description = "Reset user password using the token from the reset email"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Password reset successfully"),
        ApiResponse(responseCode = "401", description = "Invalid or expired reset token")
    ])
    @SecurityRequirements // No security required for password reset
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<MessageResponse> {
        authService.resetPassword(request.resetToken, request.newPassword)
        return ResponseEntity.ok(MessageResponse("Password reset successfully"))
    }
}

