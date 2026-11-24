# Rivo Browser — Security Baseline

Updated: 2026-11-23

## Release pipeline hardening candidate — 2026-11-23
- APKs are verified with Android SDK apksigner; AAB signature verification requires a positive jarsigner verification result. SHA-256 signer certificate fingerprints must match across APK/AAB outputs.
- R8 mapping output is mandatory and retained as a GitHub Actions artifact for 90 days. Because this repository is public, anyone with repository read access can download it; it is not included in public Release assets.
- Published release assets are downloaded again and checked against SHA256SUMS after upload.
- These are workflow-source controls only until a production-signed run using protected Environment secrets completes. PR #64 remains unmerged; no signed artifact has been verified in this session.


OWASP MASVS is the assessment baseline. This document is not an independent security audit or certification.

## Implemented controls
- GeckoView per-tab sessions; no JavaScript-to-native bridge.
- TLS certificate validation is not overridden; browser security state comes from engine callbacks.
- Native private sessions; private tabs excluded from app-side history, bookmarks and restoration.
- Per-session tracking protection; no claim of complete tracker blocking or anonymity.
- HTTPS-only supported site permission prompts; unsupported content/device capabilities default-deny.
- Website notifications require HTTPS and normal mode; receiver is non-exported and PendingIntent immutable.
- Download confirmation, HTTPS URL validation, filename normalization, app-specific storage, temporary staging, atomic finalization, 250 MB cap and scoped FileProvider.
- Restored download paths constrained to canonical app-specific roots and existing size cap.
- Private download metadata excluded from persistent index; private file bytes are removed when the originating tab closes.
- Android backup disabled, cleartext traffic disabled and no broad storage permission.
- Sensitive browser metadata encrypted with Android Keystore AES-256-GCM; malformed/ciphertext-integrity failures block further sensitive writes.
- Dependency lockfiles, OSV scanner, source SBOM and resolved Gradle dependency graph workflows.

