package mx.evolutiondev.template.core.auth.service

import mx.evolutiondev.template.core.auth.model.PermissionEntity
import mx.evolutiondev.template.core.auth.model.RoleEntity
import mx.evolutiondev.template.core.auth.repository.PermissionRepository
import mx.evolutiondev.template.core.auth.repository.RoleRepository
import mx.evolutiondev.template.core.auth.security.SystemPermissions
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Order(1)
class PermissionSeeder(
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository,
    private val cachedUserService: CachedUserService
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(PermissionSeeder::class.java)

    @Transactional
    override fun run(args: ApplicationArguments?) {
        syncPermissions()
        syncRoles()
    }

    private fun syncPermissions() {
        val existingNames = permissionRepository.findAll().map { it.name }.toSet()
        val toCreate = SystemPermissions.ALL_PERMISSIONS.filter { it !in existingNames }

        if (toCreate.isEmpty()) {
            log.debug("All permissions already exist")
            return
        }

        toCreate.forEach { name ->
            try {
                permissionRepository.save(PermissionEntity(name = name))
                log.info("Created permission: $name")
            } catch (e: DataIntegrityViolationException) {
                // Permission already exists (race condition during concurrent startup)
                log.debug("Permission already exists: $name")
            }
        }
    }

    private fun syncRoles() {
        val existingRoles = roleRepository.findAll().associateBy { it.name }
        val roleNames = listOf("USER", "ADMIN", "MASTER")

        roleNames.forEach { roleName ->
            val role = existingRoles[roleName] ?: roleRepository.save(RoleEntity(name = roleName))
            val permissionsToAssign = when (roleName) {
                "MASTER" -> SystemPermissions.ALL_PERMISSIONS
                "ADMIN" -> SystemPermissions.ADMIN_ROLE_PERMISSIONS
                else -> SystemPermissions.USER_ROLE_PERMISSIONS
            }

            val permissionEntities = permissionRepository.findByNameIn(permissionsToAssign)
            val currentPermissionNames = role.permissions.map { it.name }.toSet()
            val toAdd = permissionEntities.filter { it.name !in currentPermissionNames }

            toAdd.forEach { perm ->
                role.permissions.add(perm)
                log.info("Assigned permission ${perm.name} to role $roleName")
            }
            if (toAdd.isNotEmpty()) {
                roleRepository.save(role)
                // Evict cache after role modifications to ensure fresh permission checks
                cachedUserService.evictAll()
            }
        }
    }
}
