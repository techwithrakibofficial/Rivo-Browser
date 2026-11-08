# UI/UX and Accessibility Audit

**Task:** RIVO-GPT-02  
**Repository:** https://github.com/techwithrakibofficial/Rivo-Browser  
**Audited branch:** `main`  
**Audited HEAD:** `da6bf4aed9b53a29cff6c9226c238d1da7006acb`  
**Audit date:** 2026-11-08  
**Method:** Read-only source and documentation inspection; no emulator/device or screenshot validation.

## Executive summary

The Phase 1 UI has a working native browser shell, an omnibox, a five-action bottom toolbar, modal panels for tabs/bookmarks/history/downloads/appearance, and Dark/Light/Premium Glass color palettes. The design foundations are already present; they were not recreated.

The most important defect is **theme scope leakage**: the selected `MaterialTheme` wraps the main browser surface but not the dialogs and panels rendered later in `BrowserScreen`. Consequently, appearance selection is not consistently applied to the appearance panel, tab/bookmark/history/download panels, download confirmation, site-permission prompt and notice dialog. This is a source-level finding, not a screenshot observation.

No UI source was changed because `BrowserScreen.kt` and `MainActivity.kt` are outside this worker's file ownership. The fix is handed to the UI implementation owner.

## Severity-ranked findings

| ID | Severity | Area | Finding | Recommendation / owner |
|---|---|---|---|---|
| UI-01 | P1 | Three-theme consistency | `MaterialTheme` ends around the main `Surface`; the subsequent panel and `AlertDialog` overlays are outside the selected theme scope. | UI owner: wrap the entire screen including all overlays in the selected Rivo theme, or theme each overlay explicitly. Add one test/preview per mode. |
| UI-02 | P1 | Responsive navigation | Five text actions (Back, Forward, Reload/Stop, Bookmark, Tabs) share one fixed bottom `Row`. At narrow widths or large font scales, labels can crowd or overflow. No adaptive layout is present. | UI owner: test 320 dp width and large font scale; use adaptive/scrollable secondary actions while keeping core navigation reachable. |
| UI-03 | P2 | Contrast | Light secondary `#567D9F` on white is approximately 4.35:1, just below WCAG AA's 4.5:1 normal-text threshold. | Design-system owner: darken secondary (e.g. `#4D7192`, ~5.13:1) before using it for normal text; otherwise use a compliant semantic role. |
| UI-04 | P2 | Omnibox semantics | Omnibox has a placeholder but no persistent `label` in the inspected Compose source. The hint may not remain available as a clear field name after text entry. | UI owner: add a persistent localized label or explicit semantics and verify TalkBack while empty and populated. |
| UI-05 | P2 | Tab close action | Close action is a text button whose visible/accessible text is only `×`; this is not a clear localized action name. | UI owner: provide a localized “Close tab” semantic label and keep a visible affordance. |
| UI-06 | P2 | Appearance picker | Current selected mode is indicated by a textual checkmark prefix; no explicit selected/radio semantics are authored. | UI owner: use a selectable/radio row or selected semantics; verify TalkBack announces the selected mode. |
| UI-07 | P2 | Dialog sizing | Tab, bookmark, history and download lists use fixed `300.dp` heights. This can consume too much space on short displays and does not document an adaptive max-height strategy. | UI owner: constrain to available window height and retain internal scrolling at large font scales. |
| UI-08 | P2 | Verification | No Compose UI tests, screenshot tests, accessibility scanner results, TalkBack session or device contrast captures were available in this audit. | QA owner: run the checklist below on physical/emulated small and standard displays. Do not claim compliance until evidence exists. |
| UI-09 | P3 | Glass specification drift | `docs/design/APPEARANCE_MODES.md` previously described selective blur, while source and `DESIGN_SYSTEM.md` intentionally avoid real-time blur. | Design-system owner: align documentation with the implemented low-RAM translucent-surface approach. This documentation update resolves the mismatch. |
| UI-10 | P3 | Home / Tab Universe scope | Initial tab loads DuckDuckGo; there is no native Rivo home dashboard. Tabs are a simple list dialog, not a visual Tab Universe. | Product/UI owner: treat these as future design scope; do not claim the advanced screens exist in Phase 1. |

## Screen and feature inventory

