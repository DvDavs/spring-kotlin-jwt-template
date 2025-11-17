package mx.evolutiondev.template.core.auth.service

import mx.evolutiondev.template.core.auth.model.Role
import mx.evolutiondev.template.core.auth.repository.CustomerRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class HierarchyValidationService(
    private val customerRepository: CustomerRepository
) {

    fun validateUserCreationHierarchy(creatorRole: Role, targetRole: Role): Boolean {
        return when (creatorRole) {
            Role.MASTER -> targetRole in listOf(Role.MASTER, Role.ADMIN, Role.USER)
            Role.ADMIN -> targetRole == Role.USER
            Role.USER -> false
        }
    }

    fun validateUserAccess(requesterId: Long, targetUserId: Long): Boolean {
        if (requesterId == targetUserId) {
            return true
        }

        val requester = customerRepository.findByIdEvenIfDisabled(requesterId) ?: return false
        val targetUser = customerRepository.findByIdEvenIfDisabled(targetUserId) ?: return false

        if (!requester.isEnabled) {
            return false
        }

        return when (requester.role) {
            Role.MASTER -> true
            Role.ADMIN -> targetUser.createdBy == requesterId
            Role.USER -> false
        }
    }

    fun getUsersCreatedBy(adminId: Long): List<Long> {
        val admin = customerRepository.findByIdEvenIfDisabled(adminId) ?: return emptyList()
        
        if (admin.role !in listOf(Role.ADMIN, Role.MASTER)) {
            return emptyList()
        }

        val createdUsers = customerRepository.findAllByCreatedByWithAnyStatus(adminId, Pageable.unpaged())
        return createdUsers.content.map { it.id }
    }
}

