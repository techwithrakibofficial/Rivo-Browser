# Changelog

## 2026-11-10 — Main CI and dependency validation
- Main Android CI run #42763857175 passed on `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`: shipping build, lint, unit tests, Gecko policy tests, GeckoView/WebView benchmark builds, minified release bundle, static 16 KB alignment checks and API 35 Android security instrumentation.
- QA artifacts uploaded: ARM64 and ARMv7 debug APKs, unsigned QA AAB, Android test reports and engine benchmark artifacts.
- Same-head OSV Scanner scanned five Gradle lockfiles (814 resolved package entries) and reported **No issues found**; dependency graph and source SBOM workflows succeeded.
- These automated checks do not replace physical-device acceptance, low-RAM profiling, 16 KB runtime verification or a full OWASP MASVS assessment.

## 2026-11-10 — Phase 2 integration merged
- Merged PR #38 (Reader Mode, QR, Transfer Studio, Developer Mode, site permissions, Privacy Intelligence Center, website notifications and private-download hardening) into Main.
- Reconciled the integration candidate with latest Main history using a two-parent commit; the combined source tree is `9115b9d3c607353facc15ed9b6fb4e3e53ce696d`.
- Preserved Gecko remote debugging and console output as disabled.
- Added resolved Gradle dependency lockfiles and dependency graph/OSV/SBOM workflow improvements.
- Exact source tree passed Android CI run #463 on its PR #37 head. PR #38 SBOM, dependency graph and OSV workflows passed.
- Physical-device QA, low-RAM profiling, 16 KB runtime verification, MASVS assessment and production signing remain outstanding.

## 2026-11-10 — Signed release automation
- Merged PR #40: `.github/workflows/signed-release.yml` builds signed ABI APKs and AAB, verifies signatures, creates SHA-256 checksums and publishes assets for a version tag or manual dispatch.
- Workflow requires four protected Actions secrets; no keystore or credential is stored in Git.
- Physical-device QA and signed-release execution remain outstanding.

## Earlier changes
See Git history for prior Tab Universe, storage hardening, browser navigation, theme and security increments.


## 2026-11-10 — Repository governance audit
- Verified GitHub reports `main` as unprotected, with no required status checks enforced by branch protection.
- Recorded required governance remediation in PROJECT_STATE.md, HANDOFF.md and SECURITY.md. The available GitHub integration does not expose branch-protection write operations, so the setting remains unchanged.
- Latest Main-head Android CI run #485 completed successfully.


## 2026-11-10 — Lazy background-tab restoration (PR #43)
- Changed process restoration so saved background tabs are reconstructed without immediately navigating every saved URL; the selected tab loads at startup and other restored tabs load on first selection.
- Preserved immediate loading for user-created tabs and added JVM tests for the pending restored-tab load policy.
- PR Android CI #496 passed, including JVM tests, lint, shipping/benchmark builds, minified bundle, static 16 KB checks and API 35 instrumentation. Physical 3–4 GB RAM profiling remains outstanding.


## 2026-11-11 — Release-readiness integration
- Merged PR #43: restored background tabs defer navigation until first selection, reducing avoidable startup work; physical low-RAM profiling remains outstanding.
- Integrated website notification public-preview redaction using `VISIBILITY_PRIVATE` and generic Rivo-owned public content, with a JVM regression test.
- Added a preliminary source-based OWASP MASVS control gap register in `SECURITY_ASSESSMENT.md`; it is not a certification or independent audit.
- Physical-device QA, production signing, runtime 16 KB validation and branch protection remain release gates.


## 2026-11-11 — Security/release-readiness PR #46 merged
- Squash-merged PR #46 to Main as `b50350a241908fb1f38d2920b7a358451cb436f7`.
- Website-controlled notification previews now use private visibility and a generic Rivo-owned public version; added `WebNotificationPrivacyPolicyTest`.
- Added preliminary source-based `SECURITY_ASSESSMENT.md` MASVS gap register; it is not a penetration test or certification.
- All PR-head CI workflows passed: Android CI, OSV Scanner PR, Gradle Dependency Graph and Source SBOM. Android CI included lint, JVM tests, Gecko policy tests, static 16 KB checks and API 35 emulator security instrumentation.
- Uploaded ARM64/ARMv7 debug APKs and unsigned QA AAB; these are not production-signed releases.
- Post-merge Main workflows were still running at the time of this entry. Production signing, physical-device QA, low-RAM profiling, runtime 16 KB verification and branch protection remain open.


## 2026-11-13 — Security CI and signed-release workflow hardening (Serial 01)
- PR #50 hardens OSV PR scanning: Gradle dependency resolution is read-only, checkout credentials are not persisted, workflow pushes are removed, lockfile drift is surfaced, and resolved lockfiles are uploaded. OSV SARIF write access is scoped to the reusable scanner job.
- PR #51 separates production signing/build from publication; only the publishing job receives `contents: write`. The signing job is owner-gated, uses the `production` environment, verifies Main ancestry and APK/AAB signatures, and prepares SHA-256 checksums.
- Main Android CI #513 succeeded on `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af`. Earlier PR heads passed relevant Android/supply-chain checks; one OSV status was `neutral`, and latest amended heads require fresh CI.
- No production signing secrets, signed release, or APK/AAB publication is claimed. Environment reviewer policy, production Environment-scoped secret configuration (not repository-level secrets), branch protection and physical-device QA remain release gates.


## 2026-11-13 — PR #50 merged; PR #51 synchronized
- Squash-merged PR #50 to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- Verified PR #50 Android CI #521, OSV introduced/resolved dependency scans, Dependency Graph and Source SBOM succeeded. Five lockfiles (814 package entries) reported no OSV issues; lockfile drift check and artifact upload succeeded.
- The separate `osv-scanner` status was `neutral` due a configuration-not-found notice and is not reported as success.
- PR #51 remains open and is synchronized with the new Main base; its final checks must complete before merge.
- Production Environment reviewers, Environment-scoped signing secrets, branch protection, physical-device acceptance and a real signed release remain outstanding.


## 2026-11-13 — PR #51 merged (Serial 01)
- Squash-merged PR #51 to Main as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`; PR #50 had already merged as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- Final PR #51 Android CI, OSV introduced/resolved scans, Dependency Graph and Source SBOM succeeded. OSV scanned 814 package entries with no issues; lockfile drift was clean and the artifact was uploaded.
- The separate `osv-scanner` check was `neutral` due scheduled-workflow configuration-not-found; it is not reported as success.
- Main post-merge OSV Scanner and Source SBOM succeeded; Android CI and Dependency Graph were running at the checkpoint.
- Production Environment reviewers/secrets, branch protection, Gradle dependency verification, physical-device QA and an actual signed release remain outstanding.


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
