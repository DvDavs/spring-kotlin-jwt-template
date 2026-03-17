# RBAC (Role-Based Access Control)

El Template incluye un sistema RBAC con roles y permisos dinámicos.

## Permisos del sistema

| Permiso | Descripción |
|---------|-------------|
| `users` | Gestionar usuarios (crear, leer, actualizar, eliminar) |
| `rbac` | Gestionar roles y permisos |
| `admin` | Acceso administrativo completo |
| `audit` | Ver logs de auditoría e información del sistema |

## Roles por defecto

| Rol | Permisos |
|-----|----------|
| USER | Ninguno (acceso autenticado básico) |
| ADMIN | users |
| MASTER | users, rbac, admin, audit |

## Uso en controladores

Protege endpoints con `@PreAuthorize` usando permisos:

```kotlin
@GetMapping("/users")
@PreAuthorize("hasAuthority('users')")
fun listUsers(): ResponseEntity<List<User>> { ... }

@DeleteMapping("/users/{id}")
@PreAuthorize("hasAuthority('users')")
fun deleteUser(@PathVariable id: Long): ResponseEntity<Unit> { ... }

@GetMapping("/rbac/roles")
@PreAuthorize("hasAuthority('rbac')")
fun listRoles(): ResponseEntity<List<Role>> { ... }
```

## Verificar permisos en código

```kotlin
val userDetails = SecurityContextHolder.getContext().authentication.principal as CustomerDetails
if (userDetails.hasPermission("users")) {
    // Usuario tiene permiso
}
```

## Respuesta de autenticación

El login y refresh token incluyen los permisos del usuario:

```json
{
  "user": {
    "id": 1,
    "name": "David",
    "lastName": "García",
    "email": "david@davscode.com",
    "role": "MASTER",
    "permissions": ["users", "rbac", "admin", "audit"],
    "isBanned": false
  }
}
```

## Cache de usuarios

`CachedUserService` cachea los usuarios con sus roles y permisos durante 60 segundos para reducir consultas a la base de datos. Para invalidar el cache de un usuario (ej. tras cambiar su rol):

```kotlin
cachedUserService.evictUser(userId)
```
