# Project State

Updated: 2026-11-08

## Current phase
Phase 1 — Functional Browser MVP, authorized by user on 2026-11-08.

## Repository
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Branch: main
- Phase 2 and Phase 3 have not started.

## Phase 0
- Android foundation, modular engine probes and comparison protocol committed.
- Physical-device WebView vs GeckoView benchmark remains outstanding.
- Final engine decision remains open. GeckoView is the provisional Phase 1 implementation candidate, not a measured final winner.

## Phase 1 source implemented
- Process-scoped GeckoRuntime provider; one GeckoSession per tab.
- GeckoView is created with the current Activity context and detached on composition lifecycle changes; sessions remain in ViewModel across Activity configuration changes.
- Activity onStart/onStop now gates session activation: only the selected tab is active while visible; all tabs are deactivated/unfocused while backgrounded.
- Android system Back now navigates browser history when available.
- Omnibox resolves HTTP/HTTPS URLs and plain-text searches; unsupported schemes are sent to search.
- Back, forward, reload and stop; Gecko navigation capability state comes from delegate callbacks.
- Multiple tabs with active/inactive session handling and an 8-tab cap.
- Native Gecko private sessions; private pages are excluded from app-side history/bookmarks.
- Tracking protection enabled per session.
- Content, Android runtime and media permissions denied until a user-facing permission mediation flow exists.
- Connection security indicator uses GeckoView security callbacks.
- Local DataStore bookmarks (max 500), history (max 300) and persisted Dark/Light/Glass appearance.
- Downloads stream into app-specific Downloads storage, have a 250 MB per-file safety cap, and can be opened/shared through a non-exported scoped FileProvider.
- Centralized semantic colors, spacing and shape tokens; Quick Appearance Picker is in secondary menu; system-bar icon contrast follows appearance.
- URL/search resolver JUnit tests added.

## Latest source changes (CI verification pending)
- `8af4505`: pause Gecko sessions when Activity stops and reactivate selected session on start.
- `18cc890`: wire BrowserController lifecycle to MainActivity.
- `b8405ac`: route Android system Back to Gecko page history when available.
- `9951c49`, `44dabb1`, `24fe452`: update architecture, handoff and README documentation.
- These changes are committed to `main`; the new source has not yet been confirmed by a post-change CI run.

## Build verification
- GitHub Actions run #143 (`42461668698`) on commit `89139a036bb36829e52681df9eae7c47ae77197e` completed successfully on 2026-11-08.
- `:app:assembleDebug`, `:app:lintDebug`, `:app:testDebugUnitTest`, ABI size reporting and both artifact uploads all passed.
- Separate artifacts published: `rivo-browser-arm64-debug` (109,529,496-byte ZIP) and `rivo-browser-armv7-debug` (105,557,703-byte ZIP). Both are unexpired; GitHub retention expiry is 2026-11-22.
- Inspected both downloaded ZIPs: ARM64 APK `app-arm64-v8a-debug.apk` is 208,688,414 bytes (~199.0 MiB); ARMv7 APK `app-armeabi-v7a-debug.apk` is 169,493,492 bytes (~161.7 MiB).
- APK SHA-256: ARM64 `9bca287c9407d8d060903be24db0405c759aacd39828af32f643fa92d7641223`; ARMv7 `5af075f25765f209dff18f304cb0bf26803e9655f7b9bb7b71ae7b84f4ce892d`.
- ZIP archive listing confirms each APK contains native libraries for only its named ABI. No APK has been installed on a physical device in this session.

## Remaining / known limitations
- Investigate large GeckoView APK footprint and optimize packaging without compromising engine functionality or security.
- No physical device install/smoke test or 3–4 GB memory benchmark.
- Per-site permission allow/deny UI is not implemented; permissions remain denied.
- Download files are app-specific; downloads list metadata is not persisted after process death.
- Open-tab restoration after process death is not implemented.
- History/bookmarks are app-private but not separately encrypted with an app-managed key.
- Final engine selection awaits controlled WebView/GeckoView device benchmark.

## Next steps
1. Verify CI after Activity lifecycle and system Back changes.
2. Install and smoke-test on physical ARM64 and ARMv7 Android devices.
3. Measure actual per-APK size, cold/warm startup, PSS, CPU and stability on 3–4 GB devices.
4. Implement per-site permission UX and tab restoration.
5. Complete security/accessibility review before release.
