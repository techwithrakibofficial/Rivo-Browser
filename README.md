# Rivo Browser

A free, privacy-focused Android browser targeting low- and mid-range devices.

## Current phase
**Phase 1 — Functional Browser MVP.** Source includes GeckoView browser sessions, navigation, tabs, private sessions, local bookmarks/history, downloads, encrypted local browsing metadata and Dark/Light/Premium Glass chrome. Activity lifecycle handling and regular-tab restoration are implemented in source. PR #24's Tab Universe accessibility improvements have passed PR-head CI; physical-device verification remains pending.

## MVP source capabilities
- URL/search omnibox, back, forward, reload and stop; Android system Back follows page history when available.
- Multiple tabs; Advanced Tab Universe adds title/URL search, Normal/Private/Active filters and active-first/title/type sorting; semantic filter chips scroll horizontally; tab search has a persistent label and active/close actions expose TalkBack semantics; inactive Gecko sessions are deactivated; Activity backgrounding deactivates and unfocuses all sessions; current cap is eight.
- Regular HTTP(S) tabs are persisted/restored after process recreation; private tabs are never persisted.
- Native private sessions; private tabs do not enter app-side history or bookmarks. PR #36 candidate also deletes private download files on tab close/controller disposal and next-process startup.
- Tracking protection enabled per session.
- PR #36 candidate adds secure-origin geolocation/camera/microphone mediation with Android runtime permissions and explicit per-site consent; notification consent is coordinated with Android 13+ permission. Unsupported capabilities remain default-denied.
- Local bookmarks and bounded history.
- PR #36 candidate adds a confirmation-gated Clear Browsing Data action for Gecko site data/settings and local history/download metadata; bookmarks and normal downloaded files are retained.
- Bookmarks, history, restorable-tab metadata and download metadata are encrypted at rest with Android Keystore AES-256-GCM; legacy plaintext is migrated on read.
- PR #36 candidate hardens downloads to HTTPS-only, normalizes MIME metadata, enforces filename/path and 250 MB limits, and keeps private download files in a cleanup-only directory.
- Dark, Light and Premium Glass themes with a secondary Quick Appearance Picker.

## Build environment
- JDK 17, Gradle 9.6.0.
- Android compile SDK: API 37.2.
- targetSdk: 36.
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.

Build tasks: :app:assembleDebug, :app:lintDebug, :app:testDebugUnitTest and :app:bundleRelease.

The app creates separate ARM64 and ARMv7 debug APK outputs. Install only the APK matching the device ABI. The release bundle built by CI is unsigned and for QA only; no production signing is configured.

## Verification

Latest feature CI evidence: PR #23 run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026) passed on PR head `2f4027ac5dd5f8b60241485527c290b8b27916fa` (assembleDebug, lintDebug, JVM unit tests and minified release bundle); PR #23 was merged as `d192baf0f38db835904aa76a901d7e7cb2a24e31`. This is PR-head evidence, not a separately confirmed push-triggered Main run.

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

## PR #36 candidate (not yet Main-integrated)

The security/QA candidate adds GeckoView site-permission mediation, encrypted Android permission-denial state, private-download cleanup, Clear Browsing Data, HTTPS-only downloads and resolved dependency lockfiles. Check PR #36's latest GitHub Actions results for exact-head validation; source implementation and CI artifacts do not replace physical-device QA or independent security certification.
