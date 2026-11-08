# Development Plan

## Phase 0 — foundation
- Android foundation, modular engine API and WebView/GeckoView probe variants are committed.
- Official documentation research and benchmark protocol are present.
- Physical-device comparison and final engine decision remain pending; GeckoView is provisional, not a measured winner.

## Phase 1 — Functional Browser MVP (authorized 2026-11-08)
### Implemented in source and integrated
1. GeckoView-backed browser session adapter with process-scoped runtime and Activity-safe view lifecycle.
2. Navigation, URL/search omnibox, loading state and engine-reported security status.
3. Multi-tab lifecycle, active/inactive session handling and native private sessions.
4. Local bookmarks/history and encrypted metadata at rest.
5. Safe download streaming, app-specific storage, confirmation, 250 MB cap and scoped open/share.
6. Centralized Dark, Light and Premium Glass palettes with secondary Quick Appearance Picker.
7. Regular HTTP(S) tab persistence/restoration; private tabs excluded.
8. Security fail-closed metadata write handling and malformed-record rejection.
9. Selected-tab index restoration, omnibox edit preservation, history duplicate-URL key correction and IDN normalization.
10. Design-system contract and source-level UI/accessibility audit.

### Build baseline
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- AGP 9.5.0, Gradle 9.6.0, JDK 17.
- Compose BOM 2026.06.00; Activity Compose 1.13.0; Lifecycle ViewModel Compose 2.10.0.
- DataStore Preferences 1.3.0; coroutines Android 1.11.0.
- No backend, account, telemetry or paid service.

### Acceptance criteria
- Integrated Main CI passes assemble, lint, JVM tests and minified release AAB packaging.
- URL/search navigation, browser controls, tab lifecycle and duplicate-URL restoration work.
- Private sessions do not enter app-side history/bookmarks/restoration.
- Sensitive metadata is encrypted and corruption/key-loss never silently overwrites saved records.
- Downloads are confirmation-gated and safely stored/opened/shared.
- Dark/Light/Glass remain readable across all native surfaces and overlays.
- Regular tabs restore after process recreation; private tabs do not.
- Manual device smoke tests, performance and security evidence are recorded.

### Remaining Phase 1 work (ordered)
1. Verify integrated Main CI run #244 (or latest run on the current Main HEAD), including assembleDebug, lintDebug, testDebugUnitTest and minified bundleRelease.
2. Fix overlay theme scope so the selected MaterialTheme covers dialogs and panels; add Compose previews/tests for all three modes.
3. Adapt bottom toolbar and modal panel sizing for 320 dp width, short screens and large system font scales.
4. Fix Light secondary contrast; add persistent omnibox label, localized tab-close semantics and selected-state semantics for appearance.
5. Add Android instrumentation tests for Keystore AES-GCM round-trip, tampering, truncated payload, missing/lost key, legacy migration and DataStore corruption/relaunch preservation.
6. Verify duplicate URL tab restoration and private-tab exclusion after force-stop/relaunch.
7. Analyze minified release AAB/native library footprint; verify 16 KB native-library alignment where applicable. Do not remove required engine components solely to reduce size.
8. Install ARM64/ARMv7 builds on physical devices and benchmark representative 3–4 GB devices (startup, PSS, CPU, thermal behavior, stability).
9. Add per-site permission management and revoke controls; unsupported requests remain denied until safely mediated.
10. Complete accessibility verification, dependency vulnerability review and OWASP MASVS assessment.
11. Run controlled WebView/GeckoView comparison before making the final engine decision.

### Risks and mitigations
- GeckoView package/runtime footprint: optimize and compare actual release artifacts and WebView baseline.
- Inactive tabs: deactivate sessions correctly and retain MVP cap of eight until profiling supports a change.
- Private mode: native private sessions; do not persist private tab state or app-side history/bookmarks.
- Site permissions: deny by default; add explicit per-site controls before enabling additional capabilities.
- Downloads: HTTP(S), filename/path validation, 250 MB cap, app-specific storage and scoped FileProvider.
- Glass performance: translucent surfaces, no real-time blur, opaque fallback.
- Encrypted metadata: fail closed and validate stored values before replacement; instrument on Android.

## Phase 2 — Advanced browser
- Advanced Tab Universe.
- Privacy Intelligence Center.
- Transfer Studio.
- Reader mode and QR scanning where feasible.
- Extension workflows only where engine support is verified.
- Developer Mode using verified engine capabilities.

## Phase 3 — optimization and distribution
- Engine-specific verified features.
- Optional sync only after privacy/security design and cost review.
- Translation feasibility, accessibility polish, performance optimization and release/distribution readiness.
