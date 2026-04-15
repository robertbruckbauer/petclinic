---
name: angular-editor-component-updater
description: Update Angular editor/lister components and entity typing after model changes; use for prompts like "Update editor and lister components for ...".
---


## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target entity

Extract the entity name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that NO entity class with this name already exists.
Replace placeholder `{Entity}` with the given name.
Replace placeholder `{entity}` with kebab case of the the given name.

### Identify target domain package

Extract the domain package name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that a package directory with this name exists.
Replace placeholder `{package}` with the given name

## Task steps

### Update entity type

Synchronize entity type script `{entity}.type.ts` with entity class `{Entity}.java`.

### Update entity editor component

Use `doc/concept/angular/entity-editor.adoc` as the implementation baseline.
Create directory `src/main/angular/pages/{package}/{entity}-editor`.
Create component script `{entity}-editor.ts`.
Create component layout `{entity}-editor.html`.
Add controls to edit new properties of the entity.
Do not show or edit the primary key or the version of the entity.

### Update entity lister component

Use `doc/concept/angular/entity-lister.adoc` as the implementation baseline.
If needed, load extra data and pass it to the editor component.
