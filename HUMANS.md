# Instructions for humans

## Development

**Non-trivial edits to documentation files should be done with an AI coding assistant** (Cursor, Claude or GitHub Copilot), not by large free-hand rewrites.

Why:

- Keeps structure, tone, cross-links, IDs, and checklists consistent with existing docs
- Forces updates across related files
- Gives agents a stable pattern to follow later during implementation

### What you may edit by hand

- Typos, wording of a single sentence, checkbox ticks when you personally finished something
- Adding a short link or date if you are sure nothing else must change

### What you should not edit by hand (use AI)

- New sections, new task blocks, new open-question IDs
- Defining or rewriting programming guidelines, architecture, MVP scope, roadmap
- Restructures, renames, or “cleanup” across multiple files
- Meeting follow-ups that touch more than one document

If unsure: **use AI**.

## Prompts

### Documentation consistency checker

```
Check documentation consistency.
```

### Knowledge base maintainer

```
Update knowledge graph with all changes in this branch.
```

### Domain entities sanity checker

```
Check domain entities
```

```
In the collections skill and species of entity Vet is now a new element constraint. Add tests to VetTest.java.
```

### Domain enum creator

```
Create an enum InvoiceStatus with values D for drafted, I for issued, C for completed, and X for cancelled, in package clinic.
```

### Domain entity creator

```
Create an entity Invoice with required property issued of type LocalDate, with required property period of type Duration, with required property status of type InvoiceStatus, with required non-blank property text of type String, in package clinic.
```

### Domain entity property adder

```
At the end of treatment in a visit, the vet writes down a diagnosis. The visit is now billable. Add property billable of type boolean to entity Visit.
```

```
Add property retired of type boolean to entity Vet. Retired vets are no longer active and cannot not be assigned to visits.
```

```
Remove property billable of type boolean from entity Visit.
```

### Domain entity collection adder

```
A vet has one or more skills that he uses for treatments. Add collection skill of type String to entity Vet hast a set of skills.
```

### Domain entity many-to-one relation adder

```
A pet is treated in a visit. Add a required many-to-one relation from entity Visit to entity Pet.
```

```
A visit is conducted by a vet. Add a required many-to-one relation from entity Visit to entity Vet.
```

### Domain entity one-to-many relation adder

```
An owner can have one or more pets. Add an one-to-many relation from entity Owner to entity Pet.
```

### Domain entity one-to-one relation adder

```
Every visit results in exactly one invoice for billing. Add a required one-to-one relation from entity Invoice to entity Visit.
```

### REST API sanity checker

```
Check REST API documentation
```

### REST API service documentation creator

```
Update REST API documentation
```

```
Create REST API documentation for owner endpoints
```

### GraphQL API sanity checker

```
Check GraphQL API
```

### GraphQL API service documentation creator

```
Update GraphQL API documentation
```

```
Create GraphQL API documentation for owner operations
```

## Explore the REST API

POST /api/owner

```json
{
  "name": "Tom Bombadil",
  "address": "Bergweg 1, 5400 Hallein",
  "contact": "+43 6452 4456"
}
```

POST /api/pet

```json
{
  "owner": "/api/owner/<owner-id>",
  "name": "Max",
  "born": "2021-04-22",
  "species": "Cat",
  "sex": "M"
}
```

## Explore the GraphQL API

Query all aggregate roots.

```
query all {
  allOwner { id name }
  allPet { id name }
  allVet { id name }
  allVisit { id date }
}
```

Query for all pets with their species and owner.

```
query allPet {
  allPet {
    id
    name
    species
    sex
    owner { id name }
  }
}
```

Query an owner by name and navigate to pets.

```
query ownerWithPets {
  ownerByName(name: "Tom Bombadil") {
    id
    name
    address
    contact
    allPet { id name species }
  }
}
```
