# Rivo Browser — Development Plan

Updated: 2026-11-13

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
