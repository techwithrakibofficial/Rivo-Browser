# Rivo Browser — Security Baseline

Updated: 2026-11-09

OWASP MASVS is the assessment baseline. This document is not an independent security audit or certification.

## Implemented on Main
- GeckoView runtime with one session per tab; no JavaScript-to-native bridge.
- Native Gecko private sessions; private tabs excluded from app-side history, bookmarks and restoration.
- Per-session tracking protection; do not claim complete tracker blocking or anonymity.
- No TLS certificate validation override.
- HTTP(S)-only browser navigation and safe download flow.
- Confirmation-gated downloads, filename sanitization, app-specific storage, temporary-file streaming, atomic finalization, 250 MB cap and scoped FileProvider open/share.
- Android backup disabled, cleartext traffic disabled and no broad storage permission.
- Browser metadata encrypted with Android Keystore AES-256-GCM; physical-device key behavior still requires validation.
- No account, telemetry or mandatory backend.

## Integration candidate — PR #37
- Gecko site permissions are mediated only for supported secure-origin capabilities; unsupported permissions default-deny.
- Android location/camera/microphone requests are restricted to an explicit allowlist and user-mediated; denial state is stored encrypted.
- Website notifications require HTTPS, Android notification permission and enabled notification channel; private-mode and insecure-origin notifications are rejected.
- Notification receiver is non-exported and uses immutable PendingIntent.
- Privacy Intelligence Center reports Gecko permission state and revocation constraints; it does not fabricate tracker counts.
- Private downloads use a dedicated app-specific directory; private download metadata is not persisted and file bytes are deleted when the originating private tab closes.
- Restored download metadata is validated against canonical app-specific roots and file size.
- Clear Browsing Data clears Gecko site data/settings and local history/download metadata while preserving bookmarks and normal downloaded file bytes.
- Reader Mode uses GeckoView page extraction, rejects pages marked gated and does not bypass authentication/paywalls.
- QR payloads are bounded/sanitized and require user confirmation before opening/searching.
- Developer Mode is read-only; remote debugging, console output and about:config remain disabled.

## Important privacy boundaries
- Private browsing does not hide network activity from websites, ISPs, DNS resolvers or network operators.
- Downloaded file bytes are user-saved files; only private-mode lifecycle cleanup is applied to files created in the private download path.
- Hardware-backed/StrongBox Keystore is not guaranteed.
- Tracking protection does not imply complete tracker blocking, anonymity or a tracker count.
- GeckoView WebExtensions capability does not mean Rivo has an add-on manager or marketplace.
- QR scanning uses Google Play services' Google Code Scanner; availability depends on compatible services.

## Required security gates
1. Keystore AES-GCM round-trip, tamper, missing-key/key-loss and migration instrumentation.
2. DataStore corruption/relaunch preservation tests.
3. Site permission grant/deny/revoke, Android permission denial, notification and private isolation tests.
4. Download URL, filename, path traversal, size cap, private metadata and file cleanup tests.
5. Review resolved Gradle lockfiles, dependency graph, SBOM and OSV findings; source SBOM alone is insufficient.
6. OWASP MASVS control-by-control review with residual risks documented.
7. Physical-device validation before release.

## Release limitations
No MASVS certification, 16 KB runtime verification, physical 3–4 GB device benchmark, production signing or store release is claimed.
