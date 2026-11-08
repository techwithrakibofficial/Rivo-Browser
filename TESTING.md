# Testing Plan

## Automated CI
- Android 17 API 37.2 compile platform; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- JVM unit tests for URL/search resolver.
- Upload separate ARM64 and ARMv7 debug APK artifacts (latest workflow update pending verification).

## Last verified CI checkpoint
Run 42460649170 completed successfully:
- :app:assembleDebug — PASS
- :app:lintDebug — PASS
- :app:testDebugUnitTest — PASS
- Debug artifact upload — PASS

A later workflow change added separate ARM64/ARMv7 artifact upload; its result must be verified independently.

## Phase 1 functional checks
1. Initial home page.
2. HTTPS URL, explicit HTTP URL, bare domain and search term.
3. Unsupported schemes (javascript:, file:, content:) become search queries in the omnibox.
4. Back, forward, reload and stop.
5. Create/switch/close tabs; verify inactive sessions deactivate.
6. Private session; verify app-side history/bookmarks are unchanged.
7. Bookmark add/remove and persistence.
8. History persistence and clear.
9. Dark/Light/Glass persistence and system-bar contrast.
10. Download response streams, filename sanitization, duplicate names, 250 MB cap, open/share and private-mode notice.
11. Geolocation/camera/microphone/notification requests remain denied until permission UX exists.
12. Certificate errors are not bypassed; security label comes from GeckoView callback.
13. Activity recreation and background/foreground transitions.
14. Force-stop/relaunch and document tab restoration limitation.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB device if available. Record model, chipset, Android build, ABI, GeckoView version, individual APK size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. No emulator-only result should be extrapolated to all devices.

## Current status
CI passed at the checkpoint above. No physical-device install, smoke test or benchmark has been completed in this session.
