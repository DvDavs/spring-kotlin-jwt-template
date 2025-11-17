package mx.evolutiondev.template.modules.auth.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mx.evolutiondev.template.core.auth.jwt.JwtUtil
import mx.evolutiondev.template.core.auth.model.CustomerEntity
import mx.evolutiondev.template.core.auth.model.RefreshTokenEntity
import mx.evolutiondev.template.core.auth.model.Role
import mx.evolutiondev.template.core.auth.repository.CustomerRepository
import mx.evolutiondev.template.core.auth.repository.RefreshTokenRepository
import mx.evolutiondev.template.core.email.EmailService
import mx.evolutiondev.template.core.error.exception.*
import mx.evolutiondev.template.modules.auth.presentation.request.LoginRequest
import mx.evolutiondev.template.modules.auth.presentation.request.RegisterRequest
import mx.evolutiondev.template.modules.auth.presentation.response.AuthResponse
import mx.evolutiondev.template.modules.auth.presentation.response.UserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthService(
    private val customerRepository: CustomerRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val emailService: EmailService,
    @Value("\${template.frontend.url}") private val frontendUrl: String
) {
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val customer = customerRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException("Invalid email or password")

        if (!customer.isEnabled) {
            throw AccountDisabledException("Your account has been disabled. Please contact support.")
        }

        if (customer.isBanned) {
            throw AccountBannedException("Your account has been banned. Please contact support.")
        }

        if (!passwordEncoder.matches(request.password, customer.password)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        return generateAuthResponse(customer)
    }

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (customerRepository.findByEmail(request.email) != null) {
            throw DuplicateEmailException("Email ${request.email} is already registered. Please use a different email.")
        }

        val customer = CustomerEntity(
            name = request.name,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = Role.USER,
            isEnabled = true,
            isBanned = false
        )

        val savedCustomer = customerRepository.save(customer)
        return generateAuthResponse(savedCustomer)
    }

    @Transactional
    fun refreshToken(refreshToken: String): AuthResponse {
        val tokenEntity = refreshTokenRepository.findByToken(refreshToken)
            ?: throw InvalidRefreshTokenException("Invalid or expired refresh token")

        if (tokenEntity.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(tokenEntity)
            throw InvalidRefreshTokenException("Refresh token has expired. Please login again.")
        }

        val customer = tokenEntity.customer
        if (!customer.isEnabled) {
            throw AccountDisabledException("Your account has been disabled. Please contact support.")
        }

        if (customer.isBanned) {
            throw AccountBannedException("Your account has been banned. Please contact support.")
        }

        // Delete old refresh token and create new one
        refreshTokenRepository.delete(tokenEntity)

        return generateAuthResponse(customer)
    }

    @Transactional
    fun requestPasswordReset(email: String): String {
        val customer = customerRepository.findByEmail(email)
            ?: throw EmailNotFoundException(email)

        val resetToken = jwtUtil.generateSecureToken()
        customer.resetToken = resetToken
        customer.resetTokenExpiry = Instant.now().plus(15, ChronoUnit.MINUTES)
        customerRepository.save(customer)

        // Send password reset email asynchronously
        coroutineScope.launch {
            emailService.sendEmail(
                to = email,
                templateName = "password-reset",
                variables = mapOf(
                    "customerName" to customer.name,
                    "resetUrl" to "$frontendUrl/reset-password?token=$resetToken"
                )
            )
        }

        return "Password reset email sent successfully"
    }

    @Transactional
    fun resetPassword(resetToken: String, newPassword: String) {
        val customer = customerRepository.findByResetToken(resetToken)
            ?: throw InvalidTokenException("Invalid or expired password reset token")

        if (customer.resetTokenExpiry == null || customer.resetTokenExpiry!!.isBefore(Instant.now())) {
            throw InvalidTokenException("Password reset token has expired. Please request a new one.")
        }

        customer.password = passwordEncoder.encode(newPassword)
        customer.resetToken = null
        customer.resetTokenExpiry = null
        customerRepository.save(customer)
    }

    private fun generateAuthResponse(customer: CustomerEntity): AuthResponse {
        val accessToken = jwtUtil.generateToken(customer.id, customer.email, customer.role.name)

        // Create refresh token
        val refreshToken = jwtUtil.generateSecureToken()
        val refreshTokenEntity = RefreshTokenEntity(
            token = refreshToken,
            expiryDate = Instant.now().plus(7, ChronoUnit.DAYS),
            customer = customer
        )
        refreshTokenRepository.save(refreshTokenEntity)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 3600, // 1 hour in seconds
            user = UserInfo(
                id = customer.id,
                name = customer.name,
                lastName = customer.lastName,
                email = customer.email,
                role = customer.role.name
            )
        )
    }
}

