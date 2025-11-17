package mx.evolutiondev.template.core.error.model

/**
 * Standard error response for API endpoints
 * 
 * @param timestamp ISO 8601 formatted timestamp when the error occurred
 * @param status HTTP status code (e.g., 400, 404, 500)
 * @param error HTTP status text (e.g., "Bad Request", "Not Found")
 * @param message Human-readable error message
 * @param path The API endpoint path where the error occurred
 * @param errors Map of field-specific errors (for validation errors)
 * @param details Additional error details or context
 */
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val errors: Map<String, Any>? = null,
    val details: Any? = null
)

/**
 * Detailed validation error for a specific field
 * 
 * @param field The field name that failed validation
 * @param rejectedValue The value that was rejected
 * @param message The validation error message
 */
data class FieldError(
    val field: String,
    val rejectedValue: Any?,
    val message: String
)

/**
 * Response for validation errors with detailed field-level errors
 */
data class ValidationErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val fieldErrors: List<FieldError>
)

/**
 * Legacy ErrorMessage for backwards compatibility
 * @deprecated Use ErrorResponse instead
 */
@Deprecated("Use ErrorResponse instead", ReplaceWith("ErrorResponse"))
data class ErrorMessage(
    val timestamp: String,
    val statusCode: Int,
    val errors: Map<String, String>
)

