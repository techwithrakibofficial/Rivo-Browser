## 2026-11-24 — Release version alignment candidate

- Verified Main before this change: `b5e909e159896bc4beef49a9c3e3cb3d63a69b83` (PR #88 merge; fingerprint parser fix is present).
- Verified app metadata on Main: `versionName=0.2.1-phase1`, `versionCode=3`.
- Existing immutable tag `v0.2.2-phase1` points to `e15ee3ce1c623f70679b9c117018d98de0b1b757`, before PR #88; the tag's workflow still has the old fingerprint parser. Do not dispatch or move this tag for production signing.
- Proposed next release candidate in this PR: `v0.2.3-phase1`, app `versionName=0.2.3-phase1`, `versionCode=4`. The tag must be created only after this PR is merged and CI passes.
- Manual dispatch from Main with input `v0.2.3-phase1` checks out that tag's source; the release workflow also verifies the selected commit is reachable from Main.
- No production-signed APK/AAB or GitHub Release is verified. Signing secrets and environment protection are not inspectable here.

# Rivo Browser — Project State

## 2026-11-24 — Follow-up audit after PR #83
- PR #83 documentation reconciliation was approved by `techsomadhanmedia` and merged at 13:01:36Z. Main at the start of this follow-up is `2126dd3c692da6d85553015d2064a414df90e207`.
- PR #84 exposed a release-workflow defect: the AAB build runs `gradle clean`, so the APK build directory no longer exists when the later verification step searches it. The identical fix passed all five PR CI workflows and received CODEOWNER approval on PR #84; PR #84 was closed as superseded because it was behind Main.
- Replacement PR #85 is based on current Main and changes APK verification to inspect the preserved `dist/*.apk` files. Its five CI workflows and fresh CODEOWNER review are required before merge.
- No signed APK/AAB or GitHub Release is verified. The next release gate is a fresh `workflow_dispatch` from current Main with tag input `v0.2.1-phase1`, followed by signature, signer-fingerprint, R8 mapping and SHA-256 verification.


## 2026-11-24 — Current verified state after PR #82
- Canonical `main` SHA: `5ac3a6853cd20df47813acc580fcae2dbb34cbe9`.
- PR #82 merged at 12:34:24Z. It fixes the signed-release AGP task collision by building ABI-split APKs with `rivo.abiSplits=true`, preserving them, cleaning intermediates, then building the AAB separately with `rivo.abiSplits=false`.
- PR #82 head validation passed Android CI, OSV Scanner PR, Gradle Dependency Graph, Source SBOM and Gradle dependency-verification bootstrap.
- Post-merge Main checks at documentation time: OSV Scanner Scheduled, Source SBOM and Gradle Dependency Graph succeeded; Android CI and Gradle Dependency Verification Bootstrap were still in progress.
- Signed-release run #45385076282 failed on the pre-fix workflow at `:app:buildReleasePreBundle`; no signed artifacts or GitHub Release were verified. A fresh workflow dispatch using the corrected current-main workflow is still required.
- Existing tag `v0.2.1-phase1` is not to be moved or rewritten. Use it as the workflow-dispatch input from current `main`.


Updated: 2026-11-24

## 2026-11-23 — Release CI reconciliation (PR #64 candidate)
- Live audit confirmed PR #64 and PR #65 target the same Main SHA (cfa94c3f26e8e478bb51adf1b3b60fc6ea871006) and overlap in the signed-release workflow and dependency verification metadata. PR #64 is the preferred candidate because it additionally verifies APK/AAB signer fingerprint consistency and exact AAB output cardinality.
- PR #64 dependency bootstrap run #45171080773 passed: generated SHA-256 metadata across shipping/test configurations, strict verification, and validated lockfile/metadata artifacts. The generated files already matched the PR branch; no redundant commit was created.
- Release workflow candidate now retains the R8 mapping artifact for 90 days, explicitly requires jarsigner to report a verified AAB, and downloads published release assets to re-check their SHA-256 checksums.
- PR checks remain pending owner approval for some workflows; this candidate is not merged and no production-signed build has run. Production Environment secrets remain owner-managed.
- Active Main ruleset Protect main branch currently requires a status check named b, which is absent from Main's observed check-run names; correct the required context to actual CI check names before merge. Do not bypass the ruleset.


## Source of truth
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Canonical branch: `main`
- Latest verified Main SHA at this checkpoint: `cfa94c3f26e8e478bb51adf1b3b60fc6ea871006` (Main Android CI run #44038863782 succeeded).
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


## Authoritative Main verification — 2026-11-14

- Main SHA: `54febd57265ccc165740d9ebf42198270a92346b` (PR #54 squash merge commit).
- PR #54 (documentation synchronization) is merged; merge timestamp: 2026-11-13 21:03:14 UTC; merge commit: `54febd57265ccc165740d9ebf42198270a92346b`.
- All four post-merge workflows on this exact Main SHA completed successfully: Android CI #544 (run ID `43507177835`), Gradle Dependency Graph #183 (run ID `43507177822`), OSV Scanner Scheduled #38 (run ID `43507178272`), and Source SBOM #229 (run ID `43507177919`).
- GitHub PR search returned no open pull requests at this verification.
- No GitHub Releases are published (releases API returned an empty list). No signed APK/AAB or public APK download URL is available from this verification.
- These results close the PR #54 documentation/CI synchronization task only. Production signing/secrets and Environment approval, physical-device and accessibility QA, 3–4 GB RAM profiling, runtime 16 KB verification, Gradle artifact dependency verification, full MASVS/MASTG review, and branch protection remain outstanding as described above. Do not represent CI success as production release readiness.



## Authoritative Main CI verification — 2026-11-16

- Verified current Main SHA: `654e98b7c7ec975467b7c2af1781132eed46bb9b`.
- PR #56 (artifact action migration) is merged: https://github.com/techwithrakibofficial/Rivo-Browser/pull/56. Its head SHA `fb38196f644c56560aebee91ddaf965900e0ff49` passed all four PR workflows: Android CI #43839170714, Gradle Dependency Graph #43839170834, OSV Scanner PR #43839171295 and Source SBOM #43839170716.
- PR #56 upgraded upload/download-artifact actions to v7. The migration is present on Main; old upload-artifact v4 / Node 20 deprecation warnings are no longer present in the latest Main Android CI log.
- Latest Main-head workflows on `654e98b7c7ec975467b7c2af1781132eed46bb9b` all completed successfully: Android CI #43845322821, Gradle Dependency Graph #43845322967, Source SBOM #43845322921 and OSV Scanner Scheduled #43845323444. No failed or pending checks were present in this four-workflow set.
- Android CI passed shipping debug assembly, Android lint, app unit tests, Gecko engine policy tests, GeckoView/WebView benchmark debug and release builds, minified shipping release AAB build, static 16 KB APK/native-library alignment, QA artifact uploads and API 35 security instrumentation. Android test reports and QA artifacts were uploaded successfully.
- OSV Scanner Scheduled printed `No issues found`; dependency graph and source SBOM generation succeeded. This is an automated dependency scan result, not a complete security audit.
- Remaining non-blocking upstream/environment warnings: `android-actions/setup-android@v4` invokes `sdkmanager`, which currently emits an Android CLI deprecation warning; hosted runner had no `/dev/kvm`, so emulator software acceleration was possible (instrumentation nevertheless passed); OSV Scanner v2.7.1 emits a deprecated `--output` option warning internally. Emulator/X11/QEMU diagnostic warnings did not fail tests. These are not app compile, lint or test failures; do not hide them or replace the supported Android setup action without a verified migration.
- Release status remains separate from CI: GitHub Releases API returned no published releases. The signed-release workflow requires production Environment secrets `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS` and `RIVO_UPLOAD_KEY_PASSWORD`; secret presence/value and Environment reviewer configuration were not verifiable through this session. No signed APK/AAB is claimed.
- Physical-device functional/privacy QA, accessibility acceptance, 3–4 GB RAM profiling, runtime 16 KB validation, Gradle artifact dependency verification and full MASVS/MASTG review remain release gates. CI success is not production-release readiness.

## 2026-11-23 — GitHub governance/security audit

- Main is `cda27e5bf019fbf50be47678c53bf1dcc2e54d0e`; active ruleset `27894079` targets `refs/heads/main`.
- Verified rules: PR required, `build` required and strict/up-to-date, force-push and deletion blocked, review conversations resolved, no bypass actors.
- Gap: required approval count is zero; stale review dismissal and CODEOWNERS review are disabled.
- Hardening branch `security/governance-hardening-2026-11-23` removes the privileged PR artifact write-back job from dependency verification and prevents the release workflow from overwriting an existing GitHub Release.
- Environment protection, Environment secret names/presence, repository Actions policy and ruleset mutation could not be verified/changed by the connected GitHub integration.
- No production signed release exists. PR CI must pass before merge; then owner-only governance and signing gates remain.
- Full evidence and checklist: [GOVERNANCE_AUDIT.md](GOVERNANCE_AUDIT.md).


## 2026-11-24 authoritative follow-up (supersedes earlier 2026-11-23 snapshot)

- The repository owner reports that all four signing secrets have now been added to the GitHub Actions `production` Environment: `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, and `RIVO_UPLOAD_KEY_PASSWORD`. Secret values were not requested, accessed, or logged. The connected integration cannot inspect Environment secret metadata, so this is owner-reported and not independently API-verified.
- A fresh read of active ruleset `27894079` shows: one approving review required; stale approvals dismissed on new pushes; code-owner review required; `build` required with up-to-date branch; conversation resolution required; deletion and non-fast-forward updates blocked; no bypass actors. This supersedes earlier text in this audit branch saying zero approvals / stale dismissal off / code-owner review off.
- Classic branch-protection API remains inaccessible to this integration; the active ruleset is the effective protection observed. Production Environment reviewer/self-review and deployment-ref settings remain unverified.
- No `CODEOWNERS` file was found at root, `.github/CODEOWNERS`, or `docs/CODEOWNERS` on Main. Do not add a catch-all owner entry until a distinct trusted reviewer/code-owner identity is designated; assigning the PR author as sole code owner would make the review requirement unworkable for this PR.
- PR #66 is open and unmerged. Its five PR-head CI workflows previously completed successfully, but it has no submitted reviews and GitHub reports the merge state as blocked. Do not bypass review or merge protections. A trusted collaborator must approve; if CODEOWNERS is to be enforced for workflow/security files, designate a second trusted GitHub username and add that identity in a follow-up.
- No production-signed APK/AAB or published release is claimed. A real signed-release workflow run and verification of signatures/fingerprints/checksums remain outstanding.


## 2026-11-24 — governance follow-up (verified)
- Active Main ruleset `27894079` now requires one approving review, dismisses stale reviews, and enables code-owner review; required `build` check, up-to-date policy, conversation resolution, deletion/force-push protections and no-bypass state remain active.
- Owner reports all four signing secrets have been added to the `production` Environment. Environment reviewer/deployment restrictions and secret scope are not independently readable through this integration.
- PR #66 (`security/governance-hardening-2026-11-23`) has 5/5 PR CI workflows successful at head `24e36c109192cf16190bf40fe765886c00b9e837`; PR remains open and blocked for independent approval. No production release is claimed.
- No CODEOWNERS file exists. A second trusted maintainer must be mapped as code owner; do not make the PR author the sole owner or bypass review.
- Remaining: establish effective CODEOWNERS, independent PR approval and merge; verify production Environment protection; pin third-party Actions to verified full SHAs; perform protected signed-release execution and verify signed APK/AAB/checksums; physical-device/accessibility/low-RAM/runtime-16KB and full MASVS/MASTG assessment.


## 2026-11-23 — comprehensive Android security source audit

- Audited Main baseline: `0df6760287b0de5c07d2468b247145dae9c0ea4b`.
- Reviewed Android source/configuration for GeckoView, manifest/IPC, network configuration, navigation, downloads, permissions, notifications, encrypted metadata, private browsing and supply-chain CI controls.
- No confirmed fixable high- or medium-severity vulnerability was identified in the reviewed source/configuration paths. This is not a penetration-test result.
- Added deterministic property-style JVM regression coverage for malformed omnibox inputs and download filenames in `SecurityInputPropertyTest.kt`.
- CI status at audit update: Main Android CI run `45240923073` was in progress. OSV Scanner Scheduled, Source SBOM and Gradle Dependency Graph on the same Main SHA had completed successfully. Recheck before closing this task.
- Open release gates: GeckoView runtime TLS/mixed-content tests; merged manifest/IPC; private mode and storage forensic checks; Keystore lifecycle; screenshot/recents; full SDK/data-flow/privacy review; signed release binary inspection; physical-device and independent penetration testing.


### Mixed-content UI security remediation — 2026-11-23

- Follow-up review identified a medium security-presentation issue: browser status UI consumed GeckoView TLS/certificate booleans but ignored GeckoView active/passive mixed-content status.
- Fixed in audit branch: Gecko progress callback now forwards active/passive loaded and unknown states; per-tab state and browser UI disclose mixed content and avoid claiming full security when status is unknown.
- Added `BrowserSecurityStatusPolicyTest.kt` covering all label branches and precedence.
- Awaiting latest PR-head Android CI and independent code-owner review. Runtime GeckoView mixed-content fixture testing remains open.


## 2026-11-24 — PR #67 verification closeout (authoritative latest checkpoint)

- Audited source branch: `security/comprehensive-android-audit-2026-11-23`; verified PR head: `1cde602ae030b44c260770734205b0186deb4e28`; base Main: `0df6760287b0de5c07d2468b247145dae9c0ea4b`.
- GitHub Actions Android CI run [45242372450](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372450) completed successfully. Its build job completed all steps successfully, including strict Gradle dependency verification, static security configuration audit, shipping and benchmark builds, Android lint, JVM tests, Gecko policy tests, minified shipping release bundle, static 16 KB alignment checks, and API 35 emulator Android security instrumentation tests.
- PR-head supply-chain workflows also completed successfully: [Gradle Dependency Verification Bootstrap](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372194), [Source SBOM](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372086), [OSV Scanner](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372048), and [Gradle Dependency Graph](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242371982).
- The Medium mixed-content security-indicator finding is remediated in source and covered by JVM policy tests. CI success verifies the automated test path; GeckoView mixed-content fixture behavior on physical devices remains unverified.
- Correction to earlier preliminary audit wording: a follow-up review DID identify the Medium mixed-content status presentation issue; it is fixed in this PR. Do not interpret earlier “no confirmed Medium issue” wording as the final finding.
- PR #67 remains open and unmerged. The trusted reviewer `techsomadhanmedia` has write permission and is already requested as reviewer, but the PR currently has no submitted review/approval. Do not self-approve, bypass required review, or merge before independent approval and required checks.
- Scope boundary: successful emulator instrumentation is verified; physical-device MASTG/runtime tests, merged release manifest/IPC inspection, exact signed APK/AAB inspection, production signing/release, and independent penetration testing are NOT completed. This is not MASVS certification.
- Manual next gate: `techsomadhanmedia` must open PR #67, inspect the changes, and submit **Approve**. After approval, recheck all required checks and mergeability before merging. Physical-device checks must then be executed using the documented matrix before production security acceptance.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`.
- PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC; merge commit is the current Main SHA above.
- PR #66 governance hardening is merged. Active ruleset `Protect main branch` applies to `main`, requires a pull request, at least one approving review, CODEOWNERS review, resolved review threads and strict required check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- PR #64 signed-release workflow hardening is merged. PR #65 is closed unmerged. Live open-PR listing was empty at this checkpoint.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue by propagating GeckoView active/passive mixed-mode state into tab/UI status, qualifying unknown status, resetting status on navigation, and adding JVM regression/property tests. Runtime engine behavior still needs device/fixture verification.
- Post-merge workflows on this SHA: OSV Scanner Scheduled [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Gradle Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Gradle Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still running at the last captured check. Do not mark them successful until final conclusions are rechecked.
- Android CI steps already completed at last snapshot: security static audit, shipping assemble, lint, JVM unit tests and Gecko policy tests. Benchmark/release builds, static 16 KB artifact checks, QA uploads and Android security instrumentation were still pending/running; no result is claimed for them yet.
- Remaining release gates: final Main CI and dependency-verification results; protected production signing run and verification of signed APK/AAB, signer fingerprints and checksums; physical-device QA (including 3–4 GB RAM, accessibility, private-session/data lifecycle, TLS/mixed-content and runtime 16 KB where supported); complete MASVS/MASTG control assessment and independent penetration testing if required. CI success is not physical-device proof, production signing or certification. No MASVS certification or independent penetration test is claimed.


## Authoritative readiness checkpoint — 2026-11-24

- Main includes PR #68, squash-merged after CODEOWNER approval by `techsomadhanmedia`: https://github.com/techwithrakibofficial/Rivo-Browser/pull/68. Merge commit: `e44cae6fb2e9a34bcc40d6769e78626df1ea40c5`.
- Main ruleset `Protect main branch` is active. It requires pull requests, at least one approval, CODEOWNERS review, resolved review threads and strict required check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- No open pull requests were returned by the live repository query at this checkpoint.
- Post-merge Source SBOM [run 45251818989](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251818989) and OSV Scanner Scheduled [run 45251819550](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819550) completed successfully.
- Post-merge Android CI [run 45251819071](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819071) was still running at this checkpoint. Security static audit, shipping debug assemble, lint, app JVM tests, Gecko policy JVM tests and engine benchmark debug APK builds had succeeded; engine benchmark release bundle build was in progress. Do not claim the full Android CI, minified bundle, static 16 KB artifact verification, QA artifact uploads or Android instrumentation passed until the run concludes.
- Owner-reported device QA: the project owner states they installed and exercised the app on their Android phone and that the phone-based checks were successful. This is owner-reported acceptance, not independently reproduced evidence. It does not by itself establish runtime 16 KB compatibility on a dedicated 16 KB device or independent penetration-test completion.
- Production signing: the owner previously reported configuring four production signing secrets. This integration cannot read secret values or verify Environment protection. No signed production APK/AAB, signer fingerprint verification, checksum publication or GitHub Release is evidenced at this checkpoint. The signed-release workflow validates signer consistency before publishing, but no authorized version/tag execution has occurred.
- Remaining gates: final post-merge Android CI conclusion; authorized version/tag and production signing/release execution; independent penetration testing and any required control-by-control MASVS/MASTG assessment. No certification is claimed.


## Verified Main CI closeout — 2026-11-24 03:27 UTC

- Canonical Main revision for this checkpoint: `ecc1ce211c189d6c4e78aa58a7bb32d1d5b86380` (PR #69 squash merge).
- Main Android CI run [45318499605](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499605): **completed successfully**. Every job step succeeded, including security configuration static audit, shipping assemble, Android lint, app unit tests, Gecko engine policy tests, GeckoView/WebView benchmark builds, minified shipping release bundle, native library/APK 16 KB static alignment, QA artifact uploads and Android security instrumentation tests.
- Gradle Dependency Verification Bootstrap run [45318499764](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499764): **completed successfully**. Both metadata generation/strict validation and the follow-up metadata review job succeeded; verification metadata and lockfile artifacts were uploaded.
- Same-revision supply-chain workflows succeeded: OSV Scanner Scheduled [45318499840](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499840), Source SBOM [45318499754](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499754), and Gradle Dependency Graph [45318499608](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499608).
- CI artifacts include an **unsigned QA-only** AAB, ARM64/ARMv7 debug APKs, test reports and GeckoView/WebView benchmark outputs. These are not production-signed deliverables.
- The owner previously reported successful installation and phone-based checks. Record this as owner-reported device acceptance only; it is not independently reproduced laboratory evidence and does not establish 3–4 GB performance profiling, TalkBack/narrow-screen acceptance, or runtime 16 KB compatibility.
- The signed-release workflow is present and validates the production keystore, APK/AAB signer consistency and SHA-256 assets. The owner reports that the four production signing secrets were configured; this integration cannot inspect secret presence/values or Environment protection. No release tag/dispatch has been executed, no signed production APK/AAB has been generated, and no GitHub Release is published.
- The current GitHub integration exposes no tag-creation, workflow-dispatch, or release-creation action. The release workflow therefore cannot be triggered from this session; do not represent the unsigned CI AAB as a release.
- Remaining production-acceptance gates: execute the protected signed-release workflow from an owner-created reviewed version tag or GitHub Actions manual dispatch; verify downloaded signed APK/AAB, matching signer fingerprints and published checksums; complete the documented device matrix for 3–4 GB performance, accessibility, runtime 16 KB and GeckoView TLS/mixed-content/private-data behavior; complete independent penetration testing and control-by-control MASVS/MASTG assessment. No certification is claimed.


## Verified Android QA closeout — 2026-11-24 04:43 UTC

- Canonical Main at QA execution: `3422fd94aa20f77de6a81bc61c6c4d09ba07a656` (immutable GitHub Actions dependency pinning).
- Android CI run [#45326960699](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45326960699) completed with conclusion **success**. All job steps passed.
- Verified CI gates: strict Gradle dependency-verification metadata check, Android security static audit, shipping debug assembly, Android lint, app JVM unit tests, Gecko policy JVM tests, GeckoView/WebView benchmark builds, minified shipping release AAB build, native library/APK 16 KB static alignment, artifact uploads, and API 35 x86_64 Android Emulator instrumentation task.
- Emulator instrumentation task executed `:app:connectedDebugAndroidTest`; Gradle reported BUILD SUCCESSFUL. Test report artifact is retained by the run. Instrumentation is limited to the checked-in Android test sources; this is not a full UI journey/device compatibility suite.
- Uploaded QA artifacts: ARM64 debug APK, ARMv7 debug APK, unsigned QA-only release AAB, Android test reports, and GeckoView/WebView benchmark artifacts. These are temporary Actions artifacts (14-day retention except test reports at 7 days), not signed production downloads.
- No physical-device test was executed by this CI run. 3–4 GB RAM profiling, TalkBack/OEM behavior, real-device navigation/download/private-session workflows, runtime 16 KB hardware validation and independent penetration testing remain open.
- Production signing/release remains a separate gate. The unsigned AAB is not a Play-ready signed release. No release tag, signed APK/AAB, signer fingerprint verification or GitHub Release is evidenced here.


## 2026-11-24 — authoritative Main verification after PR #72

- Main HEAD: 39c35e64ab4260adb7787fed919c29cd098ac40a (PR #72 merged).
- Android CI run #45334566292: success. Shipping build, lint, JVM/instrumentation tests, universal no-ABI-split debug APK generation, static 16 KB alignment validation and artifact uploads completed.
- Gradle dependency-verification bootstrap #45334566241: success, including the reviewed verification-metadata proposal job. Dependency Graph #45334566254, OSV Scanner #45334566666 and Source SBOM #45334566273: success.
- CI artifacts include ARM64 and ARMv7 debug APKs, universal debug APK, unsigned QA AAB, Android test reports and engine benchmark outputs. These are QA/debug artifacts; the AAB is not production-signed.
- PR #66 governance hardening is merged; .github/CODEOWNERS assigns @techsomadhanmedia, and active ruleset 27894079 requires one approval, code-owner review, current build check, up-to-date branches and resolved conversations.
- Production release is not complete: no version tag/dispatch has been executed, no production-signed APK/AAB or GitHub Release exists. Signed-release workflow accepts v* tags or manual dispatch for an existing tag and verifies the keystore, APK/AAB signer consistency and SHA-256 manifest. Owner-reported signing secrets are not independently inspectable here; Environment reviewer/self-review and deployment-ref restrictions remain unverified.
- Physical-device acceptance, 3–4 GB RAM profiling, accessibility/OEM checks, runtime 16 KB validation, GeckoView runtime security fixtures, full MASVS/MASTG assessment and independent penetration testing remain separate, uncompleted gates. The user has deferred phone testing until the production APK is ready.


## Authoritative checkpoint — 2026-11-24 08:57 UTC

- PR #75 was independently approved by CODEOWNER `techsomadhanmedia` and merged to Main as `8f8bafd8ebfdec97c2fe1e24007feeabd8ff19c4`.
- PR #75 corrected the signed-release cleanup step to remove the actual temporary PKCS#12 file `$RUNNER_TEMP/rivo-upload.p12`; the prior cleanup targeted an obsolete `.jks` filename. The release workflow's Base64 normalization/strict decoding fix from PR #74 is also on Main.
- Main-head checks for this merge: OSV Scanner run [45360601078](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360601078), Gradle Dependency Graph [45360600467](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600467), and Source SBOM [45360600609](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600609) completed successfully. Android CI [45360600469](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600469) and Gradle Dependency Verification Bootstrap [45360600491](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600491) were still running at this checkpoint; do not infer success until final conclusions are checked.
- The existing immutable tag `v0.2.0-phase1` points to the earlier commit `e528d16a951fc6d2a153fcb7519fb0b0e2cd8255`, before the Base64 decoder and cleanup fixes. Do not reuse or move that tag. A new reviewed Main-ancestral tag (candidate `v0.2.1-phase1`) is required for a release containing these fixes.
- No GitHub Release or production-signed APK/AAB is published. The four production signing secret values are not visible to this integration; the owner-reported configuration has not been validated by a successful signed-release run. Production Environment reviewer/deployment restrictions also remain unverified.
- The connected GitHub actions do not expose tag creation or workflow dispatch. An authorized repository owner must create/push the new tag or dispatch the signed-release workflow against an existing new tag. Do not claim release completion until APK/AAB signatures, matching signer fingerprints and downloaded SHA-256 checksums pass.
- Physical-device acceptance, 3–4 GB RAM profiling, TalkBack/accessibility, runtime 16 KB verification, complete MASVS/MASTG review and independent penetration testing remain open.


## 2026-11-24 — Verified release-pipeline checkpoint (PR #85)
- Canonical Main commit: `4821c7af1255486e25b98305fd2b74cde33f488d` (squash merge of PR #85).
- PR #85 fixes post-clean APK verification: signed APKs are preserved in `dist/` before AAB build cleanup, and verification validates those preserved files rather than the deleted APK intermediate directory. Redundant copying was removed.
- PR #85 merged after CODEOWNER approval by `techsomadhanmedia`, zero unresolved review threads, and all five PR workflows succeeded: Android CI, Source SBOM, OSV Scanner PR, Gradle Dependency Graph and Gradle Dependency Verification Bootstrap.
- Android CI passed security static audit, shipping assemble, lint, JVM tests, Gecko policy tests, benchmark builds, minified release bundle, static 16 KB alignment checks and Android security instrumentation. These are CI/emulator results, not physical-device acceptance.
- Highest-priority blocker: trigger the protected signed-release workflow from current Main with a new immutable reviewed version tag. This GitHub connection exposes neither tag creation nor workflow dispatch.
- Release acceptance still requires signed APK/AAB, matching signer fingerprints, R8 mapping and SHA-256 verification. Physical-device QA, 3–4 GB profiling, accessibility/TalkBack, runtime 16 KB, complete MASVS/MASTG review and independent penetration testing remain open.
- Duplicate work retired: PR #84 was superseded by PR #85; do not reopen this fix absent regression evidence.
- Ledger: REL-APK-VERIFY COMPLETE; REL-SIGNED-PUBLISH BLOCKED on owner-triggered dispatch/tag; DEVICE-QA and MASVS-REVIEW incomplete.


## 2026-11-24 — Authoritative state after PR #86 merge
- Canonical Main SHA: `e15ee3ce1c623f70679b9c117018d98de0b1b757`; PR #86 is merged.
- PR #86 CODEOWNER review by `techsomadhanmedia`: APPROVED; review threads: zero unresolved.
- All five PR-head workflows succeeded: Android CI, Source SBOM, OSV Scanner PR, Gradle Dependency Graph and Gradle Dependency Verification Bootstrap.
- PR #86 reconciled the nine project handoff/status documents and corrected README release-state wording. This merge is documentation-only; it does not create a production release or close device/security acceptance gates.
- Signed production release remains blocked on an owner-authorized new immutable tag or workflow dispatch. The current GitHub integration exposes neither action.
- Physical-device QA, 3–4 GB performance, accessibility, runtime 16 KB and full MASVS/MASTG assessment remain incomplete. No certification is claimed.

## 2026-11-25 — Desktop Site implementation candidate
- Added a per-tab Desktop Site toggle on `feat/desktop-site-mode`.
- The GeckoView adapter sets both `USER_AGENT_MODE_DESKTOP` and `VIEWPORT_MODE_DESKTOP`; returning to Mobile Site restores both mobile settings. The page is reloaded after each change.
- The setting is in-memory per tab and is not yet persisted across process restarts.
- Source: Mozilla GeckoSessionSettings API (GeckoView 163): https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSessionSettings.html
- CI, runtime behavior, and device acceptance pending; do not describe this as tested yet.

## 2026-11-26 — Verified current Main checkpoint

- Canonical Main SHA: `3cca9117771e1412a20788be01ea56ec3d768480` (PR #100 merge). The merge commit is signed/verified by GitHub.
- PR #100 integrates the native Compose Rivo start page. Its internal `rivo://home` marker is handled inside the app and is not sent to GeckoView/network; private mode does not expose history; restored tabs reject the internal marker. Dark, Light and Premium Glass use semantic surfaces.
- PR #100 PR-head Android CI and supply-chain checks passed. The PR bootstrap metadata-review job was skipped by design because its workflow condition only permits a `push` to `main`; it is not a failed check.
- On current Main, OSV Scanner Scheduled run `45777625863`, Source SBOM run `45777625516`, and Gradle Dependency Graph run `45777625489` succeeded. Android CI run `45777625468` and Gradle Dependency Verification Bootstrap run `45777625387` were still in progress at the time of this checkpoint. Recheck final conclusions before closing them.
- No open PRs were returned by the repository query. The old `feat/desktop-site-mode` branch is 15 commits ahead and 15 behind current Main; do not merge it directly. Its implementation must be re-ported onto fresh Main and revalidated.
- Remaining gates: final current-Main CI/bootstrap results; safe Desktop Site port and tests; physical-device QA on Android 16 / HyperOS 16.3.0 / Tecno Pova Slim 5G; 3–4 GB profiling; accessibility and all-theme checks; runtime 16 KB validation; MASVS/MASTG evidence review; protected production signing and artifact verification. No production-signed APK/AAB, device acceptance or security certification is claimed.
