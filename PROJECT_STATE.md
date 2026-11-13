# Rivo Browser — Project State

Updated: 2026-11-13

## Source of truth
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Canonical branch: `main`
- Latest verified Main SHA at this checkpoint: `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af` (Android CI #513 succeeded; see authoritative Nov 13 checkpoint below).
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
- Main Android CI run [#42765445314](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42765445314) completed successfully on `45611c3a42ae14f82089f8ba49c5840a9548a0b6`.
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
- Main-head Android CI #485 completed successfully; branch-protection remains disabled and requires repository-admin configuration.


## Current PR #43 — lazy restored background tabs
- Verified startup issue: restoring saved tabs called `createTab()` for each record, and every call immediately navigated its URL, including inactive background tabs.
- PR branch defers initial navigation for restored tabs: only the saved selected tab loads at startup; other restored tabs load when first selected. User-created tabs retain immediate navigation.
- Added JVM tests for pending/selected load decisions. Android CI run [#42771971849](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42771971849) (run #496) completed successfully, including build, lint, JVM tests, Gecko policy tests, benchmark builds, minified release bundle, static 16 KB checks and API 35 instrumentation.
- This change is on PR #43 and is not yet merged into Main. Physical 3–4 GB device profiling remains required to quantify memory benefit.


## 2026-11-11 integration update
- PR #43 merged into Main as commit `7c24dd337ed96f0d8bbc70831a9e88fa8bf6ad99`; restored background tabs now defer initial navigation until first selection.
- Integrated website notification lock-screen privacy mitigation: private notification visibility plus a generic public version; JVM regression test included.
- Added `SECURITY_ASSESSMENT.md`, a preliminary source-based MASVS gap register. It explicitly records partial, missing and not-assessed controls; no certification or independent audit is claimed.
- Automated CI on the original PR #45 head and source-based MASVS documentation branch passed their recorded CI runs; the consolidated integration branch must pass fresh CI before merge.
- Remaining blockers: production signing secrets, physical Android device access, accessibility/device QA, 3–4 GB RAM profiling, runtime 16 KB verification, complete MASTG/data-flow review and repository branch protection.


## Authoritative checkpoint — 2026-11-11 13:09 UTC

- Latest `main` commit: `b50350a241908fb1f38d2920b7a358451cb436f7` (PR #46 squash-merged).
- PR #46 consolidated website-notification public-preview redaction, `WebNotificationPrivacyPolicyTest`, the preliminary `SECURITY_ASSESSMENT.md` register and release-readiness documentation. PR: https://github.com/techwithrakibofficial/Rivo-Browser/pull/46.
- All four PR-head workflows passed on `10add6d2753b3e4b1740b332e22b1c3723a42000`: Android CI #43024973140, OSV Scanner PR #43024973788, Gradle Dependency Graph #43024973190 and Source SBOM #43024973177.
- Android CI passed shipping assemble, lint, app unit tests, Gecko policy unit tests, benchmark builds, static 16 KB alignment and API 35 Android security instrumentation. Uploaded QA artifacts include ARM64 debug APK (artifact `13857117441`), ARMv7 debug APK (`13857660007`), unsigned QA AAB (`13857067490`), test reports and benchmark artifacts. These are temporary unsigned QA artifacts, not a signed public release.
- Post-merge Main workflows on `b50350a241908fb1f38d2920b7a358451cb436f7` were still in progress at this checkpoint; recheck Actions before treating Main CI as green.
- PR #43 is merged in `7c24dd337ed96f0d8bbc70831a9e88fa8bf6ad99`; lazy background-tab restoration is in Main.
- Release remains blocked by unavailable production signing secrets, physical-device/accessibility QA, low-RAM profiling, runtime 16 KB verification, full MASTG/data-flow review and disabled branch protection. `SECURITY_ASSESSMENT.md` is preliminary source analysis, not certification.

> This checkpoint supersedes earlier dated statements in this file that PR #43 was unmerged or that the consolidated security branch was awaiting merge.


## Authoritative checkpoint — 2026-11-13

- Latest Main SHA verified from GitHub branch metadata: `5845e1b7804ad2cb102af16acd58af4b77aec3cb`.
- The intervening Main commits after PR #46 are documentation synchronization commits; the latest Main tree still contains the PR #46 integration and previously integrated browser features.
- Main Android CI run #510 completed successfully on this exact SHA: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43026650020.
- Same-SHA supply-chain workflows succeeded: OSV Scanner Scheduled #31 (https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43026650746), Gradle Dependency Graph #149 (https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43026649998), and Source SBOM #195 (https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43026650015).
- Repository PR search returned no open PRs at this checkpoint. Earlier stale PR status text is historical only.
- Main branch metadata reports `protected=false`; repository rulesets query returned an empty list. Branch-protection details endpoint returned 403 to this integration, so no protection mutation or complete detailed policy inspection is claimed.
- Signed-release workflow is present. It requires four protected Actions secrets: `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, and `RIVO_UPLOAD_KEY_PASSWORD`. This session cannot read or configure repository secrets. Do not create a production release until an authorized maintainer configures them and verifies the signed artifacts.
- Remaining non-CI gates: physical-device functional and accessibility QA, 3–4 GB profiling, runtime 16 KB verification, full MASTG/data-flow review, dependency advisory triage, branch protection and signed release verification.
- No physical Android device or production signing material is available in this session; those gates remain open rather than being marked complete.


## Authoritative Serial 01 completion checkpoint — 2026-11-13 18:02 UTC

- Main SHA at PR #51 merge: `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- PR #50 merged: https://github.com/techwithrakibofficial/Rivo-Browser/pull/50 — `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- PR #51 merged: https://github.com/techwithrakibofficial/Rivo-Browser/pull/51 — `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- PR #51 final head `3c43a36dab28c72b8344491131a74bf51de6ae38`: Android CI run 43476946517 `success`, including shipping/minified build, lint, JVM/Gecko tests, benchmark builds, static 16 KB alignment, QA artifact uploads and API 35 security instrumentation.
- PR #51 OSV workflow run 43476947044: introduced-dependency and resolved-lockfile jobs `success`; five lockfiles / 814 package entries, `No issues found`, lockfile drift clean, artifact uploaded. Dependency Graph 43476946531 and Source SBOM 43476946667 `success`.
- Separate `osv-scanner` check 132063311666 was `neutral` due the scheduled scanner configuration-not-found notice; it is not counted as success.
- Main post-PR #51 checks on merge SHA: OSV Scanner run 43478503114 `success`, Source SBOM 43478502714 `success`; Android CI 43478502506 and Dependency Graph 43478502631 were still running at this checkpoint.
- Production Environment required reviewers/branch restrictions and all four signing values as `production` Environment secrets (not repository-level secrets) remain unverified/outstanding. No production signing execution or public release is claimed.
- Main branch remains unprotected and repository rulesets query is empty. The connected GitHub integration cannot mutate branch protection or production Environment configuration.
- Gradle dependency verification remains unconfigured: no `gradle/verification-metadata.xml` or verification keyring exists. Lockfiles and OSV scanning do not provide artifact checksum/signature verification.


## Serial 01 post-merge verification — 2026-11-13 18:21 UTC

- Current Main SHA at verification: `528a914db56c75c19ca1c4e03d919ee42b355580`.
- PR #50 is merged: `d93f598464a48351ca0440ad2cfeaa768e55d3f9`. PR #51 is merged: `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Main commit check-runs on `528a914`: Android `build`, OSV `Scan known dependency vulnerabilities / osv-scan`, `dependency-graph`, and `Generate source SBOM` all completed with conclusion `success`. These are the four reported check-runs; no pending, failed, neutral or skipped conclusion was present in this check-run set.
- Main branch metadata reports `protected=false`; branch protection is disabled. The available GitHub integration cannot configure branch rules or Environment protection.
- No GitHub Releases exist at verification time. The signed-release workflow is present, but no production signing run, signed APK/AAB, checksum release assets, or published release is evidenced.
- Production Environment reviewer policy, deployment ref restrictions, and presence of all four secrets specifically in the `production` Environment could not be verified with the available integration. Owner/admin must configure and verify these controls before production signing.
- Gradle artifact dependency verification remains unconfigured: `gradle/verification-metadata.xml` and a verification keyring are absent. Lockfiles and OSV scans are separate controls.
- This checkpoint updates CI state only; it does not close physical-device QA, accessibility, 3–4 GB performance profiling, runtime 16 KB validation, or the full MASVS/MASTG review.


## Latest Main update — 2026-11-13 20:45 UTC

- Main is at `2082f9ed46ec5357d22a81540030fb69bb8d59a7` (confirmed identical to `main`).
- Latest application change fixes an Android instrumentation-test DataStore fixture shutdown race in `LocalDataCipherInstrumentedTest`: teardown now cancels and joins each fixture coroutine scope before deleting/reopening its preferences file. This is test reliability hardening; it does not alter production browser behavior.
- The commit is present on Main. A successful post-commit CI result was not available from the connected GitHub checks interface at this checkpoint; do not claim this commit passed CI until its Actions run is verified.
- Existing release blockers remain unchanged: protected production signing secrets and Environment approval, signed APK/AAB publication, physical-device functional/privacy and accessibility QA, 3–4 GB profiling, runtime 16 KB validation, Gradle artifact verification metadata, full MASTG/data-flow review, and Main branch protection.
- No device acceptance or production release is claimed by this checkpoint.
