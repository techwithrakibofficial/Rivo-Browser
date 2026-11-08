# Handoff

## Current phase
Phase 1 — Functional Browser MVP (authorized 2026-11-08).

## Repository
https://github.com/techwithrakibofficial/Rivo-Browser

## Implemented
- GeckoView Stable candidate with process-scoped GeckoRuntime and per-tab GeckoSession.
- Activity-context GeckoView lifecycle is separated from ViewModel-owned browser sessions.
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
- CI run 42460649170: assembleDebug SUCCESS, Android lint SUCCESS, unit tests SUCCESS, artifact upload SUCCESS.
- CI run URL: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42460649170
- Compile SDK is Android 17 API 37.2; targetSdk remains 36.
- GeckoView dependency coordinate is org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Separate ABI artifact workflow has just been configured; its latest run must be checked before reporting those artifacts as available.
- No physical-device install, emulator smoke test or performance benchmark is claimed.

## Remaining
1. Confirm CI for separate ARM64 and ARMv7 APK artifacts.
2. Verify individual APK file sizes and install both split outputs on supported devices.
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
