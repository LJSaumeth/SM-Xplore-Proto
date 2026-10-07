# SM-Xplore Mockups Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert the 16 existing wireframes into high-fidelity, responsive desktop mockups in Figma pages 06, 07, and 08 without changing their information architecture or use-case traceability.

**Architecture:** Use the existing pages 02, 03, and 04 as source references. Create parallel mockup pages with the same screen inventory, using shared visual tokens and Auto Layout behavior. Keep tourist-facing screens warmer and more visual, and keep management screens restrained and data-focused.

**Tech Stack:** Figma Design, Figma Plugin API through `figma_use_figma`, Figma bridge exports, web desktop frames at 1440x900, Auto Layout, constraints, and reference imagery.

**Spec:** `docs/superpowers/specs/2026-10-06-sm-xplore-mockups-design.md`

## Global Constraints

- Convert the 16 existing wireframes from pages 02, 03, and 04 into high-fidelity desktop mockups.
- Create pages 06, 07, and 08 in Figma, one per feature.
- Preserve the wireframe information architecture, content, states, actors, use-case labels, and actions.
- Use 1440x900 as the desktop reference size, while making every mockup responsive and auto-adjustable.
- Do not create prototype connections or mobile variants.
- Build layouts with Figma Auto Layout, constraints, fill-container/hug-content behavior, and min/max limits where appropriate.
- Avoid hardcoded coordinates for normal content; use absolute positioning only for intentional overlays, badges, and floating elements.
- Use Manrope for the interface and DM Serif Display sparingly for public tourist headings.
- Use reference images as mockup visuals, not as final licensed production assets.
- Do not commit or push until the user explicitly requests it.

---

### Task 1: Establish Mockup Pages and Shared Visual System

**Files:**
- Modify: Figma file `3FYENRmL7fcaDvGpeT5TEb`
- Create: Figma page `06 · Mockups — Gestionar Actividades`
- Create: Figma page `07 · Mockups — Gestionar Eventos`
- Create: Figma page `08 · Mockups — Gestionar Reseñas`
- Reference: Figma pages `02 · Gestionar Actividades`, `03 · Gestionar Eventos`, `04 · Gestionar Reseñas`

**Interfaces:**
- Produces shared colors, typography, spacing, radii, button, input, card, status, navigation, and table patterns for Tasks 2–4.

- [ ] Create the three pages after page 05 without modifying pages 00–05.
- [ ] Define the shared visual tokens: navy `#12304A`, teal `#0E8C8A`, seafoam `#DDF3EC`, sand `#F4EBDD`, coral `#F47C5A`, and ink `#12212B`.
- [ ] Use Manrope for UI text and DM Serif Display only for public tourist headings.
- [ ] Establish reusable Auto Layout patterns for top navigation, management sidebar, filter rows, cards, forms, status pills, action groups, tables, and annotations.
- [ ] Set responsive behavior for shared patterns: fixed gutters, fill-container content, hug-content controls, wrapping filter groups, and right-aligned action groups with safe margins.
- [ ] Verify the new page names and order using the Figma page list before creating screens.

### Task 2: Build the Activities Mockup Page

**Files:**
- Modify: Figma page `06 · Mockups — Gestionar Actividades`
- Reference: Figma page `02 · Gestionar Actividades`
- Reference screens: `9:2`, `12:2`, `14:2`, `17:2`, `16:2`

**Interfaces:**
- Produces five 1440x900 mockup frames with the same names, UC labels, actors, content, states, and image-slot count as page 02.

- [ ] Copy the five source wireframe structures into page 06 without changing frame content or screen order.
- [ ] Style the tourist screens `UC2·UC7·UC8 · Explorar actividades` and `UC9 · Detalle de actividad` with sand/seafoam surfaces, teal primary actions, navy navigation, and restrained coral alerts.
- [ ] Style the guide screens `UC1 · Registrar actividad`, `UC3 · Actualizar actividad`, and `UC4·UC5·UC6 · Disponibilidad y cupos` with a restrained management surface and consistent form/table controls.
- [ ] Replace image placeholders with researched visual references while preserving every original slot and crop intent.
- [ ] Apply Auto Layout and constraints to navigation, cards, filters, detail columns, forms, availability controls, and action bars.
- [ ] Resize at least one tourist and one guide screen horizontally and verify no overlap, clipped control, or broken alignment.

