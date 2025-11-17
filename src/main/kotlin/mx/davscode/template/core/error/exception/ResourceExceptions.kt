package mx.evolutiondev.template.core.error.exception

/**
 * Thrown when a user is not found
 */
class UserNotFoundException(userId: Long? = null) : ResourceNotFoundException(
    userId?.let { "User with ID $it not found" } ?: "User not found"
)

/**
 * Thrown when an email is not found in the system
 */
class EmailNotFoundException(email: String) : ResourceNotFoundException("Email not found: $email")

/**
 * Generic resource not found exception with custom message
 */
class EntityNotFoundException(resourceName: String, id: Any) : ResourceNotFoundException(
    "$resourceName with ID $id not found"
)

