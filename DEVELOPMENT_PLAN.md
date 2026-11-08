# Development Plan

## Phase 0 — foundation checkpoint
- Android foundation, modular engine API and WebView/GeckoView probe variants.
- Official documentation research and benchmark protocol.
- Physical-device benchmark and final engine decision remain pending.

## Phase 1 — authorized 2026-11-08
Functional browser MVP:
1. GeckoView-backed browser session adapter with process-scoped runtime and Activity-safe view lifecycle.
2. Navigation, omnibox URL/search handling, loading state and engine-reported security status.
3. Multi-tab lifecycle, active/inactive session management and native private sessions.
4. Local bookmarks and regular browsing history with encrypted metadata at rest.
5. Safe download streaming and app-specific storage with open/share.
6. Centralized Dark, Light and Premium Glass design tokens and secondary Quick Appearance Picker.
7. Persist and restore regular HTTP(S) tabs across process recreation; never persist private tabs.
8. Automated build/lint/unit tests, release-bundle packaging checks, device smoke tests and security review.

## Dependencies and build baseline
- GeckoView Stable artifact: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- Android Gradle Plugin 9.5.0, Gradle 9.6.0, JDK 17.
- Compose BOM 2026.06.00; Activity Compose 1.13.0; Lifecycle ViewModel Compose 2.10.0.
- DataStore Preferences 1.3.0; coroutines Android 1.11.0.
- No backend, account, telemetry or paid service.

## Risks and mitigations
- GeckoView package/runtime footprint: ARM64 debug APK measured at 208,688,414 bytes (~199.0 MiB), ARMv7 at 169,493,492 bytes (~161.7 MiB). These are debug measurements; compare release size and WebView footprint before final engine selection.
- Inactive tabs: set sessions inactive and cap MVP at eight pending measurements.
- Private mode: use native GeckoSession private mode; exclude private tabs from app-side history, bookmarks and restoration state.
- Site permissions: deny by default until explicit per-site permission management is complete; HTTPS notification and persistent-storage requests currently have Allow/Deny prompts.
- Downloads: HTTP(S) only, sanitize filenames, 250 MB cap, app-specific storage and scoped FileProvider.
- Glass performance: translucent surfaces, no heavy blur, solid fallback.
- Engine choice: GeckoView is provisional for MVP; physical-device comparison with WebView remains required.
- Encrypted metadata: verify key-unavailable and tampered-ciphertext behavior; prevent silent overwrite of existing records.

## Acceptance criteria
- CI assemble, lint and unit tests pass for the current source.
- Minified release AAB builds successfully and its size is recorded.
- Navigation and omnibox work for URLs and search terms.
- Tabs create/switch/close without session leaks.
- Private session is native private mode and does not enter local history/bookmarks or restoration state.
- Bookmarks/history and appearance persist locally; sensitive metadata is encrypted.
- Downloads are user-visible and can be opened/shared safely.
- All three appearance modes maintain readable browser chrome.
- Regular tabs restore after process recreation without restoring private tabs.
- Manual device smoke tests and performance/security evidence are documented.

## Remaining Phase 1 work
1. Verify the latest source in GitHub Actions: assembleDebug, lintDebug, testDebugUnitTest and bundleRelease with R8/resource shrinking.
2. Correct encrypted-metadata error semantics and add regression tests for legacy migration, ciphertext tampering, unavailable/lost Keystore keys and read-modify-write preservation.
3. Test Activity lifecycle, regular-tab restoration and private-tab exclusion on device.
4. Analyze minified release AAB size and compare native-library footprint; do not remove required Gecko engine components merely to chase APK size. Verify 16 KB native-library alignment where applicable.
5. Install and smoke-test ARM64/ARMv7 builds on physical devices.
6. Add per-site permission management and revoke UI.
7. Complete accessibility, dependency vulnerability and OWASP MASVS reviews.
8. Run a comparative WebView/GeckoView benchmark on representative 3–4 GB hardware.

## Phase 2
Advanced Tab Universe, Privacy Intelligence Center, Transfer Studio, QR, reader mode, extension workflows where verified, Developer Mode.

## Phase 3
Engine-specific features, optional sync, translation feasibility, optimization and distribution readiness.