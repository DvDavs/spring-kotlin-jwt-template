package mx.evolutiondev.template.core.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class JwtUtil(
        @Value("\${template.jwt.secret}") private val secretKey: String,
        @Value("\${template.jwt.issuer}") private val issuer: String,
        @Value("\${template.jwt.expiration}") private val expirationMillis: Long
) {
    private val signingKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(userId: Long, email: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMillis)

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("email", email)
                .claim("role", role)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact()
    }

    fun isValidToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return getClaimsFromToken(token)?.subject
    }

    fun getEmailFromToken(token: String): String? {
        return getClaimsFromToken(token)?.get("email", String::class.java)
    }

    fun getRoleFromToken(token: String): String? {
        return getClaimsFromToken(token)?.get("role", String::class.java)
    }

    fun generateSecureToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(128)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .body
        } catch (e: Exception) {
            null
        }
    }
}

