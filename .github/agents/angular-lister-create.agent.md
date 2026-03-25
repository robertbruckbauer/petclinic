---
name: Angular lister component creator
description: |
  Creates a lister component for an entity.
  Creates an editor component for all properties of an entity.
  Creates a service component for entity data retrieval.
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
  Replace placeholder `<package>` with the given name.

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

1. **Create entity type**
  Create entity type script `<entity>.type.ts` for entity class `<Entity>.java`.

2. **Create entity service**
  Use `doc/concept/angular/entity-service.adoc` as the implementation baseline.
  Use `doc/service/<entity>-restapi.adoc` as the REST API specification.
  Create entity service script `<entity>.service.ts` for REST API access.

3. **Create entity editor component**
  Use `doc/concept/angular/entity-editor.adoc` as the implementation baseline.
  Create directory `src/main/angular/pages/<package>/<entity>-editor`.
  Create component script `<entity>-editor.ts`.
  Create component layout `<entity>-editor.html`.
  Add controls to edit all properties of the entity.
  Do not show or edit the primary key or the version of the entity.

4. **Create entity lister component**
  Use `doc/concept/angular/entity-lister.adoc` as the implementation baseline.
  Create directory `src/main/angular/pages/<package>/<entity>-lister`.
  Create component script `<entity>-lister.ts`.
  Create component layout `<entity>-lister.html`.
  Find the minimum set of properties needed to identify the entity, e.g. name.
  Use those properties for column names in the table.

5. **Add route for entity lister component**
  Use `doc/concept/angular/entity-lister.adoc` as the implementation baseline.
  Create route script `<entity>-routes.ts`.
  Add route script to `app.routes.ts`.
  Add a navigation link to `app.html`.

## Task output

Create a short summary with files changed.
