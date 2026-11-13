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
