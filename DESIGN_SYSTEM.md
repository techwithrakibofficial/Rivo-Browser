- Tab preview snapshots are downscaled, replaced snapshots are recycled, and previews are recycled on tab close/controller disposal to bound retained memory.

- Settings also includes functional JavaScript and Global Privacy Control switches. JavaScript is enabled by default; GPC is opt-in for normal browsing and GeckoView enables it in private mode by engine policy.

## 2026-11-28 — Flagship visual direction and tab/settings surfaces

- Dark and Premium Glass palettes now use a deep violet/blue foundation with luminous lavender and blue accents; Light remains a readable light palette.
- Home branding uses an animated gradient emblem and subtle breathing glow above the centered Google search field.
- Tab Universe is a four-column overview with downscaled live GeckoView snapshots for normal tabs. Private tabs intentionally use a text-only tile to avoid retaining private page imagery in the overview.
- Settings exposes only currently implemented controls: appearance, GeckoView text zoom, Google default search, existing privacy center, clear-data flow and Transfer Studio.
- Text zoom uses GeckoView's global font-size factor and reloads the active page to apply the setting. The setting is persisted locally.
- The tab grid preview is an in-memory snapshot, not a continuously live miniature browser; previews update when a normal tab is switched away from.
- Full flagship motion language, background effects, and complete Settings breadth still need visual iteration and validation; do not describe the entire product as finished.

## 2026-11-28 — Chrome-inspired start page and compact browser chrome

- Reworked the native start page around a centered Google search identity, compact pill-shaped search field, shortcut row, bookmarks and recent history.
- Removed the persistent RIVO brand/header from the top browser chrome. The upper trailing action is Reload/Stop; the bottom navigation slot previously used by Reload is now New tab (+).
- Compactened the omnibox to a 46 dp capsule and reduced adjacent control footprint while retaining accessible descriptions and keyboard Go behavior.
- Premium Glass uses a silver/ice translucent palette, subtle outlines and lightweight opacity motion; no real-time blur is introduced.
- Home motion checks Android's system animator setting and falls back to a static presentation when animations are disabled. This is intentionally restrained for low-/mid-range hardware.
- Fixed the native-home marker being overwritten by Gecko's initial `about:blank` location callback. New tabs should now remain on Rivo's native home rather than expose an empty Gecko document.
- Search fallback now targets Google Search; explicit valid HTTP(S) URLs remain direct navigation.
- UI screenshots, TalkBack, narrow widths, font scaling, reduced-animation behavior and 3–4 GB hardware rendering still require acceptance testing.

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

## 2026-11-26 — Native start page theme integration

- PR #100's native Compose start page is integrated on Main and uses semantic surfaces for Dark, Light and Premium Glass.
- This is source integration, not completed design acceptance. Still required: screenshots and contrast review for all three modes, icon legibility, TalkBack, 320 dp width, large fonts, reduced motion/transparency-disabled behavior, and low-RAM rendering on target hardware.
- Do not add a permanent theme-switch icon to the main browser display; keep Quick Appearance Picker as a secondary control.


## 2026-11-28 — Minimal home and translucent surfaces

- Home is intentionally reduced to one centered Google search/address field. Browser actions remain accessible from the three-dot menu and compact bottom controls.
- Reload is a menu action immediately above New tab; the separate top reload button is removed.
- The home page hides the top omnibox; normal website pages retain it.
- Browser chrome and native panels use translucent semantic surfaces rather than real-time blur. The corrected bottom bar is 64 dp with a higher-opacity surface to preserve control legibility over arbitrary page content.
- Preserve foreground contrast and visible control boundaries across Dark, Light and Premium Glass. Do not apply browser-chrome transparency to website-rendered content. Validate against busy/light/dark page backgrounds and large text/TalkBack.


## 2026-11-28 — Premium Glass and bottom navigation correction

