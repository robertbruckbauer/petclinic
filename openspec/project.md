# OpenSpec project context

## Directory structure and workflow

```
openspec/
├── project.md       # this file
├── AGENTS.md        # agent instructions for working in this directory
└── specs/           # current, merged truth — one capability per folder
    └── <capability>/spec.md
```

## Requirement format

Every requirement in a `specs/<capability>/spec.md` uses:

```
### Requirement: <short statement of what must be true>

<one or two sentences of context, if needed>

#### Scenario: <concrete situation>
- **GIVEN** <precondition>
- **WHEN** <action>
- **THEN** <observable outcome>
```
