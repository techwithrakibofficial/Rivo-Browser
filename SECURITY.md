# Security Baseline

OWASP MASVS is the baseline for storage, cryptography, network, platform, code, resilience and privacy. This is an implementation record, not a completed security certification.

## Implemented in Phase 1 source
- GeckoView Stable candidate pinned; no dynamic dependency versions.
- One process-scoped GeckoRuntime; one GeckoSession per tab.
- GeckoView native private mode; private tabs excluded from app-side history/bookmarks.
- Tracking protection enabled per session.
- HTTPS site requests for desktop notifications and persistent storage are shown in an explicit per-origin Allow/Deny prompt; GeckoView stores the resulting content permission.
- Unsupported content permissions, Android runtime permissions (location/camera/microphone) and media capture requests are denied by default. No runtime hardware permissions are declared in the manifest.
- Permission prompts are not shown for non-HTTPS origins; private-mode decisions remain scoped to Gecko's private session.
- No JavaScript-to-native bridge and no TLS certificate error override.
- Connection security state from GeckoView ProgressDelegate.
- Omnibox accepts HTTP/HTTPS; unsupported schemes become search queries.
- Downloads require an explicit user confirmation, accept HTTP(S) response streams, sanitize filenames, enforce a 250 MB per-file limit, write to a temporary file and atomically finalize in app-specific storage.
- Download metadata restoration validates canonical file paths against app-specific download directories; abandoned temporary `.part` files are cleaned on startup.
- A non-exported FileProvider exposes only scoped app-specific download paths for explicit open/share actions.
- App declares INTERNET only; no broad storage, camera, microphone or location permission.
- Android backup disabled and cleartext traffic disabled in Android app policy.
- Local DataStore only; no account, telemetry or mandatory backend.
- Separate ARM64/ARMv7 APK split configuration.

## Known limitations
- Per-site permission allow/deny UI is pending; permissions remain denied.
- Private mode does not hide network activity from sites, ISPs, DNS resolvers or network operators.
- Downloads explicitly saved by the user remain on device, including downloads started from private tabs.
- History/bookmarks are app-private local data but are not separately encrypted with an app-managed encryption key.
- No claim of anonymity or complete tracker/ad blocking.
- Process-death tab restoration and download metadata persistence are not implemented.
- Final engine selection and device security/performance validation remain pending.

## Release gates
- Review certificate validation, redirects, external schemes and downloads.
- Test FileProvider URI grants and path traversal resistance.
- Add explicit per-site permission mediation.
- Run dependency vulnerability review and MASVS checklist.
- Verify release signing, artifact provenance, ABI packaging and backup policy.
