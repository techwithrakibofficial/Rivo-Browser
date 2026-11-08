# Project State

Updated: 2026-11-08

## Current phase
Phase 1 — Functional Browser MVP, authorized by user on 2026-11-08.

## Repository
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Branch: main
- Latest source HEAD before this documentation refresh: 9df07aa84f2b3e94c8a383cea486ced09652b8e5.
- Phase 2 and Phase 3 have not started.

## Phase 0
- Android foundation, modular engine probes and comparison protocol committed.
- Physical-device WebView vs GeckoView benchmark remains outstanding.
- Final engine decision remains open. GeckoView is the provisional Phase 1 implementation candidate, not a measured final winner.

## Phase 1 source implemented
- Process-scoped GeckoRuntime provider; one GeckoSession per tab.
- GeckoView is created with the current Activity context and detached on composition lifecycle changes; sessions remain in ViewModel across Activity configuration changes.
- Activity onStart/onStop gates session activation: only the selected tab is active while visible; all tabs are deactivated/unfocused while backgrounded.
- Normal HTTP(S) tabs are persisted locally and restored after process recreation (up to eight); private tabs are excluded from serialization.
- Android system Back navigates browser history when available.
- Omnibox resolves HTTP/HTTPS URLs and plain-text searches; unsupported schemes are sent to search.
- Back, forward, reload and stop; Gecko navigation capability state comes from delegate callbacks.
- Multiple tabs with active/inactive session handling and an 8-tab cap.
- Native Gecko private sessions; private pages are excluded from app-side history/bookmarks.
- Tracking protection enabled per session.
- HTTPS notification and persistent-storage requests use an explicit per-origin Allow/Deny prompt; unsupported content, Android runtime and media permissions remain denied.
- Connection security indicator uses GeckoView security callbacks.
- Local DataStore bookmarks (max 500), history (max 300), restorable tabs and download metadata; sensitive metadata uses AES-256-GCM with an Android Keystore key and migrates legacy plaintext on read. Appearance persists separately.
- Downloads require explicit confirmation, stream into app-specific storage through temporary files, enforce a 250 MB per-file cap, finalize atomically and can be opened/shared through a non-exported scoped FileProvider.
- Saved download metadata is persisted and restored only when the referenced file resolves inside app-specific Downloads directories; interrupted .part files are cleaned at startup.
- Centralized semantic colors, spacing and shape tokens; Quick Appearance Picker is in the secondary menu; system-bar icon contrast follows appearance.
- URL/search resolver and safe-restorable-tab policy unit tests are present in source.

## Latest source changes — CI verification pending
- Activity lifecycle/session deactivation and system Back integration.
- Regular-tab persistence/restoration; private tabs remain excluded.
- R8 code shrinking/resource shrinking and unsigned QA AAB build configuration.
- Android Keystore AES-256-GCM encryption for bookmarks, history, restorable tabs and download metadata.
- Legacy plaintext migration on read, plus handling for malformed/unavailable encrypted data.
- Latest source commit before this documentation update: 9df07aa84f2b3e94c8a383cea486ced09652b8e5 (2026-11-08 10:17:45 UTC).

## Build verification
- Last verified GitHub Actions run #143 (42461668698) completed successfully on commit 89139a036bb36829e52681df9eae7c47ae77197e.
- That run passed assembleDebug, lintDebug, unit tests, ABI size reporting and separate ARM64/ARMv7 artifact uploads.
- Run #143 did not test the later lifecycle, tab-restoration or encryption commits; its recorded job steps did not include the current workflow's minified release AAB step.
- Debug APK sizes from run #143: ARM64 208,688,414 bytes (~199.0 MiB), ARMv7 169,493,492 bytes (~161.7 MiB). These are not release-size measurements.
- No physical-device install, smoke test or performance benchmark has been completed.
- No CI run or commit status was returned for source HEAD 9df07aa during recovery.

## Remaining / known limitations
- Run the full CI workflow against the latest source, including the minified release AAB build.
- LocalDataCipher currently converts encrypted-data decryption failures into an empty string. BrowserStorage read-modify-write paths can overwrite previously saved records after ciphertext corruption or Keystore-key loss. Treat this as a release blocker; fix failure semantics and add regression coverage.
- Process-death tab restoration is implemented in source but still needs force-stop/relaunch validation.
- Investigate the large GeckoView APK footprint and optimize packaging without removing required engine components.
- No physical-device install/smoke test or 3–4 GB memory benchmark.
- Permission mediation currently covers HTTPS notifications and persistent storage only; geolocation, camera, microphone, XR, DRM and other unsupported requests remain denied. Per-site permission management/revoke UI is pending.
- Download file contents are not encrypted at rest; they remain in app-specific storage for explicit open/share. Private mode does not erase user-saved downloads.
- Final engine selection awaits controlled WebView/GeckoView device benchmark.
- Accessibility, dependency vulnerability and MASVS reviews are incomplete.

## Exact next steps
1. Verify the current source with the complete Android CI workflow, including debug build, lint, unit tests and minified release AAB.
2. Fix encrypted-metadata failure handling so failed decryption cannot silently overwrite existing records; add regression coverage.
3. Install and smoke-test on physical ARM64 and ARMv7 Android devices.
4. Measure actual release artifact size, cold/warm startup, PSS, CPU and stability on 3–4 GB devices.
5. Implement per-site permission UX and revoke controls.
6. Complete accessibility and MASVS review before release.