## Automated security evidence
- Main-head OSV Scanner run [#42763857162](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42763857162) completed successfully and printed **No issues found**.
- Scanner read five Gradle lockfiles: app (310), engine-gecko (152), engine-benchmark (150), engine-api (101), engine-webview (101) — 814 package entries in total.
- The scanner uploaded SARIF to GitHub Code Scanning. A clean run means no known findings were detected in those scanned lockfiles at that run; it is not proof of vulnerability-free software.
- Main-head dependency graph and source SBOM workflows also completed successfully.

## Explicit limitations
- Downloaded file bytes are not encrypted by the metadata cipher.
- Private browsing does not erase user-saved normal downloads or hide network activity.
- Android Keystore hardware-backed/StrongBox storage is not guaranteed.
- Persistent Gecko permission grants may be non-revocable; UI must identify this accurately.
- Tracker counts, full tracker blocking, anonymity and MASVS certification are not claimed.
- Developer Mode is read-only; remote debugging and console output remain disabled.
- CI AAB/APK files are unsigned QA artifacts.

## Remaining security assessment
- Complete OWASP MASVS control-by-control review and record evidence/residual risk; this document is a baseline, not a completed audit.
- Test permission/notification/private-session and download lifecycle on physical devices.
- Verify Android Keystore behavior on supported devices and 16 KB runtime behavior.
- Configure signing in protected CI secrets; never commit keystore files/passwords.


## Repository and release governance
- Verified GitHub branch metadata currently reports `main` as unprotected, with protection disabled and no required status checks. This permits direct pushes/force-pushes unless separately restricted.
- Before shipping, configure a GitHub repository ruleset/branch protection requiring PR review and successful Android CI; block force-push and branch deletion; limit release/tag publishing to trusted maintainers.
- The available connected GitHub integration exposes branch-protection/ruleset reads but not mutation, so no governance change is claimed here.


## OWASP MASVS assessment register
- Preliminary source-based control register: [SECURITY_ASSESSMENT.md](SECURITY_ASSESSMENT.md). This is a gap assessment, not certification or an independent penetration test.

## Website notification lock-screen privacy
- Website-provided notification title, body and origin are treated as potentially sensitive.
- Native website notifications use Android `Notification.VISIBILITY_PRIVATE` and a generic public-version notification containing only Rivo-owned text.
- This mitigation does not guarantee identical behavior across all OEM lock screens or user notification settings; physical-device verification remains required.


## 2026-11-11 security integration verification
- PR #46 merged to Main as `b50350a241908fb1f38d2920b7a358451cb436f7`.
- Website-controlled notification title/body/origin are not exposed in the generic public notification representation; Android private visibility is applied. JVM regression test is included. OEM lock-screen and screen-sharing behavior still requires physical-device validation.
- PR-head OSV Scanner, dependency graph, Source SBOM and Android CI all passed. Android CI includes API 35 emulator instrumentation and static 16 KB alignment checks; neither is a full penetration test or runtime 16 KB hardware proof.
- `SECURITY_ASSESSMENT.md` is a preliminary source/configuration MASVS gap register only. No independent penetration test, certification or complete MASTG review is claimed.
- Production signing credentials, physical-device QA, full data-flow review and branch protection remain unresolved.


## 2026-11-13 CI workflow security review

- Review of `.github/workflows/osv-scanner-pr.yml` found that the resolved-dependency job executed PR-controlled Gradle build logic while holding `contents: write` and a persisted checkout credential, then pushed generated lockfiles back to the PR branch.
- This unnecessarily exposed repository write authority to code under test. PR #50 (`security/readonly-osv-pr-lockfiles-ci-2026-11-13`) implements `contents: read` for the Gradle resolution job, `persist-credentials: false`, no workflow-driven branch pushes, lockfile drift reporting and artifact upload. The workflow-level default is read-only; `security-events: write` is limited to the OSV reusable scan job.
- PR #50 was squash-merged to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9` after Android CI #521 and the OSV, resolved-lockfile, Dependency Graph and Source SBOM jobs completed successfully. OSV reported no issues across 814 package entries and lockfile drift verification passed; the lockfile artifact was uploaded. The separate `osv-scanner` check remained `neutral` (configuration-not-found notice), not success.
- PR #51 was subsequently squash-merged as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a` after its final Android CI, OSV scan, lockfile verification, Dependency Graph and Source SBOM jobs succeeded. The separate `osv-scanner` check was `neutral` (scheduled-workflow configuration-not-found notice), not success. No secret or credential was added to the repository.


## Signed-release workflow hardening — PR #51 — 2026-11-13

- The signed-release workflow previously granted repository-wide `contents: write` and persisted checkout credentials while executing the Gradle build with production signing material in its environment.
- PR #51 (`security/harden-signed-release-ci-2026-11-13`) implements owner-gated signing with a `production` environment and `contents: read`; the separate publishing job alone has `contents: write`. Checkout uses `persist-credentials: false`; the release commit must be an ancestor of `main`; APK/AAB signatures are verified before the signed assets and SHA-256 manifest are transferred to the publisher. The publisher uses `gh release create --verify-tag`, so it will not silently create a missing tag and an existing release causes publication to fail rather than overwrite.
- A repository administrator must configure required reviewers (preferably prevent self-review) and restrict allowed deployment branches/tags on the `production` environment before using the workflow. Store all four signing values as **environment secrets on `production`**, not repository-level secrets, so environment approval gates their availability. Merely naming an environment does not itself create an approval gate.
- PR #51 was merged to Main as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`. Its final Android CI and supply-chain jobs succeeded; the separate `osv-scanner` check remained `neutral`. No production signing run or release is claimed.


## Gradle artifact verification status — 2026-11-13
- Repository inspection on Main SHA `89d0b2beb5ccdaf6bf265fd222418a65fcba100a` found no `gradle/verification-metadata.xml` or verification keyring. Gradle dependency verification is therefore **not configured** in this repository.
- Dependency lockfiles, OSV scanning and dependency-graph submission are active, but they do not substitute for Gradle artifact checksum/signature verification. Bootstrap and review verification metadata in a separate controlled change before claiming this control.


## Serial 01 post-merge verification — 2026-11-13 18:21 UTC

- Current Main SHA at verification: `528a914db56c75c19ca1c4e03d919ee42b355580`.
- PR #50 is merged: `d93f598464a48351ca0440ad2cfeaa768e55d3f9`. PR #51 is merged: `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Main commit check-runs on `528a914`: Android `build`, OSV `Scan known dependency vulnerabilities / osv-scan`, `dependency-graph`, and `Generate source SBOM` all completed with conclusion `success`. These are the four reported check-runs; no pending, failed, neutral or skipped conclusion was present in this check-run set.
- Main branch metadata reports `protected=false`; branch protection is disabled. The available GitHub integration cannot configure branch rules or Environment protection.
- No GitHub Releases exist at verification time. The signed-release workflow is present, but no production signing run, signed APK/AAB, checksum release assets, or published release is evidenced.
- Production Environment reviewer policy, deployment ref restrictions, and presence of all four secrets specifically in the `production` Environment could not be verified with the available integration. Owner/admin must configure and verify these controls before production signing.
- Gradle artifact dependency verification remains unconfigured: `gradle/verification-metadata.xml` and a verification keyring are absent. Lockfiles and OSV scans are separate controls.
- This checkpoint updates CI state only; it does not close physical-device QA, accessibility, 3–4 GB performance profiling, runtime 16 KB validation, or the full MASVS/MASTG review.

## 2026-11-23 — live GitHub governance reconciliation

See [GOVERNANCE_AUDIT.md](GOVERNANCE_AUDIT.md) for the live ruleset and release-security audit. This checkpoint supersedes older statements in this file that Main had no active protection: ruleset `27894079` is active and requires PRs, the `build` status check, up-to-date branches, conversation resolution, and blocks deletion/force-push. However, it requires **zero** approvals, does not dismiss stale reviews, and does not require code-owner review. The classic branch-protection endpoint remains disabled; the active ruleset is the operative protection.

On branch `security/governance-hardening-2026-11-23`:
- Removed the privileged PR write-back job from Gradle verification bootstrap. PR-controlled Gradle build logic now only generates review artifacts under the read-only bootstrap job; it is not followed by a job that receives `contents: write` to push artifact contents.
- Changed signed-release publication to fail when a GitHub Release already exists, instead of uploading with `--clobber`.
- These workflow changes are pending PR CI; they are not yet integrated into Main.
- Production Environment reviewers, deployment ref restrictions and Environment-scoped secret presence remain unverified because the connected integration cannot read Environment administration settings.
- No production-signed release exists at this checkpoint. No signed build or production release is claimed.

### Dependency verification status correction — 2026-11-23
- Main now contains `gradle/verification-metadata.xml` and `DEPENDENCY_VERIFICATION.md`; the earlier 2026-11-13 note that metadata was absent is historical and superseded.
- The current metadata records SHA-256 checksums with PGP signature verification disabled. Android CI uses strict Gradle dependency verification. This is a trust-on-first-use checksum baseline, not upstream signature attestation.


## 2026-11-24 authoritative follow-up (supersedes earlier 2026-11-23 snapshot)

- The repository owner reports that all four signing secrets have now been added to the GitHub Actions `production` Environment: `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, and `RIVO_UPLOAD_KEY_PASSWORD`. Secret values were not requested, accessed, or logged. The connected integration cannot inspect Environment secret metadata, so this is owner-reported and not independently API-verified.
- A fresh read of active ruleset `27894079` shows: one approving review required; stale approvals dismissed on new pushes; code-owner review required; `build` required with up-to-date branch; conversation resolution required; deletion and non-fast-forward updates blocked; no bypass actors. This supersedes earlier text in this audit branch saying zero approvals / stale dismissal off / code-owner review off.
- Classic branch-protection API remains inaccessible to this integration; the active ruleset is the effective protection observed. Production Environment reviewer/self-review and deployment-ref settings remain unverified.
- No `CODEOWNERS` file was found at root, `.github/CODEOWNERS`, or `docs/CODEOWNERS` on Main. Do not add a catch-all owner entry until a distinct trusted reviewer/code-owner identity is designated; assigning the PR author as sole code owner would make the review requirement unworkable for this PR.
- PR #66 is open and unmerged. Its five PR-head CI workflows previously completed successfully, but it has no submitted reviews and GitHub reports the merge state as blocked. Do not bypass review or merge protections. A trusted collaborator must approve; if CODEOWNERS is to be enforced for workflow/security files, designate a second trusted GitHub username and add that identity in a follow-up.
- No production-signed APK/AAB or published release is claimed. A real signed-release workflow run and verification of signatures/fingerprints/checksums remain outstanding.


## 2026-11-24 governance and signing follow-up
- Main ruleset `27894079` now requires one approving review, dismisses stale approvals, and enables code-owner review; it retains the required `build` check, up-to-date branches, conversation resolution, deletion/force-push protection and no bypass actors.
- The owner reports adding all four signing values to the `production` Environment. The integration cannot inspect Environment secrets or reviewer/deployment restrictions; this is not independent verification. Never put secret values in source, logs or chat.
- PR #66 has five successful CI workflows on its current head but is not merged; it requires an independent reviewer. No CODEOWNERS file currently exists, so the enabled code-owner-review switch has no mappings to enforce. Add a trusted second maintainer as code owner before relying on this control.
- No production-signed APK/AAB or published release is verified. Action references remain tag-based; immutable SHA pinning is still outstanding.


## Android application audit status — 2026-11-23

Main revision reviewed: `0df6760287b0de5c07d2468b247145dae9c0ea4b`.

The source/configuration review found no confirmed fixable high- or medium-severity vulnerability in the reviewed application paths. Existing protections include GeckoView private sessions/tracking protection, no identified website-callable native JavaScript bridge, HTTPS-only download acceptance, bounded/sanitized download names, canonical-root validation for restored downloads, encrypted browser metadata using Android Keystore AES-GCM, disabled Android backup/cleartext platform traffic, and non-exported FileProvider/notification receiver components.

This is not a penetration-test result or certification. GeckoView TLS/mixed-content behavior, merged manifest, permission/IPC runtime behavior, private-mode cleanup, Keystore lifecycle, screenshots/recents, and data-flow/privacy disclosure still require device/runtime evidence. Normal downloaded file bytes are not encrypted by the metadata cipher. See [SECURITY_AUDIT.md](SECURITY_AUDIT.md) and [SECURITY_ASSESSMENT.md](SECURITY_ASSESSMENT.md) for scope, evidence and residual risks.


### Mixed-content security indicator

Rivo now surfaces GeckoView's active/passive mixed-content status in its connection message. Loaded active or passive mixed content is explicitly disclosed, and unknown mixed-content status is not represented as a fully secure page. See `SECURITY_AUDIT.md` for evidence and the remaining runtime test requirement.


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


## 2026-11-24 — Android CI verification

- Android CI [run #45326960699](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45326960699) completed successfully on Main SHA `3422fd94aa20f77de6a81bc61c6c4d09ba07a656`.
- Security static audit passed: backup/cleartext network controls, system trust anchors, exported component restrictions, absence of shipping JavaScript bridge/custom trust-all TLS hook, disabled Gecko diagnostics/debug configuration and WebView benchmark/probe restrictions.
- Strict dependency verification, Android lint, JVM tests and API 35 x86_64 emulator instrumentation also completed successfully. The instrumentation tests are narrow regression checks, not a penetration test.
- Static 16 KB package/native-library alignment passed for CI APK/AAB artifacts. This is not runtime proof on 16 KB page-size hardware.
- The unsigned AAB and debug APK artifacts are QA-only. Production signing, signer fingerprint verification, release checksum validation, physical-device security behavior and independent MASVS/MASTG assessment remain separate gates.


## 2026-11-24 authoritative governance and CI update

- Historical notes above are retained as dated audit snapshots; current state: PR #66 and PR #72 are merged. PR #66 added .github/CODEOWNERS assigning * @techsomadhanmedia; active ruleset #27894079 requires one approving review, stale-review dismissal, code-owner review, required build status, up-to-date branches and resolved conversations.
- Main SHA 39c35e64ab4260adb7787fed919c29cd098ac40a: Android CI #45334566292, Gradle verification bootstrap #45334566241, Dependency Graph #45334566254, OSV Scanner #45334566666 and Source SBOM #45334566273 all passed.
- Dependency verification metadata is present and strict Gradle verification passed. Current checksum baseline uses SHA-256 with PGP signature verification disabled; this is checksum-based trust-on-first-use, not upstream publisher-signature attestation.
- Signed-release workflow is owner-gated, requires the production Environment, verifies keystore access, R8 mapping, APK/AAB signatures and signer fingerprint consistency, creates a SHA-256 manifest, and refuses to overwrite an existing GitHub Release. It triggers on v* tags or manual dispatch with an existing tag. No tag/run/signed artifact/release currently exists.
- Repository owner reports the four signing secrets are configured in the production Environment; this integration cannot inspect secret presence/values or Environment reviewers, self-review prevention or deployment-ref restrictions. Treat those controls as unverified until an administrator checks them.
- Static CI success is not physical-device security evidence. Physical-device privacy/data lifecycle, 3–4 GB profiling, accessibility, runtime 16 KB, GeckoView TLS/mixed-content fixtures, full MASVS/MASTG control review and independent penetration testing remain open. No certification is claimed.
