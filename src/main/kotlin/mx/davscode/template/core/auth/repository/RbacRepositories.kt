package mx.evolutiondev.template.core.auth.repository

import mx.evolutiondev.template.core.auth.model.PermissionEntity
import mx.evolutiondev.template.core.auth.model.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<PermissionEntity, Long> {
    fun findByName(name: String): PermissionEntity?
    fun findByNameIn(names: Collection<String>): List<PermissionEntity>
}

@Repository
interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findByName(name: String): RoleEntity?
    fun findByNameIn(names: Collection<String>): List<RoleEntity>
}
