---
category: building
related:
  - gradle-module-conventions.md
status: current
updated: 2026-11-22
---

# PlantUML diagrams need the AsciidoctorJ diagram module, or they silently don't render

An `arc42` chapter can embed a diagram as a `[plantuml]` block (e.g. a C4 diagram via `!include <C4/C4_Context.puml>` — PlantUML bundles the C4-PlantUML stdlib itself, so this works offline with no vendoring and no network `!include`). Rendering it requires the `pages` task's Asciidoctor to have the diagram extension enabled — `asciidoctorj { modules { diagram.use() } }` in the root `build.gradle`.

Without that module, `gradle pages` still builds successfully: a `[plantuml]` block silently falls back to a plain source-code listing in the generated HTML (the raw PlantUML/DOT source shown as text) instead of an image, with no warning or error naming the missing module.

Root `build.gradle` now enables the module (added in commit `a9ef79e`, "pages mit Diagrammen") — a prior version of this note, written the same day, still described the module as missing; that's now corrected. The assembled doc is `pages/html/server/arc42/index.html` (from `arc42/index.adoc`); PlantUML blocks from included chapters such as `03-context-and-scope/context.adoc` render as SVG beside that output. Every `[plantuml]` block in `arc42` is expected to render correctly when `pages` runs with the diagram module.