### Task 3: Build the Events Mockup Page

**Files:**
- Modify: Figma page `07 · Mockups — Gestionar Eventos`
- Reference: Figma page `03 · Gestionar Eventos`
- Reference screens: `19:2`, `18:2`, `20:2`, `21:2`, `22:2`, `23:2`

**Interfaces:**
- Produces six 1440x900 mockup frames with the same names, UC labels, actors, content, states, and image-slot count as page 03.

- [ ] Copy the six source wireframe structures into page 07 without changing frame content or screen order.
- [ ] Style tourist discovery, search, detail, and planned-event screens with the Caribbean palette and clear event imagery.
- [ ] Style gestora registration and event-management screens with consistent sidebar, filters, table headers, statuses, and action groups.
- [ ] Preserve `Afluencia`, `Aforo`, publication status, empty states, and event detail actions exactly as represented in the wireframes.
- [ ] Replace event image placeholders with researched visual references while preserving the original image slots.
- [ ] Apply Auto Layout and constraints to event cards, search/filter controls, tables, forms, status pills, and action groups.
- [ ] Resize the event table and one public event screen to verify that columns, headers, dates, statuses, and actions do not overlap.

### Task 4: Build the Reviews Mockup Page

**Files:**
- Modify: Figma page `08 · Mockups — Gestionar Reseñas`
- Reference: Figma page `04 · Gestionar Reseñas`
- Reference screens: `24:2`, `25:2`, `26:2`, `27:2`, `28:2`

**Interfaces:**
- Produces five 1440x900 mockup frames with the same names, UC labels, actors, content, states, and action relationships as page 04.

- [ ] Copy the two tourist screens and three prestadora screens into page 08 without changing the source structure.
- [ ] Style public review reading and writing screens with warm surfaces, clear rating hierarchy, readable review cards, and Caribbean imagery.
- [ ] Style the prestadora panel, response screen, and report screen with the restrained management system established in Task 1.
- [ ] Preserve the review actions, rating controls, response states, report form, metrics, and action labels exactly.
- [ ] Apply Auto Layout to review cards, rating summaries, forms, table rows, action groups, and the report modal/overlay.
- [ ] Verify that the prestadora table has centered headers, consistent row alignment, safe right margins, and no overlap between review text, dates, statuses, and actions.
- [ ] Resize the review panel and writing form to verify that text, buttons, tables, and image slots reflow without clipping.

### Task 5: Research and Place Image References

**Files:**
- Reference-only assets: external visual references grouped by screen and image slot
- Modify: Figma image fills inside pages 06–08

**Interfaces:**
- Produces a reference mapping from each image slot to a selected visual reference, without treating those references as final production assets.

- [ ] Dispatch one research subagent to collect visual references by subject: Rodadero, Playa Cristal, Tayrona, Centro Histórico, event venues, tourism activities, and review avatars/objects where needed.
- [ ] Keep each reference mapped to its source screen and slot; do not add new slots or invent new content.
- [ ] Prefer visually clear, context-appropriate references and preserve the wireframe crop intent.
- [ ] Place the selected references into the mockup image fills without changing layout dimensions.
- [ ] Record any unavailable reference as a deliberate neutral visual placeholder rather than delaying the page build.

### Task 6: Visual Verification and Local Documentation

**Files:**
- Modify: Figma pages 06–08
- Create or update locally: `docs/wireframes/` captures and README only after final approval

**Interfaces:**
- Produces visually verified mockups and updated local captures; remote repository changes require a separate explicit user instruction.

- [ ] Verify that each mockup page contains exactly the same screen count as its source page: 5 activities, 6 events, and 5 reviews.
- [ ] Compare every mockup against its source wireframe for structure, text, actions, actor, UC label, state, and image-slot count.
- [ ] Inspect representative public and management screens at the 1440px reference width and at reduced widths.
- [ ] Fix any alignment, spacing, clipping, contrast, or image-crop issue found during inspection.
- [ ] Export final mockup captures locally using actor-prefixed filenames matching `docs/wireframes/README.md`.
- [ ] Review the final diff and status; do not commit or push until explicitly authorized.
