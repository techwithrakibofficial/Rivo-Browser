# Project State

Updated: 2026-11-09 (current integration divergence audit)

## Canonical repository state
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: `main`
- Main HEAD after PR #31 documentation merge: `159643bb358c5bf9e92d537acfe9ba5ee1c39f00` (latest application code merge: PR #27 `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`)
- Latest commit: `docs: refresh current Main integration audit`
- The earlier checkpoint `84303588c2560db55bfb7ae2081d8f714be532e7` is no longer current.
- PR #23 head passed CI #293; PR #24 head `4e1a404b50b96d17388fd6839068f9622fb02804` passed CI run [#300](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42561554513), including assembleDebug, lintDebug, JVM unit tests, minified bundleRelease, size reports and QA artifact uploads. PR #24 merged as `350d4f3...`; PR #27 adds two Tab Universe search/filter regression tests and merged as `ad607a7...` after CI #306 passed all configured steps. No push-triggered workflow result for the #27 merge SHA has been returned.

## Latest integration audit (2026-11-09)
- PR #10: current comparison is diverged, 10 commits behind Main, mergeable=false. Its permission changes overlap current `BrowserController.kt`, `BrowserScreen.kt` and Gecko permission code; owner was asked to rebase and rerun CI.
- PR #20: current comparison is diverged, 8 commits behind Main, mergeable=false. It overlaps controller, storage/cipher and build/CI configuration; owner was asked to coordinate conflict resolution and rerun API 35/16 KB checks.
- PR #21 remains stacked on PR #10; rebase only after #10 integration and rerun instrumentation.

## Open pull requests observed
- PR #24 — Tab Universe accessibility/narrow-layout follow-up; merged to Main as `350d4f3...` after CI #300 passed.
- PR #10 — RIVO-06 privacy and per-site permission controls. Open, based on old Main checkpoint `84303588...`; 11 commits behind current Main, connector reports mergeable=false. CI #268 passed all configured steps on its recorded head: assembleDebug, lintDebug, JVM tests, minified release bundle, size reports and QA APK/AAB uploads. No instrumentation or physical-device permission validation was included. Device validation is explicitly outstanding.
- PR #20 — RIVO-04/05/07/09 hardening, instrumentation, engine benchmark and 16 KB validation. Open, based on older Main `72700fa6...`; 9 commits behind current Main and connector reports mergeable=false. PR head `71af60e...` passed all configured CI #288 steps, including shipping assemble/lint/JVM tests, GeckoView and WebView benchmark APK/AAB builds, minified release bundle, static 16 KB alignment checks, artifact uploads and API 35 Android security instrumentation. It does not test integration with current Main or prove physical-device/16 KB runtime behavior.
- PR #21 — RIVO-11 privacy center and private-download hardening. Open and stacked on PR #10; no workflow run returned for its current head. Rebase after #10 integration, then run fresh CI.
- PR #17's preserved Advanced Tab Universe work was superseded by PR #23 and is now integrated in Main at `d192baf0...`. PR #16 benchmark remains closed/unmerged and is not integrated.
- Branch/PR status is a snapshot from this audit and may change.

## Implemented in Main source (not equivalent to device-verified)
- Tab Universe policy regression tests: trimmed search combined with Normal/Private filter and empty search-result behavior; PR #27 CI #306 passed all configured build, lint, JVM test, minified release bundle, size-report and QA artifact steps before merge.
- Process-scoped GeckoRuntime and per-tab GeckoSession architecture.
- Navigation, URL/search omnibox, back/forward/reload/stop and Gecko-reported connection security.
- Up to eight tabs; native Gecko private sessions; private tabs excluded from app-side history/bookmarks/restoration.
- Advanced Tab Universe: case-insensitive title/URL search, All/Normal/Private/Active filters, Active-first/Title/Type sorting, semantic Material 3 filter chips, horizontally scrollable filter row, persistent search label, active/inactive TalkBack state and title-specific close action; the eight-tab cap is unchanged.
- Per-session tracking protection.
- Local DataStore bookmarks (max 500), history (max 300), restorable tabs and download metadata; sensitive metadata encrypted with Android Keystore AES-256-GCM.
- Confirmation-gated HTTP(S) downloads, app-specific storage, temporary-file streaming, 250 MB cap, atomic finalization and scoped FileProvider open/share.
- Dark, Light and Premium Glass palettes; secondary-menu Quick Appearance Picker.
- Regular HTTP(S) tab persistence/restoration and selected-tab index fix.
- Security fail-closed metadata write handling, malformed-record rejection, IDN normalization and relevant JVM regression tests.
- UI panel adaptive-height source change appears in latest Main commit; visual/device acceptance remains unverified.

## Work in open PRs / not yet integrated

