package mx.evolutiondev.template.core.error.exception

/**
 * Thrown when user credentials are invalid
 */
class InvalidCredentialsException(message: String = "Invalid credentials") : UnauthorizedException(message)

/**
 * Thrown when user account is disabled
 */
class AccountDisabledException(message: String = "Account is disabled") : ForbiddenException(message)

/**
 * Thrown when user account is banned
 */
class AccountBannedException(message: String = "Account is banned") : ForbiddenException(message)

/**
 * Thrown when JWT token is invalid or expired
 */
class InvalidTokenException(message: String = "Invalid or expired token") : UnauthorizedException(message)

/**
 * Thrown when refresh token is invalid or expired
 */
class InvalidRefreshTokenException(message: String = "Invalid or expired refresh token") : UnauthorizedException(message)

/**
 * Thrown when user tries to access a resource they don't have permission for
 */
class InsufficientPermissionsException(message: String = "Insufficient permissions") : ForbiddenException(message)

/**
 * Thrown when hierarchy validation fails (e.g., ADMIN trying to create MASTER)
 */
class HierarchyViolationException(message: String) : ForbiddenException(message)

