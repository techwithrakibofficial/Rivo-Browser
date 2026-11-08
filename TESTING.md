# Testing Plan

## Automated CI
- Android compile platform: API 37.2; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- JVM unit tests for URL/search resolution and safe restorable-tab URL policy.
- Build minified release AAB with R8 and resource shrinking.
- Report APK/AAB sizes and upload unsigned QA AAB plus separate ARM64/ARMv7 debug APK artifacts.

## Last verified CI checkpoint
GitHub Actions run #143 (42461668698) completed successfully on commit 89139a036bb36829e52681df9eae7c47ae77197e:
- :app:assembleDebug — PASS
- :app:lintDebug — PASS
- :app:testDebugUnitTest — PASS
- ABI APK size reporting — PASS
- ARM64 debug artifact upload — PASS (rivo-browser-arm64-debug, ZIP 109,529,496 bytes)
- ARMv7 debug artifact upload — PASS (rivo-browser-armv7-debug, ZIP 105,557,703 bytes)

Run #143 predates the latest Activity lifecycle, tab restoration, R8/AAB and encrypted-metadata commits. Its recorded job steps did not include the current workflow's minified release AAB step. It must not be reported as verification of the latest source.

## Latest source verification status
- Latest source checkpoint before this documentation update: 9df07aa84f2b3e94c8a383cea486ced09652b8e5.
- No workflow run or commit status was returned for that source commit during recovery.
- Latest source has not yet been confirmed by CI.
- No physical-device install, force-stop/relaunch test or 3–4 GB benchmark is claimed.

## Phase 1 functional checks
1. Initial home page.
2. HTTPS URL, explicit HTTP URL, bare domain and search term.
3. Unsupported schemes (javascript:, file:, content:) become search queries in the omnibox.
4. Back, forward, reload and stop.
5. Create/switch/close tabs; verify inactive sessions deactivate.
6. Private session; verify app-side history/bookmarks and restorable-tab state are unchanged.
7. Bookmark add/remove and encrypted persistence.
8. History persistence, encryption and clear.
9. Dark/Light/Glass persistence and system-bar contrast.
10. Download confirmation/cancel, response-stream closure, filename sanitization, duplicate names, atomic finalization, 250 MB cap, encrypted metadata restoration, open/share and private-mode notice.
11. HTTPS notification and persistent-storage requests show an explicit Allow/Deny prompt; non-HTTPS and private-mode requests are handled according to their scoped policy.
12. Geolocation/camera/microphone/media and unsupported content permissions remain denied; verify no undeclared Android hardware permission is requested.
13. Certificate errors are not bypassed; security label comes from GeckoView callback.
14. Activity recreation and background/foreground transitions; verify all Gecko sessions deactivate on stop and only the selected tab reactivates on start.
15. Android system Back navigates Gecko history when available; otherwise verify normal Android back behavior.
16. Force-stop/relaunch; verify regular HTTP(S) tabs restore, selected normal tab is restored, and private tabs do not reappear.
17. Verify release bundle build completes with R8 and resource shrinking; inspect AAB size and test runtime behavior of minified code.
18. Encrypted storage failure tests: legacy plaintext migration, round-trip with fresh IV, tampered ciphertext, truncated payload, missing/invalid Keystore key and read-modify-write preservation.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB Android device if available. Record model, chipset, Android build, ABI, GeckoView version, APK/AAB size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. Do not extrapolate emulator-only results to all 3–4 GB phones.

## Current status
Run #143 remains a successful historical checkpoint only. Latest-source CI, minified AAB measurement, encrypted-storage regression tests and physical-device verification are pending. APK size is a significant optimization target before release.