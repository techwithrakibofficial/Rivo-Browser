# Security Baseline

OWASP MASVS is the baseline for storage, cryptography, network, platform, code, resilience and privacy. This is an implementation record, not a completed security certification.

## Implemented in Phase 1 source
- GeckoView Stable candidate pinned; no dynamic dependency versions.
- One process-scoped GeckoRuntime; one GeckoSession per tab.
- Native Gecko private mode; private tabs excluded from app-side history/bookmarks and tab restoration.
- Tracking protection enabled per session.
- GeckoView's PermissionDelegate explicitly mediates HTTPS site-notification and autoplay requests with per-origin Allow/Deny prompts. Site notification approval also requires Android 13+ POST_NOTIFICATIONS runtime approval; a Gecko WebNotificationDelegate posts notifications through an app notification channel and routes notification clicks back to Gecko.
- Persistent-storage requests are denied because GeckoView documents that a granted persistent-storage permission cannot be revoked. Geolocation, XR, DRM, tracking-protection exceptions, third-party storage access, local network/device access and other unsupported content permissions default-deny.
- Android runtime permission requests for location, camera and microphone are rejected. The manifest intentionally does not declare location, CAMERA or RECORD_AUDIO permissions; media capture requests are rejected.
- The site-permission manager reads real records from GeckoView StorageController.getAllPermissions(), excludes private-mode records, and resets supported records with setPermission(VALUE_PROMPT). Persistent-storage records, if present from an earlier build, are displayed as non-revocable.
- Private-mode permission decisions remain in GeckoView's private permission context; the app never calls setPrivateBrowsingPermanentPermission and does not surface private records in the persistent site-permission list. Website notifications are denied in private tabs to avoid creating OS-level notification artifacts outside the private session.
- Tracking protection is enabled per GeckoSession. Rivo reads the active page's GeckoView tracking-exception permission from onLocationChange and explains when a site exception applies. It does not invent a blocked-tracker count or claim complete tracker blocking.
- No JavaScript-to-native bridge and no TLS certificate error override.
- Connection security state comes from GeckoView ProgressDelegate.
- Omnibox accepts HTTP/HTTPS; unsupported schemes become search queries.
- Downloads require explicit confirmation, accept HTTP(S) streams, sanitize filenames, enforce a 250 MB per-file limit, write temporary files and atomically finalize in app-specific storage.
- Download metadata restoration validates canonical file paths against app-specific download directories; abandoned temporary files are cleaned on startup.
- A non-exported FileProvider exposes only scoped app-specific download paths for explicit open/share actions.
- App declares INTERNET and POST_NOTIFICATIONS only; no broad storage, camera, microphone or location permission.
- Android backup disabled and cleartext traffic disabled in Android app policy.
- Bookmarks, history, restorable-tab metadata and download metadata are encrypted at rest with AES-256-GCM using an Android Keystore key; legacy plaintext values migrate on read.
- Theme preference remains a non-sensitive DataStore value; no account, telemetry or mandatory backend.
- Separate ARM64/ARMv7 APK split configuration.

## Known limitations and review findings
- Location, camera and microphone access remain unsupported and denied until a separately reviewed Android runtime permission flow is implemented. Persistent storage remains denied because GeckoView does not support revoking it after grant.
- Private mode does not hide network activity or the device IP address from websites, ISPs, DNS resolvers or network operators.
- Downloaded file bytes remain on the device, including downloads started from private tabs; private mode does not automatically delete user-saved files. New private-tab download entries are removed from the in-memory list when their originating private tab closes and are excluded from persistent browser download metadata, but the actual file remains in app-specific storage. Download file contents are not encrypted by the metadata cipher.
- Encrypted metadata decryption failures now propagate as `EncryptedDataException`; malformed JSON and malformed entries fail closed instead of being silently discarded.
- BrowserStorage uses a process-wide fail-closed write guard for integrity failures observed in the current process. The in-memory flag resets after process restart; therefore every direct replacement of existing open-tab or download metadata, and clear-history, now decrypts and validates the current value inside the DataStore edit transaction before replacing it. A missing/lost Keystore key or malformed existing value causes the edit to abort and preserves the stored ciphertext. Theme preference remains independently writable. All sensitive DataStore edit mutations are serialized with integrity-failure publication through the same process guard; read-modify-write paths authenticate and decode their target value before writing.
- AES-GCM payload parsing validates the 12-byte IV plus 16-byte authentication tag minimum. The cipher uses a fresh provider-generated IV for each encryption.
- JVM tests cover fail-closed guard behavior and download URL, filename, restored-path and private-metadata policies. Android instrumentation tests are added for AES-GCM round-trip, AES-256 key size, ciphertext tampering and missing-key failure without replacement. These tests must pass on CI/device before those runtime behaviors are considered verified; DataStore corruption/relaunch and legacy migration instrumentation remain outstanding.
- Regular-tab restoration and download-metadata persistence are implemented in source but still require CI and force-stop/relaunch verification.
- No verified per-page blocked-tracker count is currently surfaced; tracking protection status is taken from the active GeckoSession setting. No claim of anonymity or complete tracker/ad blocking.
- Final engine selection and device security/performance validation remain pending.

