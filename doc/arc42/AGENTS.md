# Agent instructions for `doc/arc42/`

## Role of this directory

`doc/arc42` is the bird's-eye architecture view — for stakeholders, and for an architect or developer new to the project. Chapter 1 (`01-introduction-and-goals.adoc`) holds the domain story, quality goals, and stakeholder table. This file holds the process rules for maintaining the chapter set; the chapters themselves stay stakeholder-facing content, not agent instructions.

## Guiding rule for what belongs in a chapter

If removing a detail would not change a stakeholder's understanding of the architecture or an architect's fundamental implementation choices, that detail probably belongs in the Obsidian knowledge graph (`obsidian/`) instead — not in an arc42 chapter.

## Truth model and precedence

This repository has five artifacts that can describe the same current-state fact. When two of them disagree, resolve in this order:

1. Code and tests — the ground truth.
2. `doc/arc42` (this chapter set) — the bird's-eye view.
3. `doc/concept` — the implementation guides.
4. `openspec` — the normative contract. It may describe a target ahead of the current implementation only when the gap is tracked via an ADR (see `09-architecture-decisions.adoc`) and a row in `11-risks-and-technical-debt.adoc`'s risk table — that is a documented gap, not a contradiction.
5. `obsidian` — the explanatory knowledge graph. Every note there is a hypothesis about the code, never an independent fact. A note that contradicts the code is corrected to follow the code; if it can't be verified, it is marked `status: stale` with a one-line reason instead of guessing, and a note marked `stale`/`superseded` is a historical record, not current guidance.

`obsidian/` is written only by AI agents through its maintenance skills — never by direct human edit.

## Hard rule: keep chapters stakeholder-facing, don't duplicate this file

A chapter that needs the guiding rule or the truth model links to this file (e.g. "see `doc/arc42/AGENTS.md`") instead of restating it. Two chapters carrying the same rule in their own words is exactly the kind of untraceable duplication chapter 1's traceability goal rules out.
