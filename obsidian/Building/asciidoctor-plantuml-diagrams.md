---
category: Building
related:
  - gradle-module-conventions.md
status: current
updated: 2026-09-10
---

# PlantUML diagrams need the AsciidoctorJ diagram module, or they silently don't render

A `doc/arc42` chapter can embed a diagram as a `[plantuml]` block (e.g. a C4 diagram via `!include <C4/C4_Context.puml>` — PlantUML bundles the C4-PlantUML stdlib itself, so this works offline with no vendoring and no network `!include`). Rendering it requires the `pages` task's Asciidoctor to have the diagram extension enabled — `asciidoctorj { modules { diagram.use() } }` in the root `build.gradle`.

Without that module, `gradle pages` still builds successfully: a `[plantuml]` block silently falls back to a plain source-code listing in the generated HTML (the raw PlantUML/DOT source shown as text) instead of an image, with no warning or error naming the missing module. As of this note, root `build.gradle` does not enable the module, so any `[plantuml]` block in `doc/arc42` — including `03-context-and-scope.adoc`'s technical-context diagram — renders as literal source text, not a diagram.
