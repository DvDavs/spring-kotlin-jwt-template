package mx.evolutiondev.template.core.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mx.evolutiondev.template.core.error.model.ErrorMessage
import mx.evolutiondev.template.core.util.formatter.DateFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class TemplateAccessDeniedHandler(
        private val dateFormatter: DateFormatter
) : AccessDeniedHandler {

    private val logger: Logger = LoggerFactory.getLogger(TemplateAccessDeniedHandler::class.java)

    override fun handle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            accessDeniedException: AccessDeniedException
    ) {
        val requestPath = request.requestURI
        logger.info("Forbidden path: {}", requestPath)
        logger.error("Access denied: {}", accessDeniedException.message.orEmpty())

        response.contentType = "application/json"
        response.status = HttpStatus.FORBIDDEN.value()

        val message = accessDeniedException.message ?: "No tienes permisos para acceder a esta URL"

        val errorMessages = ErrorMessage(
                timestamp = dateFormatter.parseCurrentDate("dd/MM/yyyy HH:mm:ss"),
                statusCode = 403,
                errors = mutableMapOf(
                        "url" to message
                )
        )

        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, errorMessages)
    }
}

