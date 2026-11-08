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
- Bookmarks, history, restorable-tab metadata and download metadata are encrypted at rest with AES-256-GCM using an Android Keystore key; legacy plaintext values migrate on read.
- Theme preference remains a non-sensitive DataStore value; no account, telemetry or mandatory backend.
- Separate ARM64/ARMv7 APK split configuration.

## Known limitations and review findings
- Per-site permission allow/deny management and revoke UI is pending. Unsupported/device/media requests remain denied.
- Private mode does not hide network activity from sites, ISPs, DNS resolvers or network operators.
- User-saved download files remain on device, including downloads started from private tabs. Download file contents are not encrypted by the metadata cipher.
- Encrypted metadata decryption failures now propagate as `EncryptedDataException`; malformed JSON and malformed entries fail closed instead of being silently discarded.
- BrowserStorage uses a process-wide fail-closed write guard for integrity failures observed in the current process. The in-memory flag resets after process restart; therefore every direct replacement of existing open-tab or download metadata, and clear-history, now decrypts and validates the current value inside the DataStore edit transaction before replacing it. A missing/lost Keystore key or malformed existing value causes the edit to abort and preserves the stored ciphertext. Theme preference remains independently writable. All sensitive DataStore edit mutations are serialized with integrity-failure publication through the same process guard; read-modify-write paths authenticate and decode their target value before writing.
- AES-GCM payload parsing validates the 12-byte IV plus 16-byte authentication tag minimum. The cipher uses a fresh provider-generated IV for each encryption.
- JVM tests cover fail-closed guard behavior and verify that replacement is rejected when existing-value authentication or metadata validation fails. These tests exercise the validation gate with controlled callbacks; they do not prove Android Keystore cryptography or DataStore transaction behavior. Android Keystore-backed AES-GCM round-trip, tampering, key-loss, and legacy migration tests still require Android instrumentation; do not treat those cases as verified.
- Regular-tab restoration and download-metadata persistence are implemented in source; integrated Main CI run #246 passed, but force-stop/relaunch behavior still requires physical-device verification.
- No claim of anonymity or complete tracker/ad blocking.
- Final engine selection and device security/performance validation remain pending.

## CI verification checkpoint

Integrated Main commit `84303588c2560db55bfb7ae2081d8f714be532e7` passed GitHub Actions run [#246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810), including assembleDebug, lintDebug, JVM unit tests and minified release bundle packaging. These checks do not replace Android Keystore/DataStore instrumentation, physical-device tests, dependency review or MASVS assessment.

## Release gates
- Verify encrypted-metadata failure handling with Android instrumentation tests before release.
- Review certificate validation, redirects, external schemes and downloads.
- Test FileProvider URI grants and path traversal resistance.
- Add explicit per-site permission mediation and revoke controls.
- Run dependency vulnerability review and OWASP MASVS checklist.
- Verify release signing, artifact provenance, ABI packaging, release bundle size and backup policy.
- Perform physical-device privacy, lifecycle and performance testing.

## Android Keystore and storage instrumentation coverage
The API 35 emulator suite exercises AES-256-GCM round-trip with fresh randomized ciphertext, tampering and truncated payload rejection, missing-key fail-closed behavior, legacy plaintext migration, encrypted bookmark/history/tab/download metadata persistence, DataStore corruption preservation across store reopen, and preservation of encrypted records after key loss. It also checks Rivo-owned partial-download cleanup and FileProvider export/path scope. These emulator tests do not prove hardware-backed Keystore behavior, secure-lock-screen invalidation, OS force-stop/relaunch, or MASVS certification; those remain separate release gates.


## Site permissions and privacy controls
- GeckoView `PermissionDelegate` explicitly mediates only HTTPS desktop notifications and autoplay requests; all other site capabilities default-deny.
- Website notifications additionally require Android 13+ `POST_NOTIFICATIONS` approval. Private-tab website notifications are denied so OS notification artifacts do not escape the private session.
- Location, camera, microphone, persistent storage, XR, DRM, tracking exceptions, third-party storage, local-device/local-network access and unsupported capabilities are denied by default. The manifest does not request location, camera or microphone.
- The Privacy & site permissions panel reads actual normal-mode GeckoView permission records, excludes private records, and resets only revocable permissions. Persistent storage is explicitly shown as non-revocable; Rivo does not claim it can revoke that Gecko permission.
- Tracking status reflects the actual active GeckoSession setting and active-page exception callback. No fabricated blocked-tracker count is shown.
- Notification click receiver is non-exported and routes clicks to Gecko's WebNotification callback.
