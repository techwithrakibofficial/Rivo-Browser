# Rivo Browser

Rivo Browser is a free, privacy-focused Android browser designed for low- and mid-range phones, including devices with 3–4 GB RAM.

## Project status
- Canonical source: `main`
- Current integration candidate: [PR #37](https://github.com/techwithrakibofficial/Rivo-Browser/pull/37)
- PR #37 combines Account A's Reader Mode, QR scanning, Transfer Studio and Developer Mode with Account B's site permissions, Privacy Intelligence Center, notification and private-download hardening.
- PR #35 and #36 must not be merged independently because they overlap in shared browser files.
- The integration candidate is not release-ready until exact-head CI, device QA, security review and production signing gates pass.

## Main-branch browser foundation
- GeckoView session architecture with navigation, URL/search omnibox and browser controls.
- Multi-tab browsing with an eight-tab cap and native private sessions.
- Advanced Tab Universe with title/URL search, Normal/Private/Active filters, sorting and accessibility semantics.
- Local bookmarks, history and normal-tab restoration.
- Encrypted browser metadata using Android Keystore AES-256-GCM.
- Confirmation-gated downloads, app-specific storage, safe filename/path handling and a 250 MB cap.
- Dark, Light and Premium Glass appearance modes with a secondary Quick Appearance Picker.

## PR #37 integration candidate
- Native Reader Mode through GeckoView page extraction; gated/non-readerable pages are not bypassed.
- QR-only Google Code Scanner with bounded payload sanitation and explicit open/search confirmation.
- Transfer Studio with search, filters, progress, best-effort cancellation and managed file actions.
- Read-only Developer Mode diagnostics. Remote debugging, console output and about:config remain disabled.
- HTTPS-origin site permission mediation and default-deny behavior for unsupported capabilities.
- Privacy Intelligence Center with actual Gecko permission state and revocation constraints.
- HTTPS-only website notifications; private-mode and insecure-origin notifications are rejected.
- Android runtime permission mediation for supported location/camera/microphone requests.
- Private download metadata exclusion and private file cleanup on tab close.
- Clear Browsing Data for Gecko site data/settings and local history/download metadata.

## Build environment
- JDK 17, Gradle 9.6.0.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- GeckoView Stable: `org.mozilla.geckoview:geckoview:159.0.20261022150525`.

Common tasks:
- `:app:assembleDebug`
- `:app:lintDebug`
- `:app:testDebugUnitTest`
- `:engine-gecko:testDebugUnitTest`
- `:app:bundleRelease`

CI release AABs are unsigned QA artifacts. They are not production releases.

## Known limitations
- Reader Mode works only when GeckoView reports a page as readerable and does not bypass gated content.
- Google Code Scanner requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume, retry and resumable queues are not implemented.
- Developer Mode is read-only.
- GeckoView WebExtensions engine capability does not mean Rivo has an add-on manager or marketplace.
- Private browsing does not hide network activity from websites, ISPs, DNS resolvers or network operators.
- Normal downloaded file bytes are not encrypted by the metadata cipher.
- Tracking protection does not imply complete tracker blocking, anonymity or a tracker count.

## Release blockers
- Exact-head PR #37 CI and API 35 instrumentation.
- Resolved dependency/SBOM/advisory review.
- Physical-device QA, 3–4 GB RAM performance measurements and 16 KB runtime verification.
- TalkBack, narrow-width, large-font, reduced-motion and three-theme manual checks.
- OWASP MASVS review and secure production signing.

## Documentation
- Master specification: MASTER_SPEC.md
- Project state: PROJECT_STATE.md
- Handoff: HANDOFF.md
- Development plan: DEVELOPMENT_PLAN.md
- Architecture: ARCHITECTURE.md
- Design system: DESIGN_SYSTEM.md
- Feature matrix: FEATURE_MATRIX.md
- Testing: TESTING.md
- Security: SECURITY.md
- Engine benchmark protocol: docs/research/BENCHMARK_PROTOCOL.md
