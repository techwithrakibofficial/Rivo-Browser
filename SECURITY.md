# Security Baseline

OWASP MASVS is the baseline for storage, cryptography, network, platform, code, resilience and privacy. This is an implementation record, not a completed security certification.

## Implemented in Phase 1 source
- GeckoView Stable candidate pinned; no dynamic dependency versions.
- One process-scoped GeckoRuntime; one GeckoSession per tab.
- Native Gecko private mode; private tabs excluded from app-side history/bookmarks and tab restoration.
- Tracking protection enabled per session.
- HTTPS requests for desktop notifications and persistent storage receive an explicit per-origin Allow/Deny prompt; unsupported content, runtime and media permissions remain denied.
- Permission prompts are not shown for non-HTTPS origins; private-mode decisions remain scoped to Gecko private sessions.
- No JavaScript-to-native bridge and no TLS certificate error override.
- Connection security state comes from GeckoView ProgressDelegate.
- Omnibox accepts HTTP/HTTPS; unsupported schemes become search queries.
- Downloads require explicit confirmation, accept HTTP(S) streams, sanitize filenames, enforce a 250 MB per-file limit, write temporary files and atomically finalize in app-specific storage.
- Download metadata restoration validates canonical file paths against app-specific download directories; abandoned temporary files are cleaned on startup.
- A non-exported FileProvider exposes only scoped app-specific download paths for explicit open/share actions.
- App declares INTERNET only; no broad storage, camera, microphone or location permission.
- Android backup disabled and cleartext traffic disabled in Android app policy.
- Bookmarks, history, restorable-tab metadata and download metadata are encrypted at rest with AES-256-GCM using an Android Keystore AES-256 key; each ciphertext includes a fresh randomized 12-byte IV and a 128-bit GCM authentication tag. Legacy plaintext values migrate on read.
- Theme preference remains a non-sensitive DataStore value; no account, telemetry or mandatory backend.
- Separate ARM64/ARMv7 APK split configuration.

## Known limitations and review findings
- Per-site permission allow/deny management and revoke UI is pending. Unsupported/device/media requests remain denied.
- Private mode does not hide network activity from sites, ISPs, DNS resolvers or network operators.
- User-saved download files remain on device, including downloads started from private tabs. Download file contents are not encrypted by the metadata cipher.
- Encrypted metadata decryption failures now propagate as `EncryptedDataException`; malformed JSON and malformed entries fail closed instead of being silently discarded.
- BrowserStorage uses a process-wide fail-closed write guard for integrity failures observed in the current process. The in-memory flag resets after process restart; therefore every direct replacement of existing open-tab or download metadata, and clear-history, now decrypts and validates the current value inside the DataStore edit transaction before replacing it. A missing/lost Keystore key or malformed existing value causes the edit to abort and preserves the stored ciphertext. Theme preference remains independently writable. All sensitive DataStore edit mutations are serialized with integrity-failure publication through the same process guard; read-modify-write paths authenticate and decode their target value before writing.
- AES-GCM payload parsing validates the 12-byte IV plus 16-byte authentication tag minimum. Encryption obtains a provider-generated randomized IV for each call. Decryption retrieves an existing Keystore alias only; it never creates a replacement key when the alias is missing. A missing or unusable alias and authentication/truncation failures propagate as `EncryptedDataException` and leave the original DataStore ciphertext untouched.
- JVM tests cover the fail-closed write guard. Android instrumentation sources now exercise actual Android Keystore AES-GCM round-trip/fresh-IV behavior, ciphertext tampering, truncated payload rejection, missing-key behavior, malformed bookmark/download metadata, legacy plaintext migration, encrypted bookmark/history/tab/download persistence, DataStore corruption across a DataStore reopen, and preservation of sensitive records after key loss. The CI emulator runs this instrumentation suite; the run must pass before its cases are described as verified. A real OS process kill/relaunch and hardware-backed/secure-lock-screen key invalidation remain separate physical-device checks.
- Regular-tab restoration and download-metadata persistence are implemented in source but still require CI and force-stop/relaunch verification.
- No claim of anonymity or complete tracker/ad blocking.
- Final engine selection and device security/performance validation remain pending.

## Release gates
- Verify encrypted-metadata failure handling with Android instrumentation tests on an emulator and representative physical devices before release.
- Review certificate validation, redirects, external schemes and downloads.
- Instrument tests verify FileProvider is non-exported, accepts files only from configured app Downloads roots and rejects files outside those roots.
- Instrument tests verify startup cleanup removes only direct-child `.rivo-*.part` staging files while preserving completed downloads and unrelated files.
- Add explicit per-site permission mediation and revoke controls.
- Run dependency vulnerability review and OWASP MASVS checklist.
- Verify release signing, artifact provenance, ABI packaging, release bundle size and backup policy.
- Perform physical-device privacy, lifecycle and performance testing.