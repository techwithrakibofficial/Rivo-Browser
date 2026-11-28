## 2026-11-24 — Release version alignment gate

- Existing tag `v0.2.2-phase1` is immutable and points to a pre-PR #88 source revision; its app metadata is `0.2.1-phase1` / version code 3 and its workflow has the old fingerprint parser. It must not be used for production signing.
- This PR proposes the next release identity `0.2.3-phase1` / version code 4.
- Release sequence: merge this change after required review and green CI; create immutable `v0.2.3-phase1` tag at merged Main; manually dispatch Signed Android Release from Main using that tag; verify all signed outputs and release checksums.
- Production release remains blocked until the signed workflow succeeds and physical-device/security/accessibility/performance acceptance gates are evidenced.

# Rivo Browser — Development Plan

Updated: 2026-11-24

## Phase 0 — Foundation
**Source implementation: substantially complete; measured engine decision remains open.**
- Modular Kotlin Android project and engine abstraction.
- GeckoView runtime/session implementation; WebView comparison adapter and benchmark variants retained.
- Centralized Dark, Light and Premium Glass appearance system.
- Encrypted local metadata and CI/security workflow foundations.
- Remaining: repeatable physical-device engine comparison and low-RAM measurements.

## Phase 1 — Functional Browser MVP
**Source implementation: integrated in Main; acceptance validation remains.**
- Navigation, omnibox, back/forward/reload/stop and connection security.
- Multi-tab lifecycle, native private sessions and tab restoration.
- Bookmarks, history and encrypted metadata.
- Safe confirmation-gated downloads.
- Advanced Tab Universe and secondary Quick Appearance Picker.
- Remaining: physical device lifecycle, accessibility, data-loss and low-memory validation.

## Phase 2 — Advanced Browser Features
**Source implementation: integrated in Main by the consolidated PR #46 security/privacy integration.**
- Reader Mode via GeckoView page extraction.
- QR-only Google Code Scanner with confirmation-gated navigation.
- Transfer Studio with progress, filters and best-effort cancellation.
- Read-only Developer Mode.
- Gecko site permissions, Privacy Intelligence Center and website notifications.
- Private download metadata exclusion and Clear Browsing Data integration.
- Remaining: runtime/device acceptance and UX/accessibility verification.

## Phase 3 — Security, performance and release
**Automated Main validation is green; production release remains blocked by non-CI gates.**
- Main Android CI #510 passed on Main SHA `5845e1b7804ad2cb102af16acd58af4b77aec3cb`; this included shipping build, lint, JVM tests, Gecko policy tests, benchmark builds, minified release bundle, static 16 KB checks and API 35 instrumentation.
- On that same Main SHA, OSV Scanner Scheduled #31, Gradle Dependency Graph #149 and Source SBOM #195 succeeded. These are point-in-time automated checks, not a full security audit.
- Complete OWASP MASVS control-by-control assessment.
- Physical device and 3–4 GB RAM profiling.
- 16 KB runtime validation on compatible hardware.
- Validate notification/permission/private-session edge cases.
- Signed-release automation is implemented in `.github/workflows/signed-release.yml`; it requires protected keystore secrets, builds and verifies signed APK/AAB, generates checksums and publishes a GitHub Release.
- Configure signing secrets, execute a reviewed release tag, complete physical-device acceptance and staged rollout.

## Release gates
A release candidate is acceptable only when:
1. Main-head Android CI, JVM tests and API 35 instrumentation pass.
2. No unresolved high/critical dependency advisories remain without documented mitigation.
3. Manual privacy, permissions, downloads, notifications and private-session tests pass.
4. Accessibility and all appearance modes pass on narrow and large-font layouts.
5. 3–4 GB performance is measured and meets agreed budgets.
6. 16 KB runtime is validated on compatible hardware.
7. Security review and signing provenance are documented; protected signing secrets are configured and a signed APK/AAB release workflow run succeeds.


