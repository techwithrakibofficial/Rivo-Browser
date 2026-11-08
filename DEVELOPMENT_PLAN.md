# Development Plan

## Phase 0 — foundation
- Android foundation, modular engine API and GeckoView/WebView probe variants are committed.
- Official documentation research and an isolated same-host engine benchmark are added in the RIVO-07 branch.
- Physical-device comparison and final engine decision remain pending; GeckoView is provisional, not a measured winner.

## Phase 1 — Functional Browser MVP (authorized 2026-11-08)

### Implemented in source and integrated
1. GeckoView-backed browser session adapter with process-scoped runtime and Activity-safe view lifecycle.
2. Navigation, URL/search omnibox, loading state and engine-reported security status.
3. Multi-tab lifecycle, active/inactive session handling and native private sessions.
4. Local bookmarks/history and encrypted metadata at rest.
5. Safe download streaming, app-specific storage, confirmation, 250 MB cap and scoped open/share.
6. Dark, Light and Premium Glass palettes with Quick Appearance Picker.
7. Regular HTTP(S) tab persistence/restoration; private tabs excluded.
8. Fail-closed sensitive metadata handling and malformed-record rejection.
9. Selected-tab restoration, omnibox edit preservation, duplicate-history correction and IDN normalization.

### Build baseline
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Compile SDK API 37.2; targetSdk 36; minSdk 26.
- AGP 9.5.0, Gradle 9.6.0, JDK 17.
- No backend, account, telemetry or paid service.

### Remaining Phase 1 work
1. Validate the RIVO-07 benchmark and native-library CI checks before integration.
2. Complete UI overlay themes, short-screen sizing, Light contrast and selected-state semantics.
3. Add Android instrumentation for Keystore encryption, tampering, key loss, migration and DataStore corruption/relaunch.
4. Verify tab restoration and private-tab exclusion after force-stop/relaunch.
5. Review release AAB native libraries, 16 KB ELF/ZIP alignment, AAB bundle alignment and 16 KB runtime.
6. Run engine harness on physical ARM64 devices with 3–4 GB RAM and a higher-memory reference device.
7. Record cold/warm startup, host+renderer PSS, CPU, 1/5/10-tab inactive memory, navigation stability, crash/ANR behavior, provider/engine versions and per-ABI APK/AAB sizes.
8. Profile bottlenecks with Android Profiler/Perfetto **before** changing tab policy, runtime settings, warm-up or packaging.
9. Revisit engine ADR only after repeatable device measurements and browser feature parity.
10. Add per-site permission management/revoke controls and complete accessibility/security review.

### Risks and mitigations
- GeckoView footprint: compare release artifacts and measured harness output; do not strip required native libraries.
- Inactive tabs: deactivate sessions and keep the eight-tab MVP cap until profiling justifies change.
- Private mode: native private sessions; do not persist private tab state/history/bookmarks.
- Permissions: deny by default until explicit controls exist.
- 16 KB pages: inspect prebuilt GeckoView native libraries in artifacts and test on a 16 KB Android image.

## Phase 2 — Advanced browser
- Advanced Tab Universe, Privacy Intelligence Center and Transfer Studio.
- Reader mode, QR scanning, verified extension workflows and Developer Mode.

## Phase 3 — optimization and distribution
- Engine-specific verified features, optional sync after privacy/cost review, translation, accessibility, performance and release readiness.
