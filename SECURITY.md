# Security Baseline

OWASP MASVS is the baseline for storage, cryptography, network, platform, code, resilience and privacy. This document records implemented controls and remaining work; it is not a completed security certification.

## Implemented in Phase 1 source
- GeckoView Stable candidate pinned; no dynamic dependency versions.
- One process-scoped GeckoRuntime and explicit per-tab GeckoSession lifecycle.
- GeckoView native private mode for private tabs.
- Private tabs do not write app-side browsing history or bookmarks.
- GeckoView tracking protection enabled per session.
- GeckoView permission delegate denies content permissions, Android runtime permission requests and media permission requests until user-facing controls are implemented.
- No JavaScript-to-native bridge.
- No TLS certificate error override.
- Connection security state comes from GeckoView's ProgressDelegate.
- Omnibox accepts HTTP/HTTPS URLs only; unsupported schemes become search queries.
- Downloads accept HTTP(S) response streams only, sanitize filenames and write into app-specific storage.
- App INTERNET permission only; no broad storage, camera, microphone or location permission declared.
- Android backup disabled.
- Cleartext traffic disabled in Android application policy.
- Local DataStore only; no account, telemetry or mandatory backend.

## Known limitations
- Permission UX is deny-by-default; per-site allow/deny UI is not yet implemented.
- Download files are app-specific; export/share/open workflow is pending.
- History and bookmarks are app-private local data but not separately encrypted with an app-managed encryption key. Device lock and Android sandbox are assumed; evaluate encrypted-at-rest needs before release.
- Private browsing does not hide traffic from websites, network operators, ISPs or DNS resolvers. Downloads explicitly saved by the user remain on device.
- No claim of anonymity or complete tracker/ad blocking.
- Engine final selection and device security/performance validation remain pending.

## Release gates
- Review certificate validation and redirect handling.
- Verify no unsafe external intent or URI handling.
- Test permission denial paths and downloads.
- Run dependency vulnerability review and MASVS checklist.
- Verify release signing, artifact provenance and backup policy.
