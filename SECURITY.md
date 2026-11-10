# Rivo Browser — Security Baseline

Updated: 2026-11-10

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

## Preliminary OWASP MASVS gap assessment

A source-based, control-by-control preliminary register is maintained in [SECURITY_ASSESSMENT.md](SECURITY_ASSESSMENT.md). It distinguishes source evidence from partial coverage, gaps, scope exclusions and device-only verification. It is not a penetration test or certification. Do not mark a control passed without recorded MASTG test evidence or an approved, documented scope decision.