## 2026-11-11 execution checkpoint
- PR #43 lazy restored-tab loading and PR #46 notification privacy/MASVS documentation integration are merged into Main.
- PR #46 head passed Android CI, OSV Scanner, Gradle Dependency Graph and Source SBOM. Android CI included JVM tests, Gecko policy tests, static 16 KB alignment and API 35 emulator security instrumentation.
- Next release phase is gated by protected production signing secrets, physical-device/accessibility QA, 3–4 GB performance measurements, runtime 16 KB verification, complete MASTG/data-flow review and main-branch ruleset. Do not mark these as complete without evidence.


## 2026-11-13 CI security hardening task

- PR #47 refreshed release-readiness documentation and merged as `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af`.

- Source review found an avoidable write-token exposure in the resolved-dependency PR workflow: PR-controlled Gradle build logic ran with `contents: write` and persisted checkout credentials.
- PR #50 (`security/readonly-osv-pr-lockfiles-ci-2026-11-13`) carries the read-only Gradle dependency job, checkout with `persist-credentials: false`, no workflow-driven PR pushes, lockfile drift detection and lockfile artifact upload. The scanner's `security-events: write` permission is scoped to the reusable OSV scan job; the workflow default is read-only.
- PR #50 was squash-merged to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9` after PR-head Android CI #521, OSV scan, lockfile resolution, Source SBOM and Dependency Graph jobs succeeded. The separate `osv-scanner` check was `neutral` and is not counted as success.
- PR #51 merged to Main as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`. Main post-merge Android CI and Dependency Graph were still running at the final checkpoint; OSV Scanner and Source SBOM had succeeded.

## Remaining release gates (unchanged)

- Production signing secrets and a successful signed-release workflow with verified APK/AAB signatures and SHA-256 assets.
- Physical Android functional/privacy QA; TalkBack, 320 dp, large-font, contrast and all-theme validation.
- Measured startup, memory, page-load and battery results on real 3–4 GB devices.
- Runtime 16 KB page-size verification on compatible hardware.
- Full manual MASTG/data-flow/privacy review and documented dependency advisory triage.
- Main branch ruleset requiring PR review and successful checks. Repository metadata reports protection disabled; this session's GitHub integration cannot mutate that policy.


## 2026-11-13 signed-release workflow hardening

- PR #51 (`security/harden-signed-release-ci-2026-11-13`) separates the production-signing build from release publication: the owner-gated `build-and-sign` job uses `contents: read` and the `production` environment; the separate `publish` job alone receives `contents: write`. Checkout credentials are not persisted, the selected tag commit must be reachable from `main`, and verified APK/AAB plus SHA-256 assets are passed to the publisher as a short-retention artifact.
- Before production signing is enabled, configure required reviewers and branch/tag restrictions on the GitHub Environment named `production`, and store all four signing values as `production` Environment secrets (not repository-level secrets). The workflow's environment declaration alone is not an approval policy.
- PR #50 merged as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`; PR #51 merged as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a` after its final Android CI and supply-chain checks succeeded. A real signed build, APK/AAB signature verification and published checksum evidence remain required.


## Remaining supply-chain hardening — Gradle dependency verification
- `gradle/verification-metadata.xml` and a verification keyring are absent on Main at PR #51 merge SHA `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`; Gradle artifact checksum/signature verification is not configured.
- Treat this as an explicit follow-up: bootstrap and review SHA-256 verification metadata in a dedicated change, then validate all Android modules with dependency verification enabled. Do not equate dependency lockfiles or OSV scans with artifact verification.


## Serial 01 post-merge verification — 2026-11-13 18:21 UTC