- Per-site GeckoView permission mediation, permission inventory/revocation and notification handling: PR #10.
- Consolidated Keystore/DataStore hardening, Android API 35 instrumentation, GeckoView/WebView benchmark harness, corrected 16 KB validation and opt-in signing: PR #20.
- Privacy Intelligence Center, fail-closed missing-key decryption, private-download metadata exclusion, download validation and Keystore instrumentation: PR #21 (stacked on #10).

## Known release blockers
1. Verify push-triggered Main CI for application merge `ad607a7...` when available; PR-head CI #306 passed all configured jobs. The latest Main HEAD is docs-only merge `693ec61...`.
2. PR #10 is 11 commits behind current Main and reports mergeable=false; resolve/rebase before integration.
3. PR #20 is 9 commits behind current Main and reports mergeable=false; reconcile conflicts, rerun CI on the rebased head and review API 35 instrumentation/16 KB static/runtime evidence.
4. PR #21 depends on PR #10 and needs rebase plus fresh CI.
5. Recover/review PR #16 benchmark work only if it remains useful; Advanced Tab Universe is now integrated through PR #23.
6. Keystore round-trip/tampering/key-loss, DataStore corruption/relaunch and process-death restoration require executed instrumentation/device evidence.
7. No physical 3–4 GB device benchmark or 16 KB Android runtime test is claimed.
8. Dependency/SBOM review and OWASP MASVS assessment are incomplete; no MASVS certification is claimed.
9. TalkBack/manual accessibility and narrow-screen/large-font regression evidence is incomplete despite source semantics improvements; no physical-device evidence is available.
10. Production signing and release are not complete. Unsigned CI AABs are QA artifacts only.
11. Transfer Studio, Reader Mode, QR scanning and verified extension workflows are not confirmed implemented in Main.

## Current engineering phase
Phase 1 — Functional Browser MVP stabilization and verification. Do not declare Phase 1 accepted until the above automated and manual/device gates have evidence. Phase 2 features should proceed only after integration dependencies and release blockers are tracked.

## Exact next steps
1. Verify post-merge Main CI for `350d4f3...` if/when triggered; PR #24 CI #300 passed on the PR head.
2. Review PR #20 files and fresh CI; resolve all failures before considering merge.
3. Rebase PR #10 onto current Main, resolve conflicts, run full CI and validate permission flows.
4. Rebase PR #21 onto the integrated permission implementation; run CI including API 35 instrumentation.
5. Recover PR #16 benchmark branch changes if still useful; PR #17 has been superseded by merged PR #23.
6. Complete device QA: process death, private isolation, permission grant/revoke, notifications, download safety, TalkBack, narrow widths, large font, 3–4 GB memory/performance and 16 KB runtime.
7. Complete dependency/SBOM and MASVS source-level assessment.
8. Implement remaining Phase 2 features (Transfer Studio, Reader Mode, QR scanning) only with verified engine capability and tests.
9. Prepare production signing/release only after all gates pass and release credentials are securely configured.

## Verification honesty
- A merged PR, passing JVM test, successful build, emulator instrumentation run and physical-device test are different evidence levels.
- Never claim unexecuted tests, hardware-backed Keystore, full tracker blocking, anonymity, extension support or production readiness.
- PR #23 and PR #24 were merged after their respective PR-head CI runs #293 and #300 passed. Post-merge Main CI and all physical-device/UI validation remain separate evidence gates.


## Account A core-feature continuation — 2026-11-09

- Latest inspected Main base: `0d77826e655267c671498334c11ffad8d1dd5b14`. PR #33 core security integration and PR #34 supply-chain workflow integration are merged into Main.
- Account A work is isolated on `worker/account-a-core-features-2026-11-09`; PR #35 is open: https://github.com/techwithrakibofficial/Rivo-Browser/pull/35.
- PR #35 includes native GeckoView Reader Mode (readability and gated-content checks, 120,000-character cap), QR-only Google Code Scanner (no CAMERA permission, confirmation before navigation), Transfer Studio (recent-100 list, filters, progress from response bytes, best-effort cancel, Open/Share/Remove), and read-only Developer Mode diagnostics.
- Gecko remote debugging and console output are explicitly disabled. WebExtensions engine capability is documented, but Rivo has no extension manager/marketplace; no end-user extension support is claimed.
- New JVM coverage: QR sanitation, transfer filtering/cancellation classification and download progress monotonicity.
- Fresh Android CI, OSV and source-SBOM workflows must be reviewed on the final PR head. The SBOM artifact is source-level and does not represent a complete resolved Gradle inventory; OSV warns that no lockfiles are found.
- PR #10 (site permissions) and PR #21 (Privacy Intelligence Center) remain open and separate; their integration may require conflict resolution in BrowserController.
- Device QA remains outstanding: reader extraction, Play-services QR scan, transfer cancellation/file deletion, TalkBack, narrow width, large font, theme matrix and low-RAM profiling.

