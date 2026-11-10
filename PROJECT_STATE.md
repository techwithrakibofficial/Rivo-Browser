# Rivo Browser — Project State

Updated: 2026-11-09

## Source of truth
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Canonical branch: `main`
- Main baseline at integration start: `0d77826e655267c671498334c11ffad8d1dd5b14`
- Active integration candidate: PR #37 — Reader Mode, QR, Transfer Studio, Developer Mode, site permissions and privacy hardening.
- Integration branch: `integration/phase2-core-security-2026-11-09`
- PR #35 and PR #36 are source workstreams. Do not merge them independently: they overlap in BrowserController, BrowserScreen and GeckoBrowserTab. Their feature sets have been combined in PR #37.

## Implemented on Main baseline
- GeckoView runtime/session architecture, navigation, omnibox, back/forward/reload/stop and engine-reported connection security.
- Multi-tab browsing with an eight-tab cap; native private sessions.
- Local bookmarks/history and normal-tab restoration; private tabs excluded from app-side history/bookmarks/restoration.
- Encrypted browser metadata using Android Keystore AES-256-GCM.
- Confirmation-gated HTTP(S) downloads, app-specific storage, filename/path protections, atomic finalization and 250 MB cap.
- Dark, Light and Premium Glass appearance modes with secondary Quick Appearance Picker.
- Advanced Tab Universe search/filter/sort, accessible labels and policy regression coverage.
- Security, storage, tab-restoration and supply-chain workflow foundations.

## Integrated in PR #37 candidate (not yet Main)
- Native GeckoView Reader Mode using page extraction, readerability/gated-page checks, bounded in-memory article text and adjustable text size.
- QR-only Google Code Scanner; no camera permission for the QR workflow; bounded/sanitized payloads and explicit open/search confirmation.
- Transfer Studio with search, status filters, bounded recent list, progress, best-effort cancellation, open/share and safe removal of managed files.
- Read-only Developer Mode diagnostics; remote debugging, console output and about:config remain disabled.
- HTTPS-origin site permission mediation, default-deny unsupported permissions, normal-mode permission inventory/reset and Privacy Intelligence Center.
- Secure website notification handling; private and non-HTTPS notification sources are denied.
- Android runtime permission mediation for supported location/camera/microphone requests, persisted denial state and explicit notification permission flow.
- Private download metadata exclusion, private download directory cleanup on tab close and safe restored-file validation.
- Clear Browsing Data for Gecko site data/settings and local history/download metadata, while retaining bookmarks and normal downloaded file bytes.
- Gradle dependency lockfiles and dependency graph workflow improvements.

## Integration corrections already applied
- GeckoView 159 builder API uses `consoleOutput(false)`; remote debugging remains disabled.
- Reader extraction handles nullable GeckoResult metadata and errors.
- ML Kit dependency metadata is correctly nested inside the application manifest element.
- Shared controller, screen and engine files preserve both feature sets.
- Transfer progress/cancellation is combined with HTTPS-only validation and private-download lifecycle handling.
- Private download removal validates the managed private directory; private transfers are cancelled/cleaned when their tab closes.
- Android CI now uploads instrumentation XML/HTML reports even when tests fail.

## Validation status
- PR #37 exact-head Android CI is running on the latest integration candidate; check the exact head SHA and run result before merge.
- PR #36 source CI passed assemble, lint, JVM tests, Gecko policy tests, benchmark APK/AAB builds, minified release bundle and static 16 KB alignment checks. Its API 35 `:app:connectedDebugAndroidTest` step failed; the failing test identity was not present in the downloaded job log. PR #37 now uploads test reports on failure for diagnosis.
- PR #35 exposed and fixed two source issues before integration: GeckoView builder method/nullable extraction handling, and malformed ML Kit manifest metadata placement.
- Source SBOM and Gradle dependency graph workflows succeeded on the pre-lockfile-refresh candidate. OSV scanned five resolved Gradle lockfiles (814 packages) and reported no issues. The lockfile job updated `app/gradle.lockfile` on the branch; the new bot-authored commit requires a fresh exact-head Android CI run.
- No physical-device QA, 3–4 GB RAM benchmark, 16 KB runtime verification, independent OWASP MASVS certification, production signing or store release is claimed.

## Release blockers
1. Exact-head PR #37 Android CI and API 35 instrumentation must pass; inspect uploaded XML/HTML reports if any test fails.
2. Review OSV findings and verify resolved Gradle dependency inventory/lockfile coverage.
3. Perform physical-device QA for 3–4 GB devices, process death/restoration, private isolation, permission grant/deny/revoke, notifications, downloads, themes, TalkBack, narrow widths and large fonts.
4. Verify 16 KB runtime behavior on a compatible device.
5. Complete OWASP MASVS control-by-control assessment and record residual risks.
6. Configure production signing securely and complete release review. CI AABs are unsigned QA artifacts only.

## Current phase
Phase 1 stabilization and Phase 2 feature integration. Do not declare the MVP release-ready until automated, instrumentation and manual/device acceptance evidence is recorded.
