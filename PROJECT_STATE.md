# Project State

Updated: 2026-11-08

## Current phase
Phase 1 — Functional Browser MVP (authorized by user on 2026-11-08).

## Repository
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: main
- Phase 2 and Phase 3 have not been started.

## Phase 0 status
- Android foundation, modular engine adapters and engine comparison harness committed.
- Physical-device WebView vs GeckoView benchmark remains outstanding.
- Final engine decision remains open. GeckoView is the provisional Phase 1 MVP implementation candidate, not a final measured winner.

## Phase 1 implementation committed
- GeckoRuntime wrapper and one GeckoSession per browser tab.
- Browser navigation and URL/search omnibox resolver (HTTPS-preferring; unsupported schemes are treated as search queries).
- Back, forward, reload and stop.
- Multiple tabs with active/inactive session management and an 8-tab MVP safety cap.
- Private tabs use GeckoView native private-session settings; app history and bookmarks are not written for private tabs.
- Tracking protection enabled per session.
- Site content, Android and media permissions are denied until an explicit permission mediation UI is implemented.
- Engine-reported connection security state shown in browser chrome.
- Local DataStore bookmarks, bounded browsing history (300 entries), and persisted Dark/Light/Glass appearance selection.
- Downloads are streamed from GeckoView response bodies into the app-specific Downloads directory; no broad storage permission is requested.
- Centralized semantic color schemes for Dark, Light and Premium Glass.
- Lifecycle-aware ViewModel and Compose browser UI.
- JUnit tests for URL/search input resolver.
- CI updated to build/lint the GeckoView MVP and run unit tests; old superseded runs were failing due CI action setup and AGP 9 built-in Kotlin plugin changes. Latest CI result remains pending.

## Pending verification / known limitations
- Successful final CI build, lint and unit-test run not yet confirmed.
- No APK artifact has been verified/downloaded.
- No physical-device install or smoke test.
- No 3–4 GB device memory/performance benchmark.
- Downloads currently remain in app-specific storage; user-facing file open/share/export workflow is not yet implemented.
- Site permissions are deny-by-default; user-facing per-site allow/deny controls are not yet implemented.
- Browser tab state/session restoration across process death is not yet implemented.
- Final engine selection remains pending Phase 0 device benchmark.

## Next steps
1. Complete CI build/lint/test and fix all compile/runtime blockers.
2. Add safe download open/share/export UI and per-site permission mediation.
3. Test navigation, tab lifecycle, private mode, persistence, security state and downloads on emulator/device.
4. Benchmark on representative 3–4 GB Android devices.
5. Update feature matrix, security review and release readiness after evidence is collected.
