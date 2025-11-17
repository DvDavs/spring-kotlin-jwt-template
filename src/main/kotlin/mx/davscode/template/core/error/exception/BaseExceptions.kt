package mx.evolutiondev.template.core.error.exception

/**
 * Base exception for resource not found errors (404)
 */
open class ResourceNotFoundException(message: String) : RuntimeException(message)

/**
 * Base exception for bad request errors (400)
 */
open class BadRequestException(message: String) : RuntimeException(message)

/**
 * Base exception for forbidden/access denied errors (403)
 */
open class ForbiddenException(message: String) : RuntimeException(message)

/**
 * Base exception for unauthorized errors (401)
 */
open class UnauthorizedException(message: String) : RuntimeException(message)

/**
 * Base exception for conflict errors (409)
 */
open class ConflictException(message: String) : RuntimeException(message)

