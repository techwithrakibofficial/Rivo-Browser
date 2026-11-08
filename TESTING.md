# Testing Plan

## CI checks
- Assemble the GeckoView MVP debug APK.
- Run Android lint.
- Run JVM unit tests, including URL/search input resolver cases.
- Upload a temporary debug APK artifact (14-day retention).

## Phase 1 functional checks
1. Launch and initial home page.
2. HTTPS URL, explicit HTTP URL, bare domain and search term.
3. Unsupported schemes (javascript:, file:, content:) must not be loaded from the omnibox and should become search queries.
4. Back, forward, reload and stop.
5. Create/switch/close tabs; verify only selected tab is active and inactive sessions are deactivated.
6. Create private tab; visit pages; close it; verify app-side history/bookmarks are unchanged.
7. Bookmark, remove bookmark, relaunch and verify persistence.
8. Visit pages, clear history, relaunch and verify persistence.
9. Switch Dark/Light/Glass and relaunch; verify preference persistence and contrast.
10. Test download response streams, filename sanitization, duplicate names and private-mode download notice.
11. Trigger geolocation, camera, microphone and notification requests; confirm they are denied until permission UI exists.
12. Verify certificate errors are not bypassed and security label comes from GeckoView callback.
13. Rotate/recreate Activity and background/foreground transitions.
14. Force-stop/relaunch and document tab/session restoration limitation.

## Device benchmark
Use at least one 3 GB and one 4 GB device if available. Record device model, chipset, Android version, GeckoView version, APK size, cold/warm launch, PSS, CPU, thermal state, and crash/ANR behavior. Do not infer results from emulator-only tests.

## Current status
Source implementation and JUnit test files are committed. CI results, APK installation, emulator/physical-device testing and performance measurements remain unverified.
