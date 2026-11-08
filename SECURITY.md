# Security Baseline

OWASP MASVS is the baseline for storage, cryptography, network, platform, code, resilience and privacy. This is an implementation record, not a completed security certification.

## Implemented in Phase 1 source
- GeckoView Stable candidate pinned; no dynamic dependency versions.
- One process-scoped GeckoRuntime; one GeckoSession per tab.
- GeckoView native private mode; private tabs excluded from app-side history/bookmarks.
- Tracking protection enabled per session.
- Content, Android runtime and media permission requests denied until user-facing permission mediation is implemented.
- No JavaScript-to-native bridge and no TLS certificate error override.
- Connection security state from GeckoView ProgressDelegate.
- Omnibox accepts HTTP/HTTPS; unsupported schemes become search queries.
- Downloads accept HTTP(S) response streams, sanitize filenames, enforce a 250 MB per-file limit and write into app-specific storage.
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