## Account B security integration candidate — 2026-11-09

- Main baseline at branch creation: `0d77826e655267c671498334c11ffad8d1dd5b14`.
- Worker branch: `worker/account-b-security-qa-2026-11-09`.
- PR #36 is open as a draft; do not merge until current-head CI and review pass.
- Integrated candidate changes: GeckoView HTTPS site permission mediation, default-deny policy, normal permission inventory/reset, notification delegate/receiver, private download metadata suppression, URL and restored-file checks, one-time migration of legacy ambiguous download metadata, and regression tests.
- Main's existing Keystore/DataStore hardening, API 35 Keystore instrumentation, atomic download writer, Account A Tab Universe work and static 16 KB checks are preserved.
- The previous candidate CI run was cancelled due to rapid commits; current final SHA requires a fresh complete run.
- Device validation, 16 KB runtime validation, full transitive dependency inventory, MASVS independent assessment and production signing remain open.


## Account B security review update — 2026-11-09

- Reconfirmed Main base at branch creation: 0d77826e655267c671498334c11ffad8d1dd5b14. PR #36 is based directly on Main and is six commits ahead, zero behind at the time of the comparison.
- Open PR #35 is Account A core feature work; PR #36 is Account B security/privacy integration. Both remain unmerged and require their own current-head CI.
- Historical PR #10 and #21 remain open on older/stacked branches and are not merge candidates; their relevant changes are being reviewed/transplanted through PR #36.
- PR #33 and #34 are merged into Main. Their API 35 instrumentation, static 16 KB checks, OSV scan and source SBOM workflows are part of the current baseline.
- Account B found and fixed the new-download-write/legacy-download-migration ordering defect, added private/insecure-origin notification suppression, added notification policy unit tests and expanded CI to run engine Gecko unit tests.
- Added Gradle dependency graph generation for PR artifact review and GitHub Dependency Graph submission on Main pushes/manual runs. This is improved resolved transitive dependency visibility, not lockfile/checksum verification.
- Source-level MASVS assessment and residual risks are recorded in SECURITY.md. This is not certification or independent penetration testing.
- Current PR #36 CI is pending/retriggered after the latest code and docs changes. No current-head green CI is claimed yet.
- Release remains NO-GO pending current-head CI, device QA, 16 KB runtime, dependency verification/lockfiles, MASVS assessment completion and production signing.


## Account B final checkpoint — 2026-11-09

- PR #36 current branch: worker/account-b-security-qa-2026-11-09; latest source/documentation commit 93b035aba55cb47f1f32550e4996b5508a559b3a; based on Main 0d77826e655267c671498334c11ffad8d1dd5b14; 42 commits ahead, 0 behind at the latest comparison.
- PR #36 is open and draft. It contains the current Main-based integration of PR #10 site permissions and PR #21 privacy/download security work. Main remains untouched.
- Added notification privacy policy and unit tests; full-origin permission prompt display; atomic download-metadata migration marker fix; API 35 migration regression; and private-tab process-restoration snapshot regression.
- PR #36 OSV scan succeeded but explicitly found no package sources/lockfiles. Do not call this a clean dependency scan.
- PR #36 source SBOM run #42571406272 succeeded and uploaded a 2,941-byte SPDX source SBOM. It is not a resolved transitive dependency SBOM.
- PR #36 Gradle dependency graph run #42571406249 succeeded and uploaded a 10,829-byte resolved dependency graph artifact. GitHub Dependency Graph is disabled in repository settings; Dependency Review workflow was removed after it failed for this setting. Owner must enable the setting before advisory review/submission can be restored.
- PR #36 Android CI run #42571406314 first failed because Maven redirected Kotlin compiler artifact download returned HTTP 504. A retry was interrupted by subsequent code/test updates; current exact-head CI must be checked again.
- PR #33 Main baseline CI #42563889346 passed all configured tasks including Android lint/JVM tests, benchmark APK/AAB packaging, minified AAB, static APK/ELF 16 KB alignment and API 35 security instrumentation. This is not physical-device or 16 KB runtime evidence.
- PR #35 Account A and PR #36 both touch BrowserController/BrowserScreen/GeckoBrowserTab. If #35 merges first, rebase #36 onto Main and manually preserve both changes before merge.
- Release remains NO-GO: no device QA, 16 KB runtime, dependency lockfiles/verification metadata, full advisory scan, independent MASVS assessment or production signing.


## Account B verification refresh — 2026-11-09

