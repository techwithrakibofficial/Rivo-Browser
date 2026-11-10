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
- Latest documentation head `56b14acf0755d27c0f90035204ffef0a2502d493` triggered Android CI run #481; earlier source CI run #480 passed.


## 2026-11-10 — Private download cleanup test coverage
- Added JVM regression coverage for the existing private-download cleanup boundary: only the direct `.rivo-private` child is removed; normal/sibling files remain; missing directories are a no-op; symlinks targeting outside the downloads root are not followed.
- No production code or dependency changes. PR #42 Android CI verification is pending.