- Current Main SHA at verification: `528a914db56c75c19ca1c4e03d919ee42b355580`.
- PR #50 is merged: `d93f598464a48351ca0440ad2cfeaa768e55d3f9`. PR #51 is merged: `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Main commit check-runs on `528a914`: Android `build`, OSV `Scan known dependency vulnerabilities / osv-scan`, `dependency-graph`, and `Generate source SBOM` all completed with conclusion `success`. These are the four reported check-runs; no pending, failed, neutral or skipped conclusion was present in this check-run set.
- Main branch metadata reports `protected=false`; branch protection is disabled. The available GitHub integration cannot configure branch rules or Environment protection.
- No GitHub Releases exist at verification time. The signed-release workflow is present, but no production signing run, signed APK/AAB, checksum release assets, or published release is evidenced.
- Production Environment reviewer policy, deployment ref restrictions, and presence of all four secrets specifically in the `production` Environment could not be verified with the available integration. Owner/admin must configure and verify these controls before production signing.
- Gradle artifact dependency verification remains unconfigured: `gradle/verification-metadata.xml` and a verification keyring are absent. Lockfiles and OSV scans are separate controls.
- This checkpoint updates CI state only; it does not close physical-device QA, accessibility, 3–4 GB performance profiling, runtime 16 KB validation, or the full MASVS/MASTG review.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`. PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC.
- Active Main ruleset `Protect main branch` requires PR review, at least one approval, CODEOWNERS review, resolved threads and strict check `build`; branch deletion/non-fast-forward updates are blocked and no bypass actors exist.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue in source and adds JVM regression/property tests. GeckoView runtime mixed-content behavior remains unverified on physical devices/fixtures.
- On this Main SHA, OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still in progress at the last captured check; do not claim success before rechecking final conclusions.
- Remaining release gates: production-signed APK/AAB and signer/checksum verification; physical-device QA, accessibility and 3–4 GB RAM profiling; runtime 16 KB verification; full MASVS/MASTG assessment and independent penetration testing where required. CI does not replace these gates; no certification is claimed.


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


## QA execution update — 2026-11-24

- Main Android CI run [#45326960699](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45326960699) is now complete and successful on `3422fd94aa20f77de6a81bc61c6c4d09ba07a656`.
- Verified: static Android security configuration audit, strict dependency verification, debug build, lint, app/Gecko JVM tests, GeckoView/WebView benchmark builds, minified release AAB, static native/ZIP 16 KB alignment and API 35 x86_64 emulator instrumentation task.
- QA artifacts uploaded: ARM64 and ARMv7 debug APKs, unsigned QA AAB, instrumentation reports and benchmark outputs.
- Release remains blocked on protected production signing/tag execution and independent artifact signer/checksum verification. Emulator CI is not physical-device acceptance.


## 2026-11-24 — post-PR #72 execution checkpoint

- PR #72 is merged at Main SHA 39c35e64ab4260adb7787fed919c29cd098ac40a. Main Android CI #45334566292 and Gradle verification bootstrap #45334566241 passed; Dependency Graph #45334566254, OSV #45334566666 and Source SBOM #45334566273 passed.
- Universal no-ABI-split debug APK and static 16 KB alignment validation are integrated and passed on Main. CI artifacts also include ARM64/ARMv7 debug APKs, unsigned QA AAB, Android test reports and engine benchmark outputs.
- Governance PR #66 is merged; CODEOWNERS and active one-approval/code-owner ruleset are present.
- Immediate release milestone: owner creates/pushes an authorized v0.2.0-phase1 tag on the reviewed Main commit. The owner-gated signed-release workflow validates production keystore, builds minified signed APK/AAB, verifies matching signer fingerprints and publishes SHA-256 checksums. No production release exists yet; integration cannot push tags or dispatch workflows.
- Post-release acceptance (intentionally deferred until the APK is ready): physical-device browser journeys, 3–4 GB RAM, accessibility/OEM behavior, runtime 16 KB, private data lifecycle and GeckoView security fixtures. Complete full MASVS/MASTG assessment and independent penetration testing before claiming security acceptance.


## 2026-11-24 — Release workflow reconciliation

- PR #75 merged to Main as `8f8bafd8ebfdec97c2fe1e24007feeabd8ff19c4` after independent CODEOWNER approval. It fixes temporary PKCS#12 cleanup to remove `$RUNNER_TEMP/rivo-upload.p12`.
- PR #74's Base64 normalization and strict decode validation are also integrated. PR-head Android CI, dependency verification, OSV, Dependency Graph and SBOM checks passed before merge.
- Post-merge OSV #45360601078, Dependency Graph #45360600467 and Source SBOM #45360600609 passed. Post-merge Android CI #45360600469 and dependency-verification bootstrap #45360600491 were still running at this checkpoint; recheck before closure.
- Existing `v0.2.0-phase1` is an immutable older tag and excludes the release workflow fixes. The next release should use a new owner-authorized tag (candidate `v0.2.1-phase1`) on the reviewed Main commit.
- The connected GitHub integration cannot create tags or dispatch workflows. The repository owner must perform that one release trigger; do not move/rewrite the old tag or distribute unsigned QA artifacts.
- Production release remains unverified until protected signing succeeds and APK/AAB signatures, matching signer fingerprints and SHA-256 checksums are independently checked. Physical-device, accessibility, low-RAM, runtime 16 KB and full security assessment remain open.


## 2026-11-24 — Incremental execution ledger after PR #85
| ID | Work unit | Status | Evidence / next action |
|---|---|---|---|
| REL-APK-VERIFY | Verify preserved APKs after AAB clean | COMPLETE | PR #85 merged as `4821c7af1255486e25b98305fd2b74cde33f488d`; all five PR workflows succeeded; CODEOWNER approved; no unresolved threads |
| REL-SIGNED-PUBLISH | Protected production signing/publication | BLOCKED | Owner must dispatch signed-release workflow or push a new immutable tag; current integration cannot trigger either |
| REL-ARTIFACT-AUDIT | Signed artifact integrity | BLOCKED | Verify APK/AAB signatures, matching cert fingerprints, R8 mapping and SHA-256 after successful release run |
| QA-DEVICE | Physical-device and target-class QA | INCOMPLETE | Run documented device matrix; CI/emulator does not replace physical devices |
| QA-ACCESS | Accessibility and low-end performance | INCOMPLETE | TalkBack, large font, contrast, reduced motion, 3–4 GB memory/startup/battery |
| SEC-MASVS | MASVS/MASTG control evidence review | INCOMPLETE | Complete control-by-control evidence and residual-risk register; independent penetration test not evidenced |
- Dependency order: REL-SIGNED-PUBLISH → REL-ARTIFACT-AUDIT; QA-DEVICE/QA-ACCESS and SEC-MASVS are independent acceptance tracks.
- PR #84 is superseded; do not repeat REL-APK-VERIFY without regression evidence.

## 2026-11-25 — Desktop Site feature work unit
| ID | Work unit | Status | Evidence / next action |
|---|---|---|---|
| UX-DESKTOP | Per-tab Desktop Site request | IMPLEMENTED — VALIDATION PENDING | Branch `feat/desktop-site-mode`; verify Gecko UA + viewport both change, mobile reset, private-tab behavior, reload and tab isolation in exact-head CI/device tests |

## 2026-11-26 — Current execution checkpoint

- PR #100 merged to Main at `3cca9117771e1412a20788be01ea56ec3d768480`; native Compose start page is integrated with internal `rivo://home` handling and private-history separation.
- PR #100 PR-head CI passed; its metadata-review job skipped as expected on `pull_request` because the job is restricted to Main pushes.
- Current Main OSV, SBOM and Dependency Graph runs succeeded. Main Android CI and Gradle verification bootstrap were still in progress at last inspection; recheck before marking green.
- No open PRs. Desktop Site work exists on a branch that has diverged 15 commits each way from Main; direct merge is unsafe. Port only the minimal verified GeckoView UA+viewport change onto a fresh Main branch and re-test.

