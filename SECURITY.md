# Security Baseline

OWASP MASVS is the assessment baseline for storage, cryptography, network, platform, code, resilience and privacy. This document is not a certification or completed independent security assessment.

## Implemented in Main source
- GeckoView runtime with one session per tab.
- Native Gecko private mode; private tabs excluded from app-side history/bookmarks/restoration.
- Per-session tracking protection.
- No JavaScript-to-native bridge and no TLS certificate validation override.
- HTTP/HTTPS URL handling; unsupported schemes resolve as search.
- HTTP(S)-only confirmed downloads, filename sanitization, 250 MB per-file cap, temporary files, atomic finalization and app-specific storage.
- Scoped, non-exported FileProvider for explicit open/share.
- Android backup disabled, cleartext traffic disabled and no broad storage/camera/microphone/location permissions.
- Browser metadata uses AES-256-GCM with an Android Keystore key; JVM/source-level safeguards exist. On-device Keystore behavior still needs instrumentation verification.
- No account, telemetry or mandatory backend.

## Open PR work (not yet Main-integrated)
- PR #10: GeckoView site notification/autoplay mediation, default-deny unsupported/device/media requests, site permission inventory/revocation constraints and notification flow.
- PR #21: Privacy Intelligence Center, fail-closed missing-key decrypt, private-download metadata exclusion, download URL/filename/path hardening and Keystore instrumentation tests. PR #21 is stacked on #10.
- PR #20: storage hardening, API 35 instrumentation and benchmark/release validation workflow changes.

## Known limitations
- Private browsing does not hide network activity from websites, ISPs, DNS resolvers or network operators.
- User-saved downloaded file contents are not encrypted by the metadata cipher and are not automatically erased by private mode.
- Hardware-backed/StrongBox Keystore is not guaranteed.
- Tracking protection does not justify claims of complete tracker blocking, tracker counts or anonymity.
- Unsupported hardware/media permissions remain denied unless explicitly and safely supported.
- WebExtensions support is not established.
- No MASVS certification is claimed.

## Required security gates
1. Keystore AES-GCM round-trip, tamper, missing-key/key-loss and migration instrumentation.
2. DataStore corruption/relaunch preservation tests.
3. Permission grant/deny/revoke and notification flow tests, including private isolation.
4. Download path traversal, URL, filename, size, restore and private metadata tests.
5. Dependency vulnerability/SBOM review.
6. OWASP MASVS control-by-control source review and documented residual risks.
7. Physical-device validation before release.


## Supply-chain automation (PR #34)
- CI adds the official OSV-Scanner reusable pull-request scan and scheduled/full scan workflow, plus an SPDX JSON source SBOM artifact.
- A green source scan/SBOM is not proof that all resolved/transitive Gradle dependencies were inventoried: this repository does not yet commit Gradle dependency lockfiles.
- Review scanner findings and SBOM contents before making any vulnerability-free or complete-inventory claim. These workflows are security signals, not MASVS certification.

## Account B security integration candidate — 2026-11-09

- Worker branch: `worker/account-b-security-qa-2026-11-09`; current work is isolated from Main and Account A branches.
- GeckoView permission requests are mediated only for secure HTTPS notifications and autoplay; unsupported/device/media capabilities default-deny. Private-mode website notifications are denied.
- Normal-mode permission inventory is sourced from GeckoView StorageController. Private records are filtered; persistent-storage grants are not presented as revocable.
- Website notification delivery uses an app notification channel and a non-exported receiver. Android 13+ runtime notification consent is a separate required gate.
- Download URL validation rejects unsupported schemes, missing hosts and URL user-info. Restored files must be direct children of canonical app-specific roots and within the existing size cap.
- New private-tab download metadata is excluded from persistent browser download history and its in-memory entry is removed when the tab closes. Downloaded file bytes remain user-saved on-device.
- One-time migration clears ambiguous legacy browser download-history metadata, after authenticating and validating the old record; actual downloaded files are preserved. Key-loss/corruption aborts migration.
- Main's existing AES-256-GCM/Keystore, DataStore fail-closed, atomic download writer and 16 KB build checks are reused, not duplicated.
- No MASVS certification, physical-device test, 16 KB runtime test, vulnerability-free claim or production signing is claimed.