- PR #36 latest head: 8d0cdfc84269e0dd01b7a744758cdd742dfe5b26; 45 commits ahead / 0 behind Main; draft/open. Main was not modified.
- Exact-head source SBOM run #42571935161 succeeded; artifact rivo-source-sbom, 2,939 bytes. This is source-level only.
- Exact-head Gradle dependency graph run #42571935225 succeeded; artifact 10,834 bytes. GitHub Dependency Graph is disabled in repository settings; Dependency Review was attempted, failed on that repository setting and was removed. The workflow now produces read-only artifacts only.
- Exact-head OSV run #42571935564 succeeded but logs state no package sources and no lockfiles. It does not establish a vulnerability-free dependency inventory.
- Exact-head Android CI run #42571935131 was cancelled during retry after earlier failures caused by upstream HTTP 504 fetching Kotlin compiler artifacts; no app build/test pass is claimed for PR #36 yet.
- Added private tab process-restoration snapshot regression to RestorableTabPolicyTest; DataStore instrumentation suite covers store reopen, corruption and metadata migration.
- PR #33 Main baseline CI #42563889346 passed all configured tasks including API 35 instrumentation and static APK/native ELF 16 KB alignment. No 16 KB runtime or physical-device claim.
- PR #35 and #36 share BrowserController/BrowserScreen/GeckoBrowserTab. If PR #35 merges first, rebase and resolve PR #36 without overwriting Account A code.
- Release remains NO-GO: current-head Android CI, device QA, 16 KB runtime, full resolved dependency advisory scan, lock/verification metadata, independent MASVS review and production signing remain open.


## Final Account B CI checkpoint — 2026-11-09

- PR #36 latest code head: 9113b9a0a48b6d3aa6cae6fd8fb192d0fddb41f8; 51 commits ahead / 0 behind Main; draft/open.
- Exact-head OSV run #42572673279 completed successfully but reports no package sources/lockfiles; this is not a clean dependency scan.
- Exact-head source SBOM run #42572672745 succeeded after retry using Syft v1.55.0; artifact size 2,937 bytes, source-level only.
- Exact-head Gradle dependency graph run #42572672769 succeeded; artifact size 10,819 bytes. GitHub Dependency Graph is disabled in repository settings; no automated Dependency Review is available.
- Exact-head Android CI #42572672741 remained in progress at Assemble shipping Rivo Browser at this checkpoint. Earlier attempts failed/cancelled during upstream HTTP 504 downloads; do not claim PR #36 build/tests passed until this run completes.
- PR #33 Main baseline CI #42563889346 passed configured lint/JVM/instrumentation, benchmark packaging, minified AAB and static 16 KB APK/ELF alignment. This is not device runtime verification.
- Current code includes private-tab restore snapshot regression and metadata migration instrumentation coverage. Device QA, 16 KB runtime, dependency locking/full OSV scan, independent MASVS review and production signing remain open.


## PR #36 current candidate checkpoint — 2026-11-09

- Canonical source remains `main`; all work is committed to `worker/account-b-security-qa-2026-11-09`. Main has not been modified or merged by this work.
- PR #36 is open and Draft at the time of this checkpoint. Current candidate head is shown by the PR; this document must not be used instead of exact-head GitHub checks.
- Candidate implementation covers GeckoView secure-origin site permissions (location/camera/microphone/notifications), encrypted Android app-level permission denials, normal site-permission inventory/reset, tracking-protection status, confirmation-gated Clear Browsing Data, private download file cleanup, HTTPS-only download policy and MIME normalization.
- Five Gradle lockfiles are committed for app, engine-api, engine-gecko, engine-webview and engine-benchmark. OSV #42578249254 passed with no issues across 788 package instances; Source SBOM #42578248119 uploaded SPDX with 809 packages; Gradle Dependency Graph #42578248161 succeeded. GitHub Dependency Graph submission remains disabled in repository settings.
- Android CI code-equivalent run #42576548217 passed assembleDebug, lint, app JVM tests, Gecko policy tests, benchmark APK/AAB builds, minified shipping AAB, static 16 KB alignment and QA artifact uploads. Instrumentation was cancelled while the emulator runner was being reconfigured; a fresh API 35 emulator run is required on the final documentation head.
- PR #35 is still open and separate. Its changes overlap AndroidManifest.xml, BrowserController.kt, BrowserScreen.kt and GeckoBrowserTab.kt. No PR #35 changes were overwritten or cherry-picked. If #35 merges first, rebase #36 and manually preserve both implementations.
- No physical-device validation, 16 KB runtime verification, independent MASVS certification or production signing is claimed. Production release remains NO-GO until those separate gates pass.
- Next exact steps: complete the fresh Android CI/instrumentation run on the final docs commit; fix any failures; recheck mergeability; then mark PR #36 Ready for Review. Do not merge Main.
