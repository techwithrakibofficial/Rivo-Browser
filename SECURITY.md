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

## Explicit limitations
- Downloaded file bytes are not encrypted by the metadata cipher.
- Private browsing does not erase user-saved normal downloads or hide network activity.
- Android Keystore hardware-backed/StrongBox storage is not guaranteed.
- Persistent Gecko permission grants may be non-revocable; UI must identify this accurately.
- Tracker counts, full tracker blocking, anonymity and MASVS certification are not claimed.
- Developer Mode is read-only; remote debugging and console output remain disabled.
- CI AAB/APK files are unsigned QA artifacts.

## Remaining security assessment
- Complete OWASP MASVS control-by-control review and record evidence/residual risk.
- Triage OSV findings and confirm lockfile coverage on Main.
- Test permission/notification/private-session and download lifecycle on physical devices.
- Verify Android Keystore behavior on supported devices and 16 KB runtime behavior.
- Configure signing in protected CI secrets; never commit keystore files/passwords.
