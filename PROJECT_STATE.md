# Rivo Browser — Project State

Updated: 2026-11-10

## Source of truth
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Canonical branch: `main`
- Validated Main CI head: `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`
- Phase 2 integration: PR #38 merged; PR #40 merged signed-release workflow.

## Implemented in Main source
- GeckoView runtime/session architecture, navigation, omnibox, back/forward/reload/stop and engine-reported connection security.
- Multi-tab browsing with an eight-tab cap; native private sessions; private tabs excluded from app-side history, bookmarks and restoration.
- Advanced Tab Universe search/filter/sort and accessibility semantics.
- Local bookmarks/history/tab restoration/download metadata using Android Keystore AES-256-GCM for sensitive metadata, with fail-closed integrity handling.
- Confirmation-gated HTTPS downloads, filename/path validation, app-specific storage, temporary streaming, atomic finalization and 250 MB cap.
- Dark, Light and Premium Glass themes; secondary Quick Appearance Picker; restrained glass surfaces without real-time blur.
- Reader Mode via GeckoView page extraction, readerability/gated-content checks, bounded ephemeral text and adjustable text size.
- QR-only Google Code Scanner flow; no CAMERA permission required for QR scanning; payload sanitation and explicit confirmation before navigation.
- Transfer Studio search/filter, bounded recent list, progress, best-effort cancellation, managed Open/Share/Remove.
- Read-only Developer Mode diagnostics; remote debugging, console output and about:config remain disabled.
- HTTPS-origin site permission mediation, default-deny unsupported capabilities, permission inventory/reset and Privacy Intelligence Center.
- HTTPS-only website notifications, private-mode notification denial and non-exported notification receiver.
- Private download metadata exclusion, dedicated private-download directory and tab-close cleanup.
- Clear Browsing Data integration for Gecko site data/settings and local history/download metadata; bookmarks and normal downloaded file bytes are retained.
- Gradle dependency lockfiles, OSV scanning, source SBOM and resolved dependency graph workflows.

## Verified automated validation
- Main Android CI run [#42763857175](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42763857175) completed successfully on `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`.
- Passed: shipping assemble, Android lint, unit tests, Gecko engine policy tests, GeckoView/WebView benchmark builds, minified shipping release bundle, static 16 KB native-library/APK alignment checks, artifact uploads and API 35 Android security instrumentation.
- CI artifacts: ARM64 debug APK, ARMv7 debug APK, unsigned QA AAB, Android test reports, GeckoView benchmark and WebView benchmark. These are QA artifacts, not production-signed releases.
- Same-head OSV Scanner, Gradle Dependency Graph and Source SBOM workflows completed successfully (verified in Actions run results).
- Android CI success validates configured automation only; it does not replace physical-device acceptance, runtime 16 KB verification or an independent security assessment.

## Remaining release gates — not completed
1. Production signing: configure protected Actions secrets `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, `RIVO_UPLOAD_KEY_PASSWORD`; run signed-release workflow and verify signed APK/AAB + checksums. No secrets or signing material are available to this session.
2. Physical-device QA on real supported Android hardware: reader extraction, QR scanner availability, transfer cancel/remove, permissions, notifications, private-session isolation, process death/restoration.
3. Accessibility QA: TalkBack, 320 dp/narrow screens, large font, contrast and all three appearance modes.
4. Profile 3–4 GB RAM devices and compare GeckoView/WebView under a repeatable workload.
5. Verify 16 KB page-size runtime on compatible hardware; static checks are not runtime proof.
6. Complete dependency advisory triage and a control-by-control OWASP MASVS assessment. No independent certification is claimed.

## Product limitations
- Reader Mode only works when GeckoView reports readable, non-gated content.
- QR scanning requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume/retry/queue scheduling are not implemented.
- GeckoView WebExtensions engine capability exists, but Rivo has no end-user add-on manager/marketplace; extension support is not claimed.
- Developer Mode is read-only.
- Downloaded file bytes are not encrypted by the metadata cipher.
- Private browsing does not erase user-saved normal downloads or hide network activity.

## Release status
**Not production-release-ready.** Main-head automated CI is green, including API 35 instrumentation and static 16 KB checks. Production signing, signed release publication, physical-device QA, low-RAM profiling, runtime 16 KB validation and full security review remain outstanding.


## Repository governance checkpoint
- GitHub branch metadata for `main` currently reports `protected: false` and `protection.enabled: false`; required status checks are not enforced by branch protection.
- This session's GitHub integration supports repository/file/Actions reads and file/commit writes, but does not expose the branch-protection/ruleset mutation endpoint. Therefore protection could not be enabled from this session.
- Before production release, configure a repository ruleset or branch protection requiring pull requests, at least one approval, dismissal of stale approvals, required Android CI checks, and restricted force-push/deletion. Keep release permissions limited to maintainers.
- The latest documentation push triggers Main CI run #481; the earlier source-validation run #480 passed. Re-check #481 before treating the documentation head as fully CI-validated.

## Website notification privacy hardening (review branch)
- Implemented on `security/redact-web-notifications-lockscreen-2026-11-10`: native Gecko website notifications set `VISIBILITY_PRIVATE` and attach a generic public version containing only Rivo-owned text.
- Added `WebNotificationPrivacyPolicyTest` for the redacted public content.
- This is not yet part of Main; physical-device lock-screen and screen-sharing tests remain outstanding.
