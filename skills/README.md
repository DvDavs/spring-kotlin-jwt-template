# Skills Index

This directory contains AI skills for developing with this project. Skills provide specialized instructions and workflows for different aspects of the codebase.

## Available Skills

### Language & Framework

| Skill | Description |
|-------|-------------|
| [kotlin](./kotlin/SKILL.md) | Kotlin language patterns, data classes, null safety |
| [spring-boot](./spring-boot/SKILL.md) | Spring Boot best practices, configuration, REST APIs |
| [jwt-auth](./jwt-auth/SKILL.md) | JWT authentication, RBAC, security |
| [flyway-migrations](./flyway-migrations/SKILL.md) | Database migrations, schema design |
| [kotlin-testing](./kotlin-testing/SKILL.md) | JUnit, MockK, AssertJ, integration tests |

### Workflow

| Skill | Description |
|-------|-------------|
| [sdd](./sdd/SKILL.md) | Spec-Driven Development workflow |

## Setup

Run the following command to configure skills for your AI assistant:

```bash
gentle-ai install --skill kotlin,spring-boot,jwt-auth,flyway-migrations,kotlin-testing,sdd
```

Or use the interactive TUI:

```bash
gentle-ai
```

## Auto-Invoke Skills

Skills are automatically loaded based on context. For example:
- Opening a `.kt` file → loads `kotlin` skill
- Creating a controller → loads `spring-boot` skill
- Working with JWT → loads `jwt-auth` skill
- Running `/sdd-*` commands → loads `sdd` skill

## Manual Skill Loading

If you need to explicitly load a skill, use the skill tool:

```
SKILL: Load `skills/kotlin/SKILL.md` before starting.
```

## Adding New Skills

To add a new skill:
1. Create a new directory under `skills/`
2. Add a `SKILL.md` file with the skill content
3. Update this README with the new skill
4. Reference the skill in `AGENTS.md`

## Related Files

- [AGENTS.md](../AGENTS.md) - Project-level AI guidelines
- [.agents/](../.agents/) - Agent configuration and rules
