package mx.evolutiondev.template.core.auth.service

import com.github.benmanes.caffeine.cache.Caffeine
import mx.evolutiondev.template.core.auth.model.CustomerDetails
import mx.evolutiondev.template.core.auth.model.CustomerEntity
import mx.evolutiondev.template.core.auth.repository.CustomerRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CachedUserService(
    private val customerRepository: CustomerRepository
) {
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(60, TimeUnit.SECONDS)
        .maximumSize(500)
        .build<Long, CustomerDetails>()

    fun loadUserById(userId: Long): CustomerDetails? {
        return cache.get(userId) {
            val customer = customerRepository.findByIdWithRoleAndPermissions(userId) ?: return@get null
            toCustomerDetails(customer)
        }
    }

    fun evictUser(userId: Long) {
        cache.invalidate(userId)
    }

    fun evictAll() {
        cache.invalidateAll()
    }

    private fun toCustomerDetails(customer: CustomerEntity): CustomerDetails {
        val authorities = mutableListOf<SimpleGrantedAuthority>()
        authorities.add(SimpleGrantedAuthority("ROLE_${customer.roleEntity.name}"))
        customer.roleEntity.permissions.forEach { perm ->
            authorities.add(SimpleGrantedAuthority(perm.name))
        }
        return CustomerDetails(
            id = customer.id,
            email = customer.email,
            // Password intentionally set to empty string for security:
            // JWT authentication doesn't need password in UserDetails,
            // and we avoid exposing password hash in cached objects
            password = "",
            authorities = authorities,
            enabled = customer.isEnabled,
            banned = customer.isBanned
        )
    }
}
