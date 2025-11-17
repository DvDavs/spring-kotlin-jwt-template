package mx.evolutiondev.template.core.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import mx.evolutiondev.template.core.error.exception.*
import mx.evolutiondev.template.core.error.model.ErrorResponse
import mx.evolutiondev.template.core.error.model.FieldError
import mx.evolutiondev.template.core.error.model.ValidationErrorResponse
import mx.evolutiondev.template.core.util.formatter.DateFormatter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.Instant

/**
 * Global exception handler for all REST controllers
 * 
 * This class provides centralized exception handling across all @RequestMapping methods
 * through @ExceptionHandler methods. It converts exceptions into appropriate HTTP responses
 * with consistent error format.
 */
@RestControllerAdvice
class GeneralExceptionHandler(
    private val dateFormatter: DateFormatter
) {
    
    private val logger = LoggerFactory.getLogger(GeneralExceptionHandler::class.java)

    // ==================== Authentication & Authorization ====================

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(ex: UnauthorizedException, request: HttpServletRequest): ErrorResponse {
        logger.warn("Unauthorized access attempt: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message ?: "Unauthorized access",
            path = request.requestURI
        )
    }

    @ExceptionHandler(ForbiddenException::class, AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(ex: Exception, request: HttpServletRequest): ErrorResponse {
        logger.warn("Forbidden access attempt: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.FORBIDDEN.value(),
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = ex.message ?: "Access denied",
            path = request.requestURI
        )
    }

    // ==================== Resource Not Found ====================

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFound(ex: ResourceNotFoundException, request: HttpServletRequest): ErrorResponse {
        logger.debug("Resource not found: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Resource not found",
            path = request.requestURI
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFound(ex: NoHandlerFoundException, request: HttpServletRequest): ErrorResponse {
        logger.debug("No handler found for: {}", request.requestURI)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = "The endpoint you're trying to access does not exist",
            path = request.requestURI
        )
    }

    // ==================== Validation Errors ====================

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ValidationErrorResponse {
        logger.debug("Validation failed: {}", ex.message)
        
        val fieldErrors = ex.bindingResult.fieldErrors.map { error ->
            FieldError(
                field = error.field,
                rejectedValue = error.rejectedValue,
                message = error.defaultMessage ?: "Invalid value"
            )
        }

        return ValidationErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed for one or more fields",
            path = request.requestURI,
            fieldErrors = fieldErrors
        )
    }

    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ValidationErrorResponse {
        logger.debug("Binding failed: {}", ex.message)
        
        val fieldErrors = ex.fieldErrors.map { error ->
            FieldError(
                field = error.field,
                rejectedValue = error.rejectedValue,
                message = error.defaultMessage ?: "Invalid value"
            )
        }

        return ValidationErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Request binding failed",
            path = request.requestURI,
            fieldErrors = fieldErrors
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ValidationErrorResponse {
        logger.debug("Constraint violation: {}", ex.message)
        
        val fieldErrors = ex.constraintViolations.map { violation ->
            FieldError(
                field = violation.propertyPath.toString(),
                rejectedValue = violation.invalidValue,
                message = violation.message
            )
        }

        return ValidationErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Constraint violation on one or more fields",
            path = request.requestURI,
            fieldErrors = fieldErrors
        )
    }

    // ==================== Bad Request ====================

    @ExceptionHandler(BadRequestException::class, IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(ex: Exception, request: HttpServletRequest): ErrorResponse {
        logger.debug("Bad request: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Bad request",
            path = request.requestURI
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ErrorResponse {
        logger.debug("Message not readable: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Invalid request body format or missing required request body",
            path = request.requestURI
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ErrorResponse {
        logger.debug("Type mismatch: {}", ex.message)
        val message = "Parameter '${ex.name}' should be of type ${ex.requiredType?.simpleName}"
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = message,
            path = request.requestURI
        )
    }

    // ==================== Conflict ====================

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(ex: ConflictException, request: HttpServletRequest): ErrorResponse {
        logger.debug("Conflict: {}", ex.message)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = ex.message ?: "Conflict occurred",
            path = request.requestURI
        )
    }

    // ==================== Internal Server Error ====================

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ErrorResponse {
        logger.error("Unexpected error occurred: ", ex)
        return ErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred. Please contact support if the problem persists.",
            path = request.requestURI,
            details = if (isDevEnvironment()) ex.message else null
        )
    }

    /**
     * Check if we're in development environment
     * In production, you might want to hide detailed error messages
     */
    private fun isDevEnvironment(): Boolean {
        // You can implement this based on your environment configuration
        // For now, return false to not expose internal errors in production
        return System.getProperty("spring.profiles.active") == "dev"
    }
}

