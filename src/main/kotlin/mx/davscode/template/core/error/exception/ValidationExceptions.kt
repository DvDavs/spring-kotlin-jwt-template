package mx.evolutiondev.template.core.error.exception

/**
 * Thrown when an email already exists in the system
 */
class DuplicateEmailException(message: String = "Email already exists") : ConflictException(message)

/**
 * Thrown when a resource already exists (generic)
 */
class DuplicateResourceException(message: String) : ConflictException(message)

/**
 * Thrown when request validation fails
 */
class ValidationException(message: String) : BadRequestException(message)

/**
 * Thrown when a required field is missing
 */
class MissingFieldException(field: String) : BadRequestException("Required field is missing: $field")

/**
 * Thrown when a field has an invalid format
 */
class InvalidFormatException(field: String, reason: String = "Invalid format") : BadRequestException("$field: $reason")

