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


## Account B source-level MASVS assessment — 2026-11-09

Assessment scope: current Main plus Account B PR #36 candidate. This is a developer source review against the MASVS control groups, not an independent penetration test, formal verification or certification.

| MASVS group | Evidence observed | Status / residual risk |
|---|---|---|
| MASVS-STORAGE | DataStore metadata encrypted with Keystore AES-256-GCM; backup disabled; fail-closed parse/decrypt; private tabs omitted from history/bookmark/restore; private download index excluded; FileProvider is non-exported and scoped to app download roots | Source controls present. Verify migration, process death and real device Keystore behavior. Downloaded file bytes themselves are not encrypted by metadata cipher. |
| MASVS-CRYPTO | AES/GCM/NoPadding, 256-bit AES key, randomized 12-byte IV, 128-bit GCM tag, AndroidKeyStore; decrypt uses existing key only | Strong primitives selected. Hardware backing/StrongBox is not guaranteed. No per-record AAD binding is currently documented; consider versioned AAD migration before treating field-substitution resistance as complete. |
| MASVS-NETWORK | Android cleartext traffic disabled; GeckoView default TLS validation retained; download URL requires HTTP(S), host and no user-info; site permission prompts limited to HTTPS | No custom certificate bypass found in reviewed paths. HTTP downloads remain allowed by explicit product policy; no certificate-pinning claim. Verify redirects and TLS failures on device. |
| MASVS-PLATFORM | Only INTERNET on Main; PR #36 adds POST_NOTIFICATIONS; Android/media permission callback rejects; FileProvider exported=false; notification receiver exported=false and immutable PendingIntent | Camera, microphone, location, broad storage are not requested. Runtime notification consent and notification click flow require device verification. |
| MASVS-CODE | Kotlin/JVM tests, API 35 Keystore/DataStore instrumentation, lint/build CI, OSV Scanner PR scan, SPDX source SBOM; PR #36 adds Gecko notification policy test | Test coverage is not a full security test. Dependency graph workflow added; transitive graph submission only runs after Main push/manual dispatch. |
| MASVS-RESILIENCE | Release minification/resource shrinking; no remote Gecko debugging or production console exposure | No anti-tamper/obfuscation effectiveness claim. Browser content remains exposed to device owner/rooted-device threat models. |
| MASVS-PRIVACY | Native private sessions, private history/bookmark/tab restore exclusions, private download metadata exclusion and close-tab in-memory cleanup, private web notifications explicitly denied | Private mode does not anonymize network traffic and does not delete user-saved download bytes. Process-death behavior and UI visibility require device validation. |

### Security issues addressed in PR #36
- Website permission prompts are restricted to HTTPS origins and explicitly mediated Gecko notification/autoplay permission types; unsupported and Android hardware/media requests default-deny.
- Persistent-storage grants are not represented as revocable; permission inventory excludes private records and prompt/default states.
- Android notification permission is requested only after the user accepts a site notification prompt. Notifications from private browsing, non-HTTPS, malformed or user-info-bearing origins are dismissed before Android notification UI.
- Notification click PendingIntent is explicit, immutable and targets a non-exported receiver.
- Download URL validation rejects unsupported schemes, missing hosts and URL user-info. Restored download files must resolve directly under canonical app-specific download roots and remain within the existing size cap.
- New private-tab download metadata is not persisted and the in-memory item is removed when its private tab closes. Actual saved file bytes remain on-device.
- One-time migration clears ambiguous legacy download-index metadata after authenticating and validating it; user files are preserved. Corrupt data or unavailable keys abort the migration.
- Fixed migration ordering so a new-format download metadata write atomically marks the migration complete; otherwise the first subsequent load could incorrectly clear a just-written record.

### Supply-chain coverage
- OSV-Scanner remains the PR source scan. The latest successful PR scan log explicitly reported “No package sources found” and “No lockfiles found”; therefore the green workflow means the scanner job completed, not that Gradle runtime dependencies were scanned or found vulnerability-free.
- SPDX source SBOM is source-level and does not prove a complete resolved dependency inventory.
- Added .github/workflows/gradle-dependency-graph.yml: PRs generate a resolved Gradle dependency graph artifact without write permission; Main pushes/manual runs submit the resolved graph to GitHub Dependency Graph.
- Added .github/workflows/gradle-dependency-graph.yml to generate resolved Gradle dependency graph artifacts for PRs and Main pushes using read-only permissions. Repository Dependency Graph is disabled in repository settings, so GitHub Dependency Review/submission is not available; the workflow intentionally does not request write permissions or fail CI on that setting. The owner must enable the repository feature before automated GitHub advisory review can be restored.
- No Gradle lockfiles/checksum verification metadata are currently committed. Dependency graph submission improves resolved transitive visibility but is not equivalent to dependency locking or reproducible verification.
