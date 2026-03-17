# Flyway Migrations Skill

## Description

Database migration patterns using Flyway, including schema design, migration naming, and best practices for this project.

## Migration Location

All migrations are stored in:
```
src/main/resources/db/migration/
```

## Naming Convention

Use the following format: `V<Version>__<Description>.sql`

- **Version**: Incremental number (V1, V2, V3...)
- **Description**: Short description using underscores for spaces
- **Separator**: Two underscores (`__`)

```sql
-- ✅ Correcto
V1__create_users_table.sql
V2__add_rbac_schema.sql
V3__add_user_indexes.sql

-- ❌ Incorrecto
v1_create_users.sql
V1.CreateUsersTable.sql
V1-create-users-table.sql
```

## Migration Examples

### Create Users Table

```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

### RBAC Schema

```sql
-- V2__rbac_schema.sql
-- Permisos del sistema
CREATE TABLE permission (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO permission (name) VALUES 
    ('USERS'),
    ('RBAC'),
    ('ADMIN'),
    ('AUDIT');

-- Roles de la aplicación
CREATE TABLE app_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO app_role (name) VALUES 
    ('USER'),
    ('ADMIN'),
    ('MASTER');

-- Relación muchos a muchos entre roles y permisos
CREATE TABLE role_permission (
    role_id BIGINT NOT NULL REFERENCES app_role(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Agregar columna role_id a users
ALTER TABLE users ADD COLUMN role_id BIGINT REFERENCES app_role(id);

-- Actualizar usuarios existentes con rol USER
INSERT INTO role_permission (role_id, permission_id)
SELECT ar.id, p.id
FROM app_role ar
CROSS JOIN permission p
WHERE ar.name = 'ADMIN' AND p.name IN ('USERS', 'RBAC');

INSERT INTO role_permission (role_id, permission_id)
SELECT ar.id, p.id
FROM app_role ar
CROSS JOIN permission p
WHERE ar.name = 'MASTER';

-- Actualizar usuarios con role = 'ADMIN' para asignar rol admin
UPDATE users SET role_id = (SELECT id FROM app_role WHERE name = 'ADMIN') 
WHERE role = 'ADMIN';

UPDATE users SET role_id = (SELECT id FROM app_role WHERE name = 'MASTER') 
WHERE role = 'MASTER';
```

## Best Practices

### 1. Always Use BigINT for IDs

```sql
-- ✅ Correcto
id BIGSERIAL PRIMARY KEY

-- ❌ Incorrecto
id SERIAL PRIMARY KEY
```

### 2. Add Indexes for Common Queries

```sql
-- Para búsquedas por email (login)
CREATE INDEX idx_users_email ON users(email);

-- Para filtros por rol
CREATE INDEX idx_users_role ON users(role);

-- Para búsquedas por fecha
CREATE INDEX idx_created_at ON users(created_at);
```

### 3. Use TIMESTAMP with Timezone

```sql
-- ✅ Correcto
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

-- ❌ Incorrecto (deprecated)
created_at TIMESTAMP NOT NULL
```

### 4. Always Define Constraints Explicitly

```sql
-- ✅ Correcto
ALTER TABLE users ADD CONSTRAINT fk_role 
    FOREIGN KEY (role_id) REFERENCES app_role(id) ON DELETE SET NULL;

-- ❌ Incorrecto
ALTER TABLE users ADD COLUMN role_id BIGINT REFERENCES app_role(id)
```

### 5. Never Modify Existing Migrations

If you need to change schema:
- Create a NEW migration with the fix
- Never edit existing migration files

## Rollback Strategy

Flyway supports undo migrations but they're not required. Best practice:

1. **Write idempotent migrations** - can be run multiple times safely
2. **Test migrations in development** before pushing
3. **Use `flyway:baseline`** for existing databases

```sql
-- Ejemplo de migración idempotente
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL
);
```

## Configuration

In `application.yml`:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
    out-of-order: false
```

## Auto-Invoke Rules

This skill is automatically loaded when:
- Creating new database migrations
- Modifying existing schema
- Working with Flyway configuration
- Setting up database indexes

## Related Skills

- `kotlin` - Kotlin language patterns
- `spring-boot` - Spring Boot configuration
- `jwt-auth` - Authentication with database
