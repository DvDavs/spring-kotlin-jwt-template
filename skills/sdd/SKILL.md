# Spec-Driven Development (SDD) Skill

## Description

SDD workflow for planning, implementing, and verifying substantial features using structured specifications.

## What is SDD?

SDD (Spec-Driven Development) is a workflow that ensures:
1. **Clear requirements** before coding
2. **Design decisions** are documented
3. **Implementation is verifiable** against specs
4. **Knowledge is preserved** for future developers

## SDD Workflow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Explore    │───▶│  Proposal   │───▶│   Spec      │───▶│   Design    │
│  (investigate)│    │ (scope/approach)│ │ (requirements)│ │ (architecture)│
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                              │
       ┌───────────────────────────────────────────────────────┘
       ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Tasks     │───▶│   Apply     │───▶│   Verify    │───▶│  Archive    │
│ (checklist) │    │ (implement)│    │ (validate)  │    │ (finalize)  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## SDD Commands

### `/sdd-init`

Initialize SDD context in a project:
- Detects tech stack
- Sets up persistence backend (Engram or OpenSpec)
- Creates project configuration

### `/sdd-explore <topic>`

Explore and investigate before committing:
- Research existing solutions
- Analyze requirements
- Identify risks and unknowns

**Returns**: Exploration report with findings

### `/sdd-new <change>`

Create a new change:
- Runs exploration first (if not done)
- Creates proposal document
- Starts the SDD workflow

### `/sdd-propose`

Define scope and approach:
- Intent and motivation
- Scope (in/out of scope)
- Approach (how to implement)
- Risks and alternatives

### `/sdd-spec`

Write detailed requirements:
- Functional requirements
- User stories
- Acceptance criteria
- Edge cases

### `/sdd-design`

Create technical design:
- Architecture decisions
- Data models
- API contracts
- Integration points

### `/sdd-tasks`

Break into actionable tasks:
- Implementation checklist
- Dependencies between tasks
- Priority and estimation

### `/sdd-apply`

Implement tasks:
- Write actual code
- Follow specs and design
- Keep tasks up to date

### `/sdd-verify`

Validate implementation:
- Check against specs
- Verify all tasks completed
- Identify gaps

### `/sdd-archive`

Finalize and document:
- Merge delta specs to main
- Archive change documentation
- Update project knowledge

## Project Structure with SDD

When SDD is initialized, the project gets:

```
.agents/                    # Agent configuration
├── rules/                  # Orchestrator rules
│   └── sdd-orchestrator.md
└── skills/                 # SDD-specific skills
    ├── sdd-init/
    ├── sdd-explore/
    ├── sdd-propose/
    ├── sdd-spec/
    ├── sdd-design/
    ├── sdd-tasks/
    ├── sdd-apply/
    ├── sdd-verify/
    └── sdd-archive/
```

## When to Use SDD

Use SDD for:
- ✅ New features
- ✅ Architectural changes
- ✅ API redesigns
- ✅ Complex bug fixes
- ✅ Refactoring that affects multiple modules

Don't use SDD for:
- ❌ Quick fixes
- ❌ Documentation updates
- ❌ Simple one-file changes
- ❌ Dependency updates

## Artifact Storage

SDD artifacts can be stored in:

| Backend | Description |
|---------|-------------|
| **Engram** | Default, persistent memory across sessions |
| **OpenSpec** | File-based artifacts (YAML/JSON) |
| **Hybrid** | Both backends |

The backend is configured during `sdd-init`.

## Example: Creating a New Feature

```bash
# 1. Start exploring
/sdd-explore user-profile-api

# 2. Create proposal
/sdd-new user-profile-api

# 3. Write specification
/sdd-spec

# 4. Create design
/sdd-design

# 5. Break into tasks
/sdd-tasks

# 6. Implement
/sdd-apply

# 7. Verify
/sdd-verify

# 8. Archive
/sdd-archive
```

## SDD Artifacts

Each phase produces an artifact:

| Phase | Artifact |
|-------|----------|
| Explore | Exploration report |
| Proposal | Change proposal |
| Spec | Requirements document |
| Design | Technical design |
| Tasks | Implementation checklist |
| Apply | Progress tracking |
| Verify | Verification report |
| Archive | Final documentation |

## Auto-Invoke Rules

This skill is automatically loaded when:
- Running any `/sdd-*` command
- Planning substantial features
- Implementing complex changes

## Related Skills

- `kotlin` - Kotlin language patterns
- `spring-boot` - Spring Boot development
- `kotlin-testing` - Testing patterns
