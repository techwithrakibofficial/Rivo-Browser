# Rivo Browser

A free, privacy-focused Android browser targeting low- and mid-range devices.

## Current phase
**Phase 1 — Functional Browser MVP.** Source includes GeckoView browser sessions, navigation, tabs, private sessions, local bookmarks/history, downloads, encrypted local browsing metadata and Dark/Light/Premium Glass chrome. Activity lifecycle handling and regular-tab restoration are implemented in source. The latest source changes still require CI and physical-device verification.

## MVP source capabilities
- URL/search omnibox, back, forward, reload and stop; Android system Back follows page history when available.
- Multiple tabs; inactive Gecko sessions are deactivated; Activity backgrounding deactivates and unfocuses all sessions; current cap is eight.
- Regular HTTP(S) tabs are persisted/restored after process recreation; private tabs are never persisted.
- Native private sessions; private tabs do not enter app-side history or bookmarks.
- Tracking protection enabled per session.
- Site/device/media permissions denied until proper permission mediation is implemented; HTTPS notification and persistent-storage requests have explicit Allow/Deny prompts.
- Local bookmarks and bounded history.
- Bookmarks, history, restorable-tab metadata and download metadata are encrypted at rest with Android Keystore AES-256-GCM; legacy plaintext is migrated on read.
- Downloads saved to app-specific storage (250 MB per file), with Open/Share actions; metadata is persisted and restored after path validation.
- Dark, Light and Premium Glass themes with a secondary Quick Appearance Picker.

## Build environment
- JDK 17, Gradle 9.6.0.
- Android compile SDK: API 37.2.
- targetSdk: 36.
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.

Build tasks: :app:assembleDebug, :app:lintDebug, :app:testDebugUnitTest and :app:bundleRelease.

The app creates separate ARM64 and ARMv7 debug APK outputs. Install only the APK matching the device ABI. The release bundle built by CI is unsigned and for QA only; no production signing is configured.

## Verification

Latest integrated Main CI checkpoint: [GitHub Actions run #246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810), on commit `84303588c2560db55bfb7ae2081d8f714be532e7`. Its single build job succeeded.

Verified: `:app:assembleDebug`, `:app:lintDebug`, `:app:testDebugUnitTest`, minified `-Privo.abiSplits=false :app:bundleRelease`, ABI APK/AAB size reporting, unsigned QA AAB upload, ARM64 APK upload and ARMv7 APK upload.

This validates CI for that exact commit only. It does not establish physical-device behavior, accessibility compliance, production signing or release readiness. Run #143 APK byte counts are historical and are not attributed to the current commit. No physical-device installation or 3–4 GB benchmark is verified.


## Documentation
- Master specification: MASTER_SPEC.md
- Project state: PROJECT_STATE.md
- Development plan: DEVELOPMENT_PLAN.md
- Architecture: ARCHITECTURE.md
- Design system: DESIGN_SYSTEM.md
- Feature matrix: FEATURE_MATRIX.md
- Testing: TESTING.md
- Security: SECURITY.md
- Handoff: HANDOFF.md
- Engine benchmark protocol: docs/research/BENCHMARK_PROTOCOL.md

## Limitations and release blockers
- Encrypted metadata failures now propagate and sensitive writes use fail-closed validation; Android Keystore/DataStore instrumentation is still required for on-device verification.
- Per-site permission management/revoke UI and process-death tab restoration validation are pending.
- Download file contents are stored as ordinary files in app-specific storage and are not encrypted by the metadata cipher; private mode does not erase user-saved downloads.
- Private browsing does not hide traffic from websites, ISPs or network operators.
- GeckoView remains the provisional MVP engine; final selection awaits controlled 3–4 GB device benchmarking.
- Phase 2 and Phase 3 features have not started.