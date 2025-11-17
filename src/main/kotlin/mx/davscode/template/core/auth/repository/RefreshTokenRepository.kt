package mx.evolutiondev.template.core.auth.repository

import mx.evolutiondev.template.core.auth.model.CustomerEntity
import mx.evolutiondev.template.core.auth.model.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByCustomer(customer: CustomerEntity): RefreshTokenEntity?
    fun findByToken(token: String): RefreshTokenEntity?
}

