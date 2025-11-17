package mx.evolutiondev.template.core.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mx.evolutiondev.template.core.auth.jwt.JwtUtil
import mx.evolutiondev.template.core.auth.ktx.isCustomerAvailable
import mx.evolutiondev.template.core.auth.model.CustomerDetails
import mx.evolutiondev.template.core.auth.repository.CustomerRepository
import mx.evolutiondev.template.core.error.model.ErrorMessage
import mx.evolutiondev.template.core.util.formatter.DateFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(
        private val customerRepository: CustomerRepository,
        private val jwtUtil: JwtUtil,
        private val dateFormatter: DateFormatter
) : OncePerRequestFilter() {

    private val appLogger: Logger = LoggerFactory.getLogger(JwtTokenFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        appLogger.info("authenticated urls: {}", request)
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            if (!jwtUtil.isValidToken(token)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "El token proporcionado no es válido")
                return
            }
            jwtUtil.getUserIdFromToken(token)?.let { customerId ->
                val longCustomerId = customerId.toLong()
                val customer = customerRepository.findByIdEvenIfDisabled(longCustomerId)
                if (customer == null) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "El token proporcionado no es válido")
                    return
                }
                if (!customer.isEnabled) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "El usuario asociado al token no existe")
                    return
                }
                if (customer.isBanned) {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "El usuario se encuentra temporalmente deshabilitado")
                    return
                }
                val authorities = listOf(SimpleGrantedAuthority("ROLE_${customer.role}"))
                val authentication = UsernamePasswordAuthenticationToken(
                        CustomerDetails(longCustomerId, customer.email, "", authorities),
                        null,
                        authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } ?: run {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "El token proporcionado no es válido")
                return
            }
        }
        chain.doFilter(request, response)
    }

    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        response.contentType = "application/json"
        response.status = status
        val errorMessages = ErrorMessage(
                timestamp = dateFormatter.parseCurrentDate("dd/MM/yyyy HH:mm:ss"),
                statusCode = status,
                errors = mutableMapOf(
                        "url" to message
                )
        )
        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, errorMessages)
    }
}

