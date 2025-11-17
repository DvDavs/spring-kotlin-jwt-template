package mx.evolutiondev.template.core.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomerDetails(
        val id: Long,
        private val email: String,
        private val password: String,
        private val authorities: Collection<GrantedAuthority>
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = email

    override fun getUsername(): String = password
}

