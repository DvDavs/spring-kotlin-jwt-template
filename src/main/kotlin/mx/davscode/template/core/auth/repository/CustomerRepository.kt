package mx.evolutiondev.template.core.auth.repository

import mx.evolutiondev.template.core.auth.model.CustomerEntity
import mx.evolutiondev.template.core.auth.model.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor<CustomerEntity> {
    fun findByEmail(email: String): CustomerEntity?
    fun findByResetToken(resetToken: String): CustomerEntity?
    fun findByRoleIn(roles: List<Role>): List<CustomerEntity>
    fun findAllByCreatedBy(createdBy: Long): List<CustomerEntity>
    fun findAllByCreatedBy(createdBy: Long, pageable: Pageable): Page<CustomerEntity>
    override fun findAll(pageable: Pageable): Page<CustomerEntity>
    
    @Query("SELECT * FROM customer WHERE id = :id", nativeQuery = true)
    fun findByIdEvenIfDisabled(@Param("id") id: Long): CustomerEntity?

    
    @Query("SELECT * FROM customer", nativeQuery = true)
    fun findAllWithAnyStatus(pageable: Pageable): Page<CustomerEntity>
    
    @Query("SELECT * FROM customer WHERE created_by = :adminId", nativeQuery = true)
    fun findAllByCreatedByWithAnyStatus(@Param("adminId") adminId: Long, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE is_enabled = :status",
        countQuery = "SELECT count(*) FROM customer WHERE is_enabled = :status",
        nativeQuery = true
    )
    fun findAllByIsEnabledWithAnyStatus(@Param("status") status: Boolean, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE created_by = :adminId AND is_enabled = :status",
        countQuery = "SELECT count(*) FROM customer WHERE created_by = :adminId AND is_enabled = :status",
        nativeQuery = true
    )
    fun findAllByCreatedByAndIsEnabledWithAnyStatus(@Param("adminId") adminId: Long, @Param("status") status: Boolean, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE role = CAST(:role AS text)",
        countQuery = "SELECT count(*) FROM customer WHERE role = CAST(:role AS text)",
        nativeQuery = true
    )
    fun findAllByRoleWithAnyStatus(@Param("role") role: String, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE role = CAST(:role AS text) AND is_enabled = :status",
        countQuery = "SELECT count(*) FROM customer WHERE role = CAST(:role AS text) AND is_enabled = :status",
        nativeQuery = true
    )
    fun findAllByRoleAndIsEnabledWithAnyStatus(@Param("role") role: String, @Param("status") status: Boolean, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE created_by = :adminId AND role = CAST(:role AS text)",
        countQuery = "SELECT count(*) FROM customer WHERE created_by = :adminId AND role = CAST(:role AS text)",
        nativeQuery = true
    )
    fun findAllByCreatedByAndRoleWithAnyStatus(@Param("adminId") adminId: Long, @Param("role") role: String, pageable: Pageable): Page<CustomerEntity>

    @Query(
        value = "SELECT * FROM customer WHERE created_by = :adminId AND role = CAST(:role AS text) AND is_enabled = :status",
        countQuery = "SELECT count(*) FROM customer WHERE created_by = :adminId AND role = CAST(:role AS text) AND is_enabled = :status",
        nativeQuery = true
    )
    fun findAllByCreatedByAndRoleAndIsEnabledWithAnyStatus(@Param("adminId") adminId: Long, @Param("role") role: String, @Param("status") status: Boolean, pageable: Pageable): Page<CustomerEntity>

    @Modifying
    @Query(value = "DELETE FROM customer WHERE id = :id", nativeQuery = true)
    fun hardDeleteById(@Param("id") id: Long)
}