### Priority order
1. Close current Main CI/bootstrap and inspect any failures.
2. Port/retest Desktop Site safely (or explicitly defer if API/UI conflicts arise).
3. Complete device/theme/accessibility/low-RAM/security acceptance evidence.
4. Execute protected production signing with owner authorization and verify signed artifacts.

## Correction — Desktop Site status (2026-11-26)

Desktop Site is already integrated by PR #93 and is an ancestor of current Main. The old branch is 14 commits behind Main; do not port or merge it again. Move this work item from implementation to physical-device and lifecycle validation (per-tab isolation, reload, mobile reset, private tabs, restore).

## CI status refresh — 2026-11-26

Dependency-verification bootstrap `45777625387` completed successfully. Android CI `45777625468` was still running Android security instrumentation tests at the latest check. Close this gate only after a successful final run.


## 2026-11-28 — Main UX correction verification and 0.2.8 release preparation

- Main includes PR #108 at merge commit `8b2f9adc8b6a2828cbfa1d144828b3c367a8c668`.
- PR #108 CODEOWNER review and merge completed. Exact-merge-commit Android CI run [46147059192](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059192) completed successfully; Gradle dependency-verification bootstrap [46147059274](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059274) also completed successfully.
- Same-revision supply-chain checks succeeded: OSV Scanner [46147059324](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059324), Source SBOM [46147059195](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059195), and Gradle Dependency Graph [46147059212](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059212).
- Android CI passed its configured build, lint, JVM/policy tests, release-bundle build, static 16 KB alignment checks and security instrumentation steps. These are CI/emulator checks, not physical-device acceptance or runtime 16 KB proof.
- CI produced unsigned QA/debug artifacts only. The latest public signed release remains `v0.2.7-phase1.1`; do not distribute unsigned CI artifacts as a production APK.
- Next release identity is `0.2.8-phase1` / versionCode 9. Production signing and publishing require a new immutable tag or manual workflow dispatch, then signer/checksum verification. The available GitHub connection in this session cannot create/push tags or dispatch workflows.
- Physical TECNO Pova Slim 5G / Android 16 / HyperOS 16.3.0 acceptance, supplied-video visual matching, accessibility/TalkBack, 3–4 GB performance profiling, runtime 16 KB validation and independent MASVS/MASTG/penetration assessment remain unverified. No certification is claimed.

