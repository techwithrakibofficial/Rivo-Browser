# Changelog

## 2026-11-10 — Phase 2 integration merged
- Merged PR #38 (Reader Mode, QR, Transfer Studio, Developer Mode, site permissions, Privacy Intelligence Center, website notifications and private-download hardening) into Main.
- Reconciled the integration candidate with latest Main history using a two-parent commit; the combined source tree is `9115b9d3c607353facc15ed9b6fb4e3e53ce696d`.
- Preserved Gecko remote debugging and console output as disabled.
- Added resolved Gradle dependency lockfiles and dependency graph/OSV/SBOM workflow improvements.
- Exact source tree passed Android CI run #463 on its PR #37 head. PR #38 SBOM, dependency graph and OSV workflows passed; post-merge Main CI is tracked separately.
- Physical-device QA, low-RAM profiling, 16 KB runtime verification, MASVS assessment and production signing remain outstanding.

## 2026-11-10 — Signed release automation
- Merged PR #40: `.github/workflows/signed-release.yml` builds signed ABI APKs and AAB, verifies signatures, creates SHA-256 checksums and publishes assets for a version tag or manual dispatch.
- Workflow requires four protected Actions secrets; no keystore or credential is stored in Git.
- Physical-device QA and signed-release execution remain outstanding.

## Earlier changes
See Git history for prior Tab Universe, storage hardening, browser navigation, theme and security increments.
