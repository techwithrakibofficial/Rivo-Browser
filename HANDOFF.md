# Handoff

## Current phase
Phase 1 — Functional Browser MVP (authorized 2026-11-08).

## Repository
https://github.com/techwithrakibofficial/Rivo-Browser
Branch: main
Latest source checkpoint before this handoff update: 9df07aa84f2b3e94c8a383cea486ced09652b8e5 (2026-11-08 10:17:45 UTC).

## Implemented in source
- GeckoView Stable candidate with process-scoped GeckoRuntime and per-tab GeckoSession.
- Activity-safe GeckoView lifecycle; only the selected foreground session is active, and sessions deactivate while the Activity is stopped.
- Android system Back navigates page history when available.
- URL/search omnibox; back, forward, reload and stop.
- Multi-tab management, inactive session handling and an eight-tab cap.
- Native Gecko private sessions; private tabs are excluded from app-side history/bookmarks and tab restoration.
- Regular HTTP(S) tabs persist and restore after process recreation.
- Tracking protection per session; HTTPS notification and persistent-storage requests have explicit Allow/Deny prompts.
- Unsupported content, Android runtime and media permissions remain denied.
- Engine-reported TLS connection status.
- Local bookmarks, history, restorable-tab metadata and download metadata are persisted; sensitive metadata uses Android Keystore AES-256-GCM and legacy plaintext values migrate on read.
- Confirmation-gated downloads, temporary-file streaming, atomic finalization, 250 MB cap, app-specific storage and scoped FileProvider open/share.
- Centralized Dark/Light/Premium Glass semantic tokens and secondary Quick Appearance Picker.
- ARM64/ARMv7 APK splits; R8/resource shrinking and unsigned QA AAB build are configured.

## Verified
- GitHub Actions run #143 (42461668698) passed assembleDebug, lint, unit tests, ABI size reporting and ARM64/ARMv7 artifact uploads.
- Run #143 tested commit 89139a036bb36829e52681df9eae7c47ae77197e, not the later lifecycle, tab-restoration, R8/AAB or encryption changes.
- The current workflow includes a minified release AAB step which was not present in run #143's recorded job steps.
- Debug APK sizes from run #143: ARM64 208,688,414 bytes (~199.0 MiB); ARMv7 169,493,492 bytes (~161.7 MiB).
- No physical-device install, force-stop/relaunch test or performance benchmark is claimed.
- No CI run or commit status was returned for latest source commit 9df07aa during this recovery.

## Review finding — release blocker
LocalDataCipher currently converts encrypted-data decryption failures into an empty string. BrowserStorage read-modify-write paths consume that fallback; after ciphertext corruption or Keystore-key loss they may overwrite previously saved bookmarks/history instead of preserving the original data. Fix the failure semantics and add regression coverage before release.

## Remaining
1. Verify the latest source with the complete Android CI workflow, including the minified release AAB.
2. Fix encrypted-metadata failure handling so a decrypt failure cannot silently overwrite existing records; add regression tests for legacy migration, tampering and unavailable keys.
3. Validate regular-tab restoration and private-tab exclusion with force-stop/relaunch.
4. Investigate GeckoView binary footprint and safely optimize release packaging.
5. Install ABI builds on physical devices and benchmark a representative 3–4 GB device.
6. Add per-site permission management/revoke UI.
7. Complete accessibility, dependency vulnerability and OWASP MASVS reviews.

## Known limitations
- Private mode does not hide traffic from websites, ISPs, DNS resolvers or network operators.
- Downloaded files remain in app-specific storage and are not encrypted by the metadata cipher or erased by private mode.
- Site permission mediation covers HTTPS notifications and persistent storage only; hardware/media permissions remain denied.
- GeckoView remains the provisional MVP engine; final selection awaits controlled WebView/GeckoView benchmarking.
- Phase 2/3 features have not started.