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
- GeckoView is created with the current Activity context and detached on composition lifecycle changes; sessions remain in ViewModel.
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

## Build verification
- GitHub Actions run 42460649170 completed successfully: assembleDebug, Android lint, unit tests and artifact upload.
- ABI split build configuration was then added. A follow-up CI run is pending for the updated separate ARM64/ARMv7 artifact workflow.
- The previous combined debug artifact ZIP was 215,087,168 bytes; individual split APK sizes have not yet been independently measured.
- No APK has been installed on a physical device in this session.

## Remaining / known limitations
- Latest CI after separate ABI artifact upload changes must be confirmed.
- No physical device install/smoke test or 3–4 GB memory benchmark.
- Per-site permission allow/deny UI is not implemented; permissions remain denied.
- Download files are app-specific; downloads list metadata is not persisted after process death.
- Open-tab restoration after process death is not implemented.
- History/bookmarks are app-private but not separately encrypted with an app-managed key.
- Final engine selection awaits controlled WebView/GeckoView device benchmark.

## Next steps
1. Verify CI builds and publishes both ARM64 and ARMv7 split APK artifacts.
2. Install and smoke-test on physical ARM64 and ARMv7 Android devices.
3. Measure actual per-APK size, cold/warm startup, PSS, CPU and stability on 3–4 GB devices.
4. Implement per-site permission UX and tab restoration.
5. Complete security/accessibility review before release.
