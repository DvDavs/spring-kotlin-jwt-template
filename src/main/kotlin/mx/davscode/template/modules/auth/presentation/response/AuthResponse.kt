package mx.evolutiondev.template.modules.auth.presentation.response

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserInfo
)

data class UserInfo(
    val id: Long,
    val name: String,
    val lastName: String,
    val email: String,
    val role: String
)

