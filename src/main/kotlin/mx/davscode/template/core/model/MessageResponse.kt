package mx.evolutiondev.template.core.model

/**
 * Simple message response wrapper
 * 
 * @param message The message to return
 * @param data Optional additional data
 */
data class MessageResponse(
    val message: String,
    val data: Any? = null
)

/**
 * Success response with data
 * 
 * @param T The type of data being returned
 * @param data The data to return
 * @param message Optional success message
 */
data class SuccessResponse<T>(
    val data: T,
    val message: String? = null
)

