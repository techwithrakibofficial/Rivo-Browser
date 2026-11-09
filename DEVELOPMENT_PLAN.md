# Development Plan

## Current audit checkpoint — 2026-11-09
- Current Main HEAD is docs-only merge `159643bb358c5bf9e92d537acfe9ba5ee1c39f00`; latest application code merge is PR #27 `ad607a7...`.
- Main HEAD after PR #27 merge: `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`. PR #27 adds two Tab Universe search/filter JVM regressions; CI #306 passed all configured jobs on PR head before merge. Post-merge Main CI remains unconfirmed.
- Main HEAD after PR #23 merge: `d192baf0f38db835904aa76a901d7e7cb2a24e31`; Advanced Tab Universe is now integrated.
- PR #23 head passed CI #293; PR #24 head passed CI #300 (assembleDebug, lint, JVM tests, minified release bundle, size reports and QA artifact uploads) before merge as `350d4f3...`. A separate push-triggered Main CI result is not yet confirmed.
- Open PR #10: permission controls; 11 commits behind Main, mergeability conflict reported, rebase required. CI #268 passed its configured build/lint/JVM/release-bundle/QA artifact steps, but no instrumentation/device validation.
- Open PR #20: security/benchmark/API 35 integration; 9 commits behind Main, mergeability conflict reported. CI #288 passed all configured tasks on its old head, including benchmark builds, static 16 KB checks and API 35 emulator instrumentation; rebase and fresh CI required.
- Open PR #21: privacy center/private download hardening; stacked on #10, rebase required after #10.
- PR #17 was superseded by merged PR #23 (Advanced Tab Universe); PR #16 benchmark remains closed/unmerged.
- PR #24 Tab Universe accessibility and narrow-width polish is merged; manual 320 dp, large-font and TalkBack verification remains.
- Current priority is Phase 1 stabilization, integration and verification, not declaring Phase 2 complete.

## Phase 0 — foundation
- Android foundation, modular engine API and WebView/GeckoView probe variants are committed.
- Official documentation research and benchmark protocol are present.
- Physical-device comparison and final engine decision remain pending; GeckoView is provisional, not a measured winner.

## Phase 1 — Functional Browser MVP
### Implemented in Main source
1. GeckoView-backed browser session adapter with process-scoped runtime and Activity-safe view lifecycle.
2. Navigation, URL/search omnibox, loading state and engine-reported security status.
3. Multi-tab lifecycle, active/inactive session handling and native private sessions.
4. Local bookmarks/history and encrypted metadata at rest.
5. Safe download streaming, app-specific storage, confirmation, 250 MB cap and scoped open/share.
6. Centralized Dark, Light and Premium Glass palettes with secondary Quick Appearance Picker.
7. Regular HTTP(S) tab persistence/restoration; private tabs excluded.
8. Security fail-closed metadata write handling and malformed-record rejection.
9. Selected-tab index restoration, omnibox edit preservation, history duplicate-URL key correction and IDN normalization.
10. Design-system contract and source-level UI/accessibility audit.
11. Advanced Tab Universe: case-insensitive search, All/Normal/Private/Active filters, Active-first/Title/Type sorting and tab close/switch. PR #23 head CI #293 passed; manual responsive and accessibility QA remain.

### Build baseline
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- AGP 9.5.0, Gradle 9.6.0, JDK 17.
- No backend, account, telemetry or paid service.

### Remaining Phase 1 work (ordered)
1. Obtain/verify push-triggered CI for Main merge SHA `350d4f3...`; PR #24 CI #300 is green on its tested PR head.
2. Reconcile PR #20's five-commit divergence from Main; review changes and rerun CI/API 35 instrumentation before integration.
3. Rebase PR #10 to current Main, resolve conflicts, run CI and verify Gecko permission/notification behavior.
4. Rebase PR #21 after #10 integration; run fresh CI and API 35 instrumentation.
5. Recover/review PR #16 benchmark work if still useful; Advanced Tab Universe is integrated through PR #23.
6. Verify Keystore AES-GCM round-trip, tampering, missing/lost key, migration and DataStore corruption/relaunch on Android.
7. Verify duplicate-URL tab restoration and private-tab exclusion after force-stop/relaunch.
8. Test permissions, notifications, downloads, all themes, 320 dp width, short screens, large fonts and TalkBack.
9. Run ARM64/ARMv7 physical-device smoke tests and 3–4 GB RAM performance benchmarks.
10. Verify 16 KB runtime behavior, dependency/SBOM and OWASP MASVS controls.
11. Complete current UI/theme/accessibility defects and regression testing.
12. Verify PR #24's merged filter semantics on a 320 dp device, large font and TalkBack.
13. Verify PR #27's trimmed-search/filter-combination and empty-result policies in the broader UI/device workflow; JVM regression tests now pass on its PR head.
13. Only after Phase 1 acceptance, proceed to remaining Phase 2 features.

### Acceptance criteria
- Current Main CI passes assemble, lint, JVM tests, instrumentation and minified release AAB packaging.
- URL/search navigation, browser controls, tab lifecycle and duplicate-URL restoration work.
- Private sessions do not enter app-side history/bookmarks/restoration.
- Sensitive metadata is encrypted and corruption/key-loss never silently overwrites saved records.
- Downloads are confirmation-gated and safely stored/opened/shared.
- Dark/Light/Glass remain readable across all native surfaces and overlays.
- Manual device smoke tests, performance and security evidence are recorded.

## Phase 2 — Advanced browser
- Advanced Tab Universe is implemented in Main through merged PR #23; test narrow screens, large fonts, TalkBack and private-tab switching.
- Privacy Intelligence Center (PR #21 is open and depends on #10).
- Transfer Studio.
- Reader mode and QR scanning where feasible.
- Extension workflows only where engine support is verified.
- Developer Mode using verified engine capabilities.

## Phase 3 — optimization and distribution
- Final engine decision based on reproducible physical-device measurements.
- Accessibility and performance polish.
- Dependency/security review and release hardening.
- Production signing and distribution only after all release gates pass.
