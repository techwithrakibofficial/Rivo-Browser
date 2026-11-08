# Testing Plan

## Automated CI
- Android 17 API 37.2 compile platform; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- JVM unit tests for URL/search resolver.
- Upload separate ARM64 and ARMv7 debug APK artifacts.

## Last verified CI checkpoint
GitHub Actions run #143 (`42461668698`) completed successfully on commit `89139a036bb36829e52681df9eae7c47ae77197e`:
- :app:assembleDebug — PASS
- :app:lintDebug — PASS
- :app:testDebugUnitTest — PASS
- ABI APK size reporting — PASS
- ARM64 debug artifact upload — PASS (`rivo-browser-arm64-debug`, ZIP 109,529,496 bytes)
- ARMv7 debug artifact upload — PASS (`rivo-browser-armv7-debug`, ZIP 105,557,703 bytes)

Artifacts expire 2026-11-22. Extracted APKs: ARM64 208,688,414 bytes (~199.0 MiB); ARMv7 169,493,492 bytes (~161.7 MiB). ZIP and APK sizes are distinct.

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
11. HTTPS notification and persistent-storage requests show an explicit Allow/Deny prompt; non-HTTPS and private-mode requests are handled according to their scoped policy.
12. Geolocation/camera/microphone/media and unsupported content permissions remain denied; verify no undeclared Android hardware permission is requested.
13. Certificate errors are not bypassed; security label comes from GeckoView callback.
14. Activity recreation and background/foreground transitions; verify all Gecko sessions deactivate on stop and only the selected tab reactivates on start.
15. Android system Back navigates Gecko history when available; otherwise verify normal Android back behavior.
16. Force-stop/relaunch; verify regular HTTP(S) tabs restore, selected normal tab is restored, and private tabs do not reappear.
17. Verify release bundle build completes with R8 and resource shrinking; inspect AAB size and test runtime behavior of minified code.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB device if available. Record model, chipset, Android build, ABI, GeckoView version, individual APK size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. No emulator-only result should be extrapolated to all devices.

## Current status
CI passed at the checkpoint above. Activity lifecycle, process-death tab restoration and R8/AAB changes are newer; their CI run is pending. Both APK archives were downloaded, extracted and inspected for ABI-specific native library paths. No physical-device install, smoke test or benchmark has been completed in this session. APK size is a significant optimization target before release.
