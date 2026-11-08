# Handoff

## Current phase
Phase 1 — Functional Browser MVP (authorized 2026-11-08).

## Repository
https://github.com/techwithrakibofficial/Rivo-Browser

## Implemented
- GeckoView Stable candidate with process-scoped GeckoRuntime and per-tab GeckoSession.
- Activity-context GeckoView lifecycle is separated from ViewModel-owned browser sessions.
- Activity start/stop now gates Gecko session activation; backgrounded tabs are deactivated and unfocused.
- Android system Back navigates page history when available.
- URL/search omnibox; back, forward, reload and stop.
- Tab switching/closing; inactive sessions are deactivated; 8-tab cap.
- Private Gecko sessions, excluded from app-side history/bookmarks.
- Tracking protection enabled; site content, Android and media permissions denied by default.
- Engine-reported TLS connection status.
- Local bookmarks/history, clear history and persisted Dark/Light/Premium Glass appearance.
- Downloads stream to app-specific storage, max 250 MB per file; scoped FileProvider supports open/share.
- Centralized semantic color, spacing and shape tokens; secondary Quick Appearance Picker; system bar contrast.
- JUnit tests for URL/search input resolution.
- ABI split configuration for arm64-v8a and armeabi-v7a.

## Verified
- CI run #143 (`42461668698`) on commit `89139a036bb36829e52681df9eae7c47ae77197e`: assembleDebug PASS, Android lint PASS, unit tests PASS, ABI size report PASS, ARM64 artifact upload PASS, ARMv7 artifact upload PASS.
- CI run URL: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42461668698
- ARM64 artifact `rivo-browser-arm64-debug`: ZIP 109,529,496 bytes; expires 2026-11-22.
- ARMv7 artifact `rivo-browser-armv7-debug`: ZIP 105,557,703 bytes; expires 2026-11-22.
- Compile SDK is Android 17 API 37.2; targetSdk remains 36.
- GeckoView dependency coordinate is org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Extracted APK sizes: ARM64 208,688,414 bytes (~199.0 MiB); ARMv7 169,493,492 bytes (~161.7 MiB). Native library paths inside each APK are limited to the matching ABI.
- SHA-256 ARM64: `9bca287c9407d8d060903be24db0405c759aacd39828af32f643fa92d7641223`; ARMv7: `5af075f25765f209dff18f304cb0bf26803e9655f7b9bb7b71ae7b84f4ce892d`.
- No physical-device install, emulator smoke test or performance benchmark is claimed.

## Remaining
1. Investigate GeckoView binary footprint and safe APK-size optimization.
2. Install both APKs on physical supported devices and run smoke tests.
3. Add per-site permission UI.
4. Add process-death tab/session restoration.
5. Complete accessibility and MASVS review.
6. Perform 3–4 GB device benchmark before final engine decision.

## Known limitations
- Private mode does not hide traffic from websites, ISPs, DNS resolvers or network operators.
- Downloaded files remain on-device in app-specific storage and are not automatically erased by private mode.
- Permission prompts are currently denied; user-facing per-site controls are pending.
- History/bookmarks are local-only and not separately encrypted by the app.
- GeckoView remains the provisional MVP engine, not a benchmark-confirmed final choice.

## Scope boundary
Phase 2/3 features are not started.
