# Project Guidelines

## Overview

This is a **Spring Boot + Kotlin** backend project with JWT authentication and Role-Based Access Control (RBAC).

## Tech Stack

- **Language**: Kotlin 1.9.x
- **Framework**: Spring Boot 3.2.x
- **Database**: PostgreSQL with Flyway migrations
- **Authentication**: JWT (stateless)
- **Security**: Spring Security 6.2.x with RBAC
- **Build**: Gradle (Kotlin DSL)

## Project Structure

```
src/main/kotlin/mx/dvscode/template/
├── core/
│   ├── auth/           # JWT, Security, Permissions
│   ├── config/         # Spring configuration
│   └── exception/      # Custom exceptions
├── modules/
│   ├── auth/           # Login, Register endpoints
│   ├── users/          # User management
│   └── rbac/           # Role & Permission management
└── TemplateApplication.kt
```

## Available Skills

Use these skills for detailed patterns:

| Skill | Description | URL |
|-------|-------------|-----|
| `kotlin` | Kotlin patterns and conventions | [SKILL.md](./skills/kotlin/SKILL.md) |
| `spring-boot` | Spring Boot best practices | [SKILL.md](./skills/spring-boot/SKILL.md) |
| `jwt-auth` | JWT + RBAC implementation | [SKILL.md](./skills/jwt-auth/SKILL.md) |
| `flyway-migrations` | Database migrations | [SKILL.md](./skills/flyway-migrations/SKILL.md) |
| `kotlin-testing` | Testing with JUnit/MockK | [SKILL.md](./skills/kotlin-testing/SKILL.md) |
| `sdd` | Spec-Driven Development | [SKILL.md](./skills/sdd/SKILL.md) |

## Auto-Invoke Rules

When performing these actions, the corresponding skill is automatically loaded:

| Action | Skill |
|--------|-------|
| Working with Kotlin files | `kotlin` |
| Creating controllers/services | `spring-boot` |
| Authentication endpoints | `jwt-auth` |
| Database schema changes | `flyway-migrations` |
| Writing tests | `kotlin-testing` |
| Planning features | `sdd` |

## Development Commands

```bash
# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Build
./gradlew build

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Database

- PostgreSQL is required
- Migrations are in `src/main/resources/db/migration/`
- Use Flyway for all schema changes (no auto-DDL)

## API Conventions

All API responses follow this format:

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": 1700000000000
}
```

## Authentication

- JWT tokens are sent in `Authorization: Bearer <token>` header
- Tokens expire in 24 hours (configurable)
- Roles: `USER`, `ADMIN`, `MASTER`
- Permissions: `USERS`, `RBAC`, `ADMIN`, `AUDIT`

## RBAC System

| Endpoint | Required Permission |
|----------|-------------------|
| `GET /api/v1/users` | `USERS` |
| `POST /api/v1/users` | `USERS` |
| `PUT /api/v1/users/{id}` | `USERS` |
| `DELETE /api/v1/users/{id}` | `USERS` |
| `POST /api/v1/rbac/users/{id}/role` | `RBAC` |
| `GET /api/v1/rbac/users/{id}/role` | `RBAC` |

## Contributing

1. Create a feature branch
2. Follow Kotlin coding conventions
3. Add tests for new functionality
4. Use Flyway migrations for database changes
5. Run `./gradlew check` before committing

## Questions?

- Check the skills in `/skills/` for detailed patterns
- Use `/sdd-` commands for planning substantial changes
- Refer to `/docs/` for additional documentation
