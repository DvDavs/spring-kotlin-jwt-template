package mx.evolutiondev.template.modules.rbac.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import mx.evolutiondev.template.core.auth.model.PermissionEntity
import mx.evolutiondev.template.core.auth.model.RoleEntity
import mx.evolutiondev.template.core.auth.repository.CustomerRepository
import mx.evolutiondev.template.core.auth.repository.PermissionRepository
import mx.evolutiondev.template.core.auth.repository.RoleRepository
import mx.evolutiondev.template.core.auth.service.CachedUserService
import mx.evolutiondev.template.core.error.exception.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rbac")
@SecurityRequirement(name = "bearerAuth")
class RbacController(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val customerRepository: CustomerRepository,
    private val cachedUserService: CachedUserService
) {

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('rbac')")
    @Operation(summary = "Listar todos los roles", description = "Requiere permiso 'rbac'")
    fun listRoles(): ResponseEntity<List<RoleResponse>> {
        val roles = roleRepository.findAll()
        return ResponseEntity.ok(roles.map { it.toResponse() })
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('rbac')")
    @Operation(summary = "Listar todos los permisos", description = "Requiere permiso 'rbac'")
    fun listPermissions(): ResponseEntity<List<PermissionResponse>> {
        val permissions = permissionRepository.findAll()
        return ResponseEntity.ok(permissions.map { it.toResponse() })
    }

    @PostMapping("/users/{userId}/role")
    @PreAuthorize("hasAuthority('users')")
    @Operation(summary = "Asignar rol a un usuario", description = "Requiere permiso 'users'. Solo ADMIN/MASTER puede ejecutar esta acción.")
    fun assignRole(
        @PathVariable userId: Long,
        @RequestBody request: AssignRoleRequest
    ): ResponseEntity<UserRoleResponse> {
        val customer = customerRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado con ID: $userId") }

        val role = roleRepository.findByName(request.roleName)
            ?: throw ResourceNotFoundException("Rol no encontrado: ${request.roleName}")

        customer.roleEntity = role
        customerRepository.save(customer)

        // Evict cache to ensure fresh permissions on next request
        cachedUserService.evictUser(userId)

        return ResponseEntity.ok(
            UserRoleResponse(
                userId = customer.id,
                email = customer.email,
                roleName = role.name,
                message = "Rol asignado exitosamente"
            )
        )
    }

    @GetMapping("/users/{userId}/role")
    @PreAuthorize("hasAuthority('users')")
    @Operation(summary = "Obtener rol de un usuario", description = "Requiere permiso 'users'")
    fun getUserRole(@PathVariable userId: Long): ResponseEntity<UserRoleResponse> {
        val customer = customerRepository.findByIdWithRoleAndPermissions(userId)
            ?: throw ResourceNotFoundException("Usuario no encontrado con ID: $userId")

        return ResponseEntity.ok(
            UserRoleResponse(
                userId = customer.id,
                email = customer.email,
                roleName = customer.roleEntity.name,
                permissions = customer.roleEntity.permissions.map { it.name },
                message = null
            )
        )
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('users')")
    @Operation(summary = "Listar usuarios por rol", description = "Opcionalmente filtrar por nombre de rol. Requiere permiso 'users'")
    fun listUsersByRole(@RequestParam(required = false) role: String?): ResponseEntity<List<UserInfoResponse>> {
        val customers = if (role != null) {
            customerRepository.findAllByRoleWithAnyStatus(role, PageableUtil.unpaged()).content
        } else {
            customerRepository.findAll().map { customerRepository.findByIdWithRoleAndPermissions(it.id)!! }
        }

        return ResponseEntity.ok(customers.map {
            UserInfoResponse(
                id = it.id,
                name = it.name,
                lastName = it.lastName,
                email = it.email,
                roleName = it.roleEntity.name,
                isEnabled = it.isEnabled,
                isBanned = it.isBanned
            )
        })
    }

    private fun RoleEntity.toResponse() = RoleResponse(
        id = id,
        name = name,
        description = description,
        permissionNames = permissions.map { it.name }
    )

    private fun PermissionEntity.toResponse() = PermissionResponse(
        id = id,
        name = name,
        description = description
    )
}

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val permissionNames: List<String>
)

data class PermissionResponse(
    val id: Long,
    val name: String,
    val description: String?
)

data class AssignRoleRequest(
    val roleName: String
)

data class UserRoleResponse(
    val userId: Long,
    val email: String,
    val roleName: String,
    val permissions: List<String>? = null,
    val message: String?
)

data class UserInfoResponse(
    val id: Long,
    val name: String,
    val lastName: String,
    val email: String,
    val roleName: String,
    val isEnabled: Boolean,
    val isBanned: Boolean
)

object PageableUtil {
    fun unpaged(): org.springframework.data.domain.Pageable {
        return org.springframework.data.domain.Pageable.unpaged()
    }
}
