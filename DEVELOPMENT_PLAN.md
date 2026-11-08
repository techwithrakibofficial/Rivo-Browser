# Development Plan

## Phase 0 — foundation checkpoint
- Android foundation, modular engine API and WebView/GeckoView probe variants.
- Official documentation research and benchmark protocol.
- Physical-device benchmark and final engine decision remain pending.

## Phase 1 — authorized 2026-11-08
Functional browser MVP:
1. GeckoView-backed browser session adapter with process-scoped runtime and Activity-safe view lifecycle.
2. Navigation, omnibox URL/search handling, loading state and engine-reported security status.
3. Multi-tab lifecycle, active/inactive session management and private sessions.
4. Local bookmarks and regular browsing history.
5. Safe download streaming and app-specific storage with open/share.
6. Centralized Dark, Light and Premium Glass design tokens and quick appearance picker.
7. Automated build/lint/unit tests, device smoke tests and security review.

## Dependencies and build baseline
- GeckoView Stable artifact: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- Android Gradle Plugin 9.5.0, Gradle 9.6.0, JDK 17.
- Compose BOM 2026.06.00; Activity Compose 1.13.0; Lifecycle ViewModel Compose 2.10.0.
- DataStore Preferences 1.3.0; coroutines Android 1.11.0.
- No backend, account, telemetry or paid service.

## Risks and mitigations
- GeckoView package/runtime footprint: ARM64 debug APK measured at 208,688,414 bytes (~199.0 MiB), ARMv7 at 169,493,492 bytes (~161.7 MiB); ABI splits prevent cross-ABI native libraries but do not eliminate the large Gecko engine payload. Compare WebView footprint and measure PSS on 3–4 GB devices before final engine selection.
- Inactive tabs: set sessions inactive and cap MVP at eight pending measurements.
- Private mode: use native GeckoSession private mode; exclude private tabs from app-side history/bookmarks.
- Site permissions: deny by default until an explicit per-site permission UI is complete.
- Downloads: HTTP(S) only, sanitize filenames, 250 MB cap, app-specific storage and scoped FileProvider.
- Glass performance: translucent surfaces, no heavy blur, solid fallback.
- Engine choice: GeckoView is provisional for MVP; physical-device comparison with WebView remains required.

## Acceptance criteria
- CI assemble, lint and URL resolver unit tests pass.
- Navigation and omnibox work for URLs and search terms.
- Tabs create/switch/close without session leaks.
- Private session is native private mode and does not enter local history/bookmarks.
- Bookmarks/history and appearance persist locally.
- Downloads are user-visible and can be opened/shared safely.
- All three appearance modes maintain readable browser chrome.
- Manual device smoke tests and performance/security evidence are documented.

## Remaining Phase 1 work
- Re-run CI after the documentation-only checkpoint and confirm the latest workflow.
- Investigate APK footprint: compare debug vs release packaging, inspect GeckoView distribution options and quantify a WebView baseline without removing required engine components.
- Install and smoke-test ARM64/ARMv7 APKs.
- Add per-site permission mediation and process-death tab restoration.
- Complete accessibility and MASVS review.
- Run comparative engine benchmark on representative hardware.

## Phase 2
Advanced Tab Universe, Privacy Intelligence Center, Transfer Studio, QR, reader mode, extension workflows where verified, Developer Mode.

## Phase 3
Engine-specific features, optional sync, translation feasibility, optimization and distribution readiness.