### Execution order
1. Complete review/merge of the 0.2.8 release-preparation metadata and documentation.
2. Owner-trigger the protected signed-release workflow using the immutable `v0.2.8-phase1` tag.
3. Verify downloaded ARM64/ARMv7 APKs, AAB, matching signer fingerprints and SHA-256 manifest.
4. Complete physical-device UX, accessibility, low-memory, runtime 16 KB and security acceptance with evidence.


## 2026-11-28 — PR #110 candidate (not yet merged)
- Reworked the native start page toward a Chrome-style Google home: Google search field, local bookmark/history shortcuts, History and Incognito actions, plus Bookmarks and Tabs quick actions. No external news feed or fake account profile is presented.
- Browser top chrome now exposes Home/New tab, Guest profile/settings, open-tab count and overflow controls.
- Settings panel surface is opaque in Premium Glass to prevent website pixels bleeding through text; explicit semantic foreground colors are used.
- GeckoView's documented Android Autofill bridge is enabled on created GeckoView instances. This allows a separately configured Android Autofill provider to participate; it is not a Rivo password manager, and it does not implement credential storage or sync.
- Rivo account sign-in/cloud sync, a built-in password vault, and an extension marketplace remain unavailable and are not represented as shipped features.
- Candidate app version: 0.2.9-phase1 / versionCode 10. PR #110 requires fresh CI/review/merge; physical-device visual and autofill acceptance remain pending.
