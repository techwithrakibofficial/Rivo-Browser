# Development Plan

## Phase 0 — foundation checkpoint
- Android foundation, modular engine API and WebView/GeckoView probe variants.
- Official documentation research and benchmark protocol.
- Physical-device benchmark and final engine decision remain pending.

## Phase 1 — authorized 2026-11-08
Functional browser MVP:
1. GeckoView-backed browser session adapter with lifecycle-safe runtime/session ownership.
2. Navigation, omnibox URL/search handling, loading state and security status.
3. Multi-tab lifecycle, active/inactive session management and private sessions.
4. Local bookmarks and regular browsing history.
5. Safe downloads integration.
6. Centralized Dark, Light and Premium Glass design tokens and quick appearance picker.
7. Automated tests, CI build/lint, manual test protocol and security review.

## Phase 1 dependencies
- Mozilla GeckoView Stable (currently pinned candidate 159.0.20261022150525).
- AndroidX Compose BOM 2026.09.00, Activity Compose 1.13.0.
- DataStore Preferences 1.3.0 for small local settings and bounded local history/bookmarks; Room only if dataset needs grow beyond bounded lists.
- No backend, account, telemetry or paid service.

## Risks and mitigations
- GeckoView APK size and RAM: keep inactive sessions inactive, benchmark on 3–4 GB devices, avoid excessive retained tabs.
- GeckoView API version drift: pin stable artifact and compile against that artifact, not mozilla-central API snapshots.
- Private mode: use native GeckoSession private mode; ensure app-side history is not written for private tabs.
- Download handling: restrict to HTTP(S), sanitize names, use DownloadManager and avoid arbitrary file paths.
- Glass performance: keep blur optional/low-tier and use solid surfaces on constrained devices.
- Engine choice: provisional for Phase 1; physical-device benchmark still required before final release.

## Acceptance criteria
- Build and lint pass for the app.
- Navigation and omnibox work for valid URLs and search terms.
- Tabs create/switch/close without leaked sessions.
- Private session is configured private by GeckoView and does not enter local history.
- Bookmark/history records persist only for normal browsing.
- Theme selection persists and applies immediately.
- Downloads are user-visible and safely handed to Android DownloadManager.
- Known limitations and device-test evidence are documented.

## Phase 2
Advanced Tab Universe, Privacy Intelligence Center, Transfer Studio, QR, reader mode, extension workflows where verified, Developer Mode.

## Phase 3
Engine-specific features, optional sync, translation feasibility, optimization and distribution readiness.

## Exit gates
Each phase requires tests, documented limitations, known-issue review and explicit scope approval.
