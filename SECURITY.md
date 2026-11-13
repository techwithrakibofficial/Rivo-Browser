# Rivo Browser — Security Baseline

Updated: 2026-11-13

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
- Post-merge Main checks were queued when this checkpoint was recorded. No secret or credential was added to the repository.


## Signed-release workflow hardening — PR #51 — 2026-11-13

- The signed-release workflow previously granted repository-wide `contents: write` and persisted checkout credentials while executing the Gradle build with production signing material in its environment.
- PR #51 (`security/harden-signed-release-ci-2026-11-13`) implements owner-gated signing with a `production` environment and `contents: read`; the separate publishing job alone has `contents: write`. Checkout uses `persist-credentials: false`; the release commit must be an ancestor of `main`; APK/AAB signatures are verified before the signed assets and SHA-256 manifest are transferred to the publisher. The publisher uses `gh release create --verify-tag`, so it will not silently create a missing tag and an existing release causes publication to fail rather than overwrite.
- A repository administrator must configure required reviewers (preferably prevent self-review) and restrict allowed deployment branches/tags on the `production` environment before using the workflow. Store all four signing values as **environment secrets on `production`**, not repository-level secrets, so environment approval gates their availability. Merely naming an environment does not itself create an approval gate.
- PR #51 has been synchronized with the PR #50 merge; fresh checks on the synchronized head are required before merge. No production signing run or release is claimed.
