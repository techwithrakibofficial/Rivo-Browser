# Rivo Browser — Design System

Updated: 2026-11-10

## Appearance modes
- Dark, Light and Premium Glass are supported through one centralized semantic color/shape/spacing system.
- All browser panels and dialogs inherit the active MaterialTheme.
- Quick Appearance Picker remains a secondary overflow/menu control; no permanent theme icon is placed on the main browser display.

## Premium Glass constraints
- Use translucent semantic surfaces, restrained borders and layered elevation.
- Do not use expensive real-time blur or GPU-heavy effects on low-/mid-range devices.
- Glass must remain readable when transparency is reduced or disabled.
- Preserve sufficient text/icon contrast in all three modes.

## Interaction and accessibility
- Prefer Material 3 components and semantic labels/state descriptions.
- Maintain clear focus order, large touch targets and TalkBack names for tab close, filters and controls.
- Respect system font scaling and reduced-motion preferences.
- Validate 320 dp widths and large fonts; horizontal filter rows should remain operable.

## Feature surfaces
- Reader Mode, Transfer Studio, Privacy Intelligence Center, permission prompts and dialogs use semantic theme tokens.
- QR results require an explicit confirmation surface.
- Developer Mode remains read-only and visually distinguished from normal browsing controls.

## Acceptance checklist
- [ ] Dark, Light and Glass screenshots on supported Android versions.
- [ ] Contrast and icon legibility in all modes.
- [ ] TalkBack and keyboard/focus order.
- [ ] 320 dp width and large font scaling.
- [ ] Reduced motion and transparency-disabled behavior.
- [ ] 3–4 GB device performance without sustained blur/render overhead.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`. PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC.
- Active Main ruleset `Protect main branch` requires PR review, at least one approval, CODEOWNERS review, resolved threads and strict check `build`; branch deletion/non-fast-forward updates are blocked and no bypass actors exist.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue in source and adds JVM regression/property tests. GeckoView runtime mixed-content behavior remains unverified on physical devices/fixtures.
- On this Main SHA, OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still in progress at the last captured check; do not claim success before rechecking final conclusions.
- Remaining release gates: production-signed APK/AAB and signer/checksum verification; physical-device QA, accessibility and 3–4 GB RAM profiling; runtime 16 KB verification; full MASVS/MASTG assessment and independent penetration testing where required. CI does not replace these gates; no certification is claimed.


## 2026-11-24 — Verification boundary
- No design-system source changes were made in PR #85.
- Existing centralized Dark, Light and Premium Glass themes and secondary Quick Appearance Picker remain integrated per the feature matrix.
- Contrast, TalkBack, font scaling, reduced-motion behavior and low-end rendering still require physical-device/accessibility acceptance; CI does not close these gates.

## 2026-11-25 — Native start-page composition
- New-tab content uses a lightweight Compose start page with semantic Material surfaces, shared Rivo spacing/radii and no real-time blur.
- The page prioritizes a clear brand header, quick-access actions, bookmarks and recently visited pages; existing top omnibox remains the single search/navigation entry point.
- In private tabs, recently visited history is not rendered. User-saved bookmarks remain available.
- Validation still required: all three appearance modes, 320 dp width, large fonts, TalkBack, reduced motion/transparency preferences and 3–4 GB device rendering.
