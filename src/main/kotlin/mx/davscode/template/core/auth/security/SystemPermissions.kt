package mx.evolutiondev.template.core.auth.security

/**
 * Centralized system permission constants for RBAC.
 * These must match the permission names in the database (seeded by PermissionSeeder).
 */
object SystemPermissions {
    const val USERS = "users"
    const val RBAC = "rbac"
    const val ADMIN = "admin"
    const val AUDIT = "audit"

    /** All permissions for MASTER role seeding */
    val ALL_PERMISSIONS = listOf(USERS, RBAC, ADMIN, AUDIT)

    /** Permissions for ADMIN role */
    val ADMIN_ROLE_PERMISSIONS = listOf(USERS)

    /** Permissions for USER role (none by default) */
    val USER_ROLE_PERMISSIONS = emptyList<String>()
}
