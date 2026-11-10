# Rivo Browser — Project State

Updated: 2026-11-10

## Source of truth
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Canonical branch: `main`
- Current Main HEAD: `a40a64460f51c6c4652e133d35e99da5a89d44d4`
- Phase 2 integration: PR #38 merged; PR #37 was also recorded as merged through its included integration commit.
- Reconciliation commit: `2a33c1157c32c29f58e2ccb079e6d3dbcb07bf0c`
- Combined source tree: `9115b9d3c607353facc15ed9b6fb4e3e53ce696d`

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

## Verification evidence
- Combined source tree `9115b9d...` passed Android CI run #463 (PR #37 source head): shipping assemble, lint, app JVM tests, Gecko policy tests, benchmark debug/release builds, minified shipping AAB, static 16 KB alignment checks, QA artifact uploads and API 35 security instrumentation.
- On PR #38 exact reconciliation head, Source SBOM run #151, Gradle Dependency Graph run #105 and OSV Scanner PR run #147 completed successfully. Android CI run #? was still running at merge time; verify Main push CI before treating the merge commit as independently validated.
- Main push CI after merge: Android CI run https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42760626579; dependency graph run https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42760626529; source SBOM run https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42760626525; OSV run https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42760626964. Their final conclusions must be recorded after completion.
- Automated source/build evidence is not physical-device acceptance.

## Outstanding release gates
1. Confirm post-merge Main Android CI and security instrumentation on the actual Main merge SHA.
2. Physical-device QA: reader extraction, Play-services QR scanning, transfer cancellation/removal, website permission grant/deny/revoke, notification delivery/click, private-session isolation, process death/restoration.
3. Accessibility QA: TalkBack, 320 dp/narrow screens, large font, contrast and all three appearance modes.
4. Profile 3–4 GB RAM devices; compare GeckoView and WebView under the same repeatable workload.
5. Verify 16 KB page-size runtime on compatible hardware; static APK/ELF checks are not runtime proof.
6. Complete dependency advisory triage and a control-by-control OWASP MASVS assessment. No independent certification is claimed.
7. Configure production signing securely and complete release review. CI AAB/APK artifacts are unsigned QA outputs.

## Product limitations
- Reader Mode only works when GeckoView reports readable, non-gated content.
- QR scanning requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume/retry/queue scheduling are not implemented.
- GeckoView WebExtensions engine capability exists, but Rivo has no end-user add-on manager/marketplace; extension support is not claimed.
- Developer Mode is read-only.
- Downloaded file bytes are not encrypted by the metadata cipher.
- Private browsing does not erase user-saved normal downloads or hide network activity.

## Release status
**Not release-ready.** Core and Phase 2 source features are integrated, but post-merge CI, device QA, low-RAM profiling, runtime 16 KB validation, full security review and production signing remain open.