- PR #108 increases semantic Glass surface opacity and outline contrast to improve browser-chrome legibility; the design intentionally balances translucency against readable controls.
- Bottom navigation height is 64 dp so icon/label pairs fit within the safe-drawing content area. Confirm actual visibility, gesture/navigation-bar insets, TalkBack and font scaling on target hardware.
- Exact matching to the previously supplied motion-video reference is not verified because that reference was not available in this work turn.


## 2026-11-28 — PR #110 candidate (not yet merged)
- Reworked the native start page toward a Chrome-style Google home: Google search field, local bookmark/history shortcuts, History and Incognito actions, plus Bookmarks and Tabs quick actions. No external news feed or fake account profile is presented.
- Browser top chrome now exposes Home/New tab, Guest profile/settings, open-tab count and overflow controls.
- Settings panel surface is opaque in Premium Glass to prevent website pixels bleeding through text; explicit semantic foreground colors are used.
- GeckoView's documented Android Autofill bridge is enabled on created GeckoView instances. This allows a separately configured Android Autofill provider to participate; it is not a Rivo password manager, and it does not implement credential storage or sync.
- Rivo account sign-in/cloud sync, a built-in password vault, and an extension marketplace remain unavailable and are not represented as shipped features.
- Candidate app version: 0.2.9-phase1 / versionCode 10. PR #110 requires fresh CI/review/merge; physical-device visual and autofill acceptance remain pending.


## Authoritative project checkpoint — 2026-11-28 (PR #110 merged)

- Canonical Main commit: `e10220c0c09711d5b5c5575bef106687797f9bae`; PR #110 is merged. The earlier “PR #110 candidate / not yet merged” entries above are historical and superseded by this checkpoint.
- App metadata on Main: `0.2.9-phase1`, versionCode 10.
- Android CI run [#46171158253](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158253) completed successfully. Verified steps include shipping debug assemble, Android lint, app unit tests, Gecko engine policy tests, GeckoView/WebView benchmark builds, minified shipping release bundle, static native-library/APK 16 KB alignment checks, QA artifact uploads and Android security instrumentation tests. Gradle dependency-verification bootstrap [#46171158244](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158244) and OSV scan [#46171158469](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158469) also completed successfully.
- CI outputs are QA/debug artifacts and an unsigned QA AAB; they are not production-signed release files. The immutable tag `v0.2.9-phase1` is not present, and no signed v0.2.9 GitHub Release is verified. Do not distribute unsigned CI artifacts as the production APK.
- PR #110 delivered the Chrome-inspired native home, local History/Bookmarks shortcuts, toolbar Home/profile/tab/overflow controls, opaque Settings surface with explicit foreground colors, and GeckoView Android Autofill integration. Autofill depends on the Android provider configured by the user; this is not a built-in password vault or account sync.
- Physical TECNO Pova Slim 5G / Android 16 / HyperOS 16.3.0 acceptance remains unverified in this audit. Also outstanding: TalkBack/large-font/narrow-width/reduced-motion checks, 3–4 GB memory profiling, runtime 16 KB validation on compatible hardware, GeckoView runtime security fixtures, and complete control-by-control MASVS/MASTG review. No independent penetration test or MASVS certification is claimed.
- Keep historical checkpoints above for traceability; this dated checkpoint is authoritative for the current Main revision only.

PR #110 visual note: Settings Glass surface uses an opaque surface and explicit semantic foreground colors to prevent webpage pixels bleeding through text. Physical contrast/accessibility validation across Dark, Light and Premium Glass remains pending.

## 2026-11-29 — Browser toolbar layout candidate
- Compact primary row for Back, Forward, Reload/Stop, omnibox, tab count and overflow; horizontally scrollable secondary actions avoid clipping on narrow screens.
- Controls use semantic Material theme colors and translucent surfaces; no real-time blur is introduced.
- Verify Dark, Light and Premium Glass contrast, 320 dp width, TalkBack focus order and large-font behavior on-device.
