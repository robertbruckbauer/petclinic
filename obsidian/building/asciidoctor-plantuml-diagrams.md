---
category: building
related:
  - gradle-module-conventions.md
status: current
updated: 2026-09-15
---

# PlantUML diagrams need the AsciidoctorJ diagram module, or they silently don't render

A `doc/arc42` chapter can embed a diagram as a `[plantuml]` block (e.g. a C4 diagram via `!include <C4/C4_Context.puml>` — PlantUML bundles the C4-PlantUML stdlib itself, so this works offline with no vendoring and no network `!include`). Rendering it requires the `pages` task's Asciidoctor to have the diagram extension enabled — `asciidoctorj { modules { diagram.use() } }` in the root `build.gradle`.

Without that module, `gradle pages` still builds successfully: a `[plantuml]` block silently falls back to a plain source-code listing in the generated HTML (the raw PlantUML/DOT source shown as text) instead of an image, with no warning or error naming the missing module.

Root `build.gradle` now enables the module (added in commit `a9ef79e`, "pages mit Diagrammen") — a prior version of this note, written the same day, still described the module as missing; that's now corrected. Verified against the actual generated output: `pages/html/server/arc42/03-context-and-scope.html` embeds `<img src="technical-context.svg">`, and `technical-context.svg` next to it is a real rendered C4 diagram (boxes, legend, connectors), not source text. Every `[plantuml]` block in `doc/arc42` renders correctly today.
