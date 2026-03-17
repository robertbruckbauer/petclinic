---
name: Angular editor component updater
description: |
  Updates the editor component for all properties of an entity.
  Updates the service component for entity data retrieval.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that NO entity class with this name already exists.
  Replace placeholder `<Entity>` with the given name.
  Replace placeholder `<entity>` with lowercase name.

2. **Identify target domain package**
  Extract the domain package name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that a package directory with this name exists.
  Replace placeholder `<package>` with the given name

3. **Locate implementation files**
  Locate existing implementation files:
  - **Entity classes** → Check *.java in lib/backend-api/src/main/java/**/api/**
  - **Angular types** → Check *.type.ts in app/client-angular/src/main/angular/types
  - **Angular services** → Check *.service.ts in app/client-angular/src/main/angular/types
  - **Angular routes** → Check *.routes.ts in app/client-angular/src/main/angular/pages/**
  - **Angular editor components** → Check *.[ts|html|css] in app/client-angular/src/main/angular/pages/**/*-editor
  - **Angular lister components** → Check *.[ts|html|css] in app/client-angular/src/main/angular/pages/**/*-lister
  
4. **Locate documentation files**
  Locate existing implementation files that affect documentation:
  - **REST API service documentation** → Check *restapi.adoc in doc/service/**
  - **GraphQL service documentation** → Check *graphql.adoc in doc/service/**
  
## Task steps

1. **Update entity type**
  Synchronize entity type script `<entity>.type.ts` with entity class `<Entity>.java`.

3. **Update entity editor component**
  Use `doc/concept/angular/entity-editor.adoc` as the implementation baseline.
  Create directory `src/main/angular/pages/<package>/<entity>-editor`.
  Create component script `<entity>-editor.ts`.
  Create component layout `<entity>-editor.html`.
  Add controls to edit new properties of the entity.
  Do not show or edit the primary key or the version of the entity.

4. **Update entity lister component**
  Use `doc/concept/angular/entity-lister.adoc` as the implementation baseline.
  If needed, load extra data and pass it to the editor component.

## Task output

Create a short summary with files changed.