## Release gates
- Verify encrypted-metadata failure handling with Android instrumentation tests before release.
- Review certificate validation, redirects, external schemes and downloads.
- Test FileProvider URI grants and path traversal resistance.
- Validate per-site permission mediation/revocation, notification runtime permission and private-session isolation on device.
- Run dependency vulnerability review and OWASP MASVS checklist.
- Verify release signing, artifact provenance, ABI packaging, release bundle size and backup policy.
- Perform physical-device privacy, lifecycle and performance testing.

## Privacy Intelligence Center

The Privacy Intelligence Center reports only state available from the active GeckoView session and stored Gecko permission records:
- Tracking protection status reads the active session setting. A site exception is shown only when GeckoView reports the corresponding tracking permission for the active page.
- The UI does not show a blocked-tracker count because no verified per-page count is exposed by the current integration.
- Site permissions are loaded from GeckoView `StorageController.getAllPermissions()`. Private records and prompt/default entries are excluded. Revocation resets the original Gecko `ContentPermission` to `VALUE_PROMPT` and verifies the stored result. Persistent-storage grants are shown as non-revocable.
- Only HTTPS desktop notifications and audible/inaudible autoplay requests are eligible for an explicit site prompt. Private-mode website notifications are denied; location, camera, microphone, persistent storage, XR, DRM, tracking exceptions and unsupported permissions default-deny.
- The panel discloses encryption coverage, private-mode persistence boundaries and the fact that downloaded file contents are not encrypted by the metadata cipher.

## OWASP MASVS coverage map (source-level, not certification)

| MASVS group | Current evidence | Remaining verification / limitation |
|---|---|---|
| MASVS-STORAGE | Sensitive browser metadata uses AES-256-GCM with an Android Keystore key; app backup is disabled; download metadata paths are validated against app-specific roots; private tabs are not restored and private download metadata is not persisted | Run instrumentation and device tests; downloaded file contents are not encrypted |
| MASVS-CRYPTO | AES/GCM/NoPadding, 256-bit key, provider-generated IV, 128-bit GCM tag; decrypt requires the existing key and fails closed if it is missing | Verify on supported Android devices; hardware-backed/StrongBox key storage is not guaranteed or claimed |
| MASVS-NETWORK | Cleartext traffic disabled; no certificate-error override; GeckoView reports connection security state | Full TLS/redirect/deep-link review and network testing remain pending |
| MASVS-PLATFORM | Native GeckoView sessions, denied unsupported Android/media permission callbacks, non-exported scoped FileProvider with read grants | Device validation of permission and URI-grant flows remains pending |
| MASVS-PRIVACY | Private browsing excludes app-side history/bookmarks/tab restoration; private permission decisions are not listed as persistent; user-facing privacy disclosures; no mandatory telemetry/backend | Private browsing does not hide network activity or erase downloaded files; no anonymity/fingerprinting-resistance claim |
| MASVS-CODE / RESILIENCE | Pinned dependency versions and CI build/lint/test gates | Dependency/SBOM vulnerability review, tamper analysis and full resilience testing remain pending |

A MASVS mapping is a gap-tracking aid only. It is not a MASVS assessment, verification report, certification or claim of compliance.
