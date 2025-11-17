package mx.evolutiondev.template.core.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mx.evolutiondev.template.core.error.model.ErrorMessage
import mx.evolutiondev.template.core.util.formatter.DateFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthEntryPointJwt(
        private val dateFormatter: DateFormatter
) : AuthenticationEntryPoint {

    private val logger: Logger = LoggerFactory.getLogger(AuthenticationEntryPoint::class.java)
    override fun commence(request: HttpServletRequest?, response: HttpServletResponse?, authException: AuthenticationException?) {
        val requestPath = request?.requestURI
        logger.info("Requested path: {}", requestPath)
        logger.error("Unauthorized error: {}", authException?.message.orEmpty())

        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.status = HttpServletResponse.SC_UNAUTHORIZED
        val message = authException?.message ?: "No es posible acceder a esta url"
        val errorMessages = ErrorMessage(
                timestamp = dateFormatter.parseCurrentDate("dd/MM/yyyy HH:mm:ss"),
                statusCode = 401,
                errors = mutableMapOf(
                        "url" to message
                )
        )
        val mapper = ObjectMapper()
        mapper.writeValue(response?.outputStream, errorMessages)
    }
}