| Area | Source-level status | Notes |
|---|---|---|
| Browser home | Basic web landing only | `HOME_URL` points to DuckDuckGo. No native dashboard or quick-link home screen found. |
| Omnibox | Implemented | `OutlinedTextField`, Go button, URI keyboard action. Persistent label and TalkBack review remain. |
| Tab interface | Basic list implemented | “Your tabs” dialog lists title/private prefix and close action. No thumbnail grid, grouping or visual Tab Universe. |
| Quick Appearance Picker | Implemented | Appearance item is in secondary overflow menu; selection is a text checkmark. |
| Dark / Light / Glass | Palettes implemented | Main browser surface uses the selected scheme. Overlay scope defect prevents consistent application to dialogs/panels. |
| Private browsing | Basic status UI implemented | Private banner and tab-list prefix exist. No separate private-mode dashboard was found. |
| Bookmarks | Panel implemented | Empty state exists; row navigation and remove actions are present. |
| History | Panel implemented | Empty state and clear action exist. |
| Downloads | Panel implemented | Empty state, status, open/share actions and private-download explanation exist. |
| Loading | Basic state implemented | Linear page progress and “Starting Rivo Browser…” state. |
| Errors / permissions | Basic dialogs implemented | Download, permission and notice dialogs exist; they share the overlay theme-scope issue. |
| Settings | Not found as a dedicated screen | Appearance is the only settings-like panel in the inspected Phase 1 UI. |
| Privacy Intelligence Center | Not implemented | Phase 2 per project feature documentation. |
| Transfer Studio | Not implemented | Phase 2 per project feature documentation. |

## Three-theme and contrast review

Contrast ratios below are calculated from the literal palette hex values in `RivoColorSchemes.kt`; they are **source-level estimates**, not rendered screenshot measurements. Glass composition depends on the actual background beneath its translucent surface.

| Mode | Pair | Approx. contrast | WCAG AA normal text |
|---|---|---:|---|
| Light | onSurface `#172333` / surface `#FFFFFF` | 15.85:1 | Pass |
| Light | primary `#315B85` / surface `#FFFFFF` | 7.08:1 | Pass |
| Light | secondary `#567D9F` / surface `#FFFFFF` | 4.35:1 | **Fail** |
| Light | error `#BA1A1A` / surface `#FFFFFF` | 6.46:1 | Pass |
| Dark | onSurface `#E8EEF7` / surface `#171E28` | 14.37:1 | Pass |
| Dark | primary `#9CCBFF` / surface `#171E28` | 9.90:1 | Pass |
| Dark | secondary `#B8CDE2` / surface `#171E28` | 10.27:1 | Pass |
| Dark | error `#FFB4AB` / surface `#171E28` | 9.87:1 | Pass |
| Glass | onSurface `#F3F7FF` / background `#101827` | 16.55:1 | Pass |
| Glass | primary `#B7D5FF` / background `#101827` | 11.82:1 | Pass |
| Glass | secondary `#C4D8F0` / background `#101827` | 12.21:1 | Pass |

For the Glass surface token `#CC26354B` composited at roughly 80% opacity over `#101827`, the estimated result is `#222F44`; foreground `#F3F7FF` is approximately 12.55:1 against that result. This estimate does not replace checking all possible layered surfaces and states.

## Accessibility review

- **Contrast:** one palette token fails normal-text AA if used as text (Light secondary). Main source does not appear to use this secondary token for a visible label in the inspected screen, but the token should be corrected before broader component reuse.
- **Names and roles:** text-labelled controls are generally discoverable; the `×` close-tab action needs a meaningful localized semantic name. The omnibox should have a persistent label.
- **Selected state:** theme selection is represented visually with a checkmark prefix; expose a selected/radio state to assistive technology.
- **Touch targets:** Material 3 buttons are used, but this audit did not measure rendered bounds. Five toolbar actions in one row create a narrow-width and large-font risk. Verify every action is at least 48 × 48 dp.
- **Text scaling:** no custom typography scale or font-size clamp was found; Material 3 defaults are used. Large font-scale behavior was not tested.
- **Motion:** no decorative browser-chrome animation was found. Do not add motion without respecting Android reduced-motion/animation settings.
- **Focus:** keyboard and TalkBack focus order were not exercised. Verify omnibox → Go/menu → page → bottom actions → modal content, and ensure dialogs trap/restore focus correctly.
- **State communication:** private mode and connection security have explicit text labels, which is preferable to color-only signaling.

## Recommended verification matrix

1. Test Dark, Light and Glass with the browser page, tab panel, bookmarks, history, downloads, appearance picker, download confirmation, permission prompt and notice dialog all open in turn.
2. Capture screenshots at 320 dp width, a standard phone width, short screen height, and large system font scale.
3. Run TalkBack through every toolbar action, omnibox, tab close, appearance selection, list item and dialog button.
4. Measure touch bounds (minimum 48 × 48 dp) and check focus order/return focus after dialogs close.
5. Run Android Accessibility Scanner or equivalent and manually verify WCAG contrast for all actual text and interactive states.
6. Repeat the visual pass with Glass transparency fallback enabled; confirm no blur or continuous animation is required.
7. Record device model, Android version, font scale, screenshots and tool output. Until this is done, status remains “source review only”, not accessibility-certified.

## Scope and ownership

This audit did not modify `BrowserScreen.kt`, `MainActivity.kt`, `BrowserController.kt`, theme Kotlin sources, Gradle files, security/storage files or other UI-owner files. The P1/P2 source fixes are recommendations for the designated UI implementation owner. The documentation/specification changes are owned by RIVO-GPT-02.
