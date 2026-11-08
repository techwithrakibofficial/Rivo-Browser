# Project State

Updated: 2026-11-08 12:44 UTC

## Current phase
Phase 1 — Functional Browser MVP. Phase 0 foundation is in place; final engine selection remains provisional pending physical-device benchmarking. Phase 2 and Phase 3 have not started.

## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: `main`
- Integrated HEAD: `1683ffb57dda1d1a9d15f85e892cb8139b06d23c`
- PR #1, #2 and #3 are merged; no open PRs were found at this checkpoint.
- Worker branches were integrated using squash merges. Their changes are now in main; do not reimplement them.
- Main CI run #244 (https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481557821) was running against this HEAD during this update. Debug assembly passed; lint was in progress; unit tests, release AAB and artifact steps were pending. Do not report integrated CI as passed until run #244 completes.

## Integrated work
### Security QA — PR #1
- Encrypted metadata decryption/format errors propagate instead of silently becoming empty data.
- Sensitive metadata writes use a process-wide fail-closed guard.
- Direct tab/download replacement and clear-history authenticate and validate existing values inside DataStore edits before overwriting.
- Malformed saved records are rejected rather than silently dropped.
- AES-GCM minimum payload size accepts the valid 12-byte IV + 16-byte tag case.
- JVM tests cover guard behavior and validation gates.
- Still requires Android Keystore instrumentation and DataStore corruption/relaunch tests; JVM callback tests do not prove on-device cryptography or transaction behavior.

### Android engine fixes — PR #2
- Restored selected tab is selected by saved record index, not URL equality, so duplicate-URL tabs restore independently.
- Omnibox preserves text while the user is editing despite Gecko location callbacks.
- History list no longer uses URL as a unique LazyColumn key.
- Internationalized domain names are normalized to IDNA ASCII.
- JVM regression tests added. Force-stop/relaunch and physical-device validation remain outstanding.

### UI/UX audit and design documentation — PR #3
- Three-theme design contract and appearance-mode documentation expanded.
- Premium Glass accurately specified as translucent surfaces without real-time blur.
- Source-level accessibility audit and severity-ranked findings added at `docs/design/UI_UX_ACCESSIBILITY_AUDIT.md`.
- This PR is documentation-only; it does not fix UI source findings.

## Phase 1 source baseline
- Process-scoped GeckoRuntime and per-tab GeckoSession architecture.
- Activity-aware session activation/deactivation; selected foreground tab only.
- Navigation, URL/search omnibox, back/forward/reload/stop and Gecko-reported connection security.
- Up to eight tabs; native Gecko private sessions; private tabs excluded from app-side history/bookmarks/restoration.
- Tracking protection enabled per session.
- Local DataStore bookmarks (max 500), history (max 300), restorable tabs and download metadata; sensitive metadata encrypted with Android Keystore AES-256-GCM; legacy plaintext migration on read.
- Confirmation-gated HTTP(S) downloads, app-specific storage, temporary-file streaming, 250 MB cap, atomic finalization and scoped FileProvider open/share.
- Dark, Light and Premium Glass palettes; secondary-menu Quick Appearance Picker; system-bar contrast follows appearance.
- Regular HTTP(S) tab persistence/restoration, with selected-tab index fix now integrated.
- URL/search, restorable-tab policy, sensitive-write guard and IDN/tab-selection JVM tests.

## Verified versus unverified
- PR CI runs #237 (security), #240 (Android engine) and #241 (UI documentation) completed successfully.
- These PR checks are not a substitute for the integrated Main run #244.
- Integrated Main run #244 is the current validation gate; see TESTING.md for its live status.
- No physical-device install, force-stop/relaunch test, TalkBack audit or 3–4 GB performance benchmark is claimed.
- Release AAB from CI is unsigned QA output, not a production-signed release.

## Known issues and release blockers
- P1: selected MaterialTheme currently does not cover dialogs/panels emitted after the main BrowserScreen Surface; Dark/Light/Glass appearance can leak on overlays.
- P1: five-action bottom toolbar may crowd/overflow at 320 dp width or large font scale; adaptive behavior needs implementation and device verification.
- P2: Light secondary palette #567D9F is approximately 4.35:1 on white, below WCAG AA 4.5:1 for normal text if used as text.
- P2: omnibox persistent label, tab-close semantics, appearance selected-state semantics and fixed 300 dp panels need accessibility/responsive fixes.
- Android Keystore-backed AES-GCM round-trip, tampering, key-loss and DataStore corruption/relaunch instrumentation are outstanding.
- Process-death tab restoration and private-tab exclusion require force-stop/relaunch tests.
- Per-site permission management/revoke UI is pending. HTTPS notification and persistent-storage requests have prompts; unsupported/device/media permissions remain denied.
- Downloaded file contents are not encrypted by the metadata cipher; private mode does not erase user-saved downloads.
- GeckoView remains provisional until comparative WebView/GeckoView benchmarks on representative 3–4 GB devices.
- Dependency vulnerability review and OWASP MASVS verification remain incomplete.
- Advanced Tab Universe, Privacy Intelligence Center, Transfer Studio, reader mode, QR and verified extension workflows are not implemented.

## Exact next steps
1. Confirm integrated Main CI run #244 succeeds; if it fails, inspect the failing job and fix before further feature work.
2. Fix the P1 overlay theme-scope defect and narrow-width/large-font toolbar behavior; add Compose UI tests/previews where feasible.
3. Fix Light secondary contrast and omnibox/tab/theme-selection accessibility semantics; make panels adapt to available window height.
4. Add Android instrumentation tests for Keystore AES-GCM round-trip, tampering, missing/lost key, legacy migration and DataStore corruption/relaunch preservation.
5. Test tab restoration, duplicate URLs and private-tab exclusion after force-stop/relaunch.
6. Install ARM64/ARMv7 builds on physical devices; benchmark startup, PSS, CPU, stability and release footprint on 3–4 GB devices.
7. Complete permission management/revoke UX, dependency review and OWASP MASVS assessment.
8. Only after Phase 1 acceptance, start Phase 2 features.
