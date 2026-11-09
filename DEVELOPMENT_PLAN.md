# Development Plan

## Current audit checkpoint — 2026-11-09
- Main HEAD: `72700fa6e599af94317d5e7d76eaa211e992281d`; latest commit adapts browser panels to short screens.
- No CI result was returned for this exact Main SHA. Historical run #246 on `84303588...` is not current validation.
- Open PR #10: permission controls; old Main base, mergeability conflict reported, rebase required.
- Open PR #20: security/benchmark/API 35 integration; fresh CI required.
- Open PR #21: privacy center/private download hardening; stacked on #10, rebase required after #10.
- PR #16 benchmark and PR #17 Advanced Tab Universe are closed unmerged; recover branch work before counting either as integrated.
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

### Build baseline
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- Android compile SDK API 37.2; targetSdk 36; minSdk 26.
- AGP 9.5.0, Gradle 9.6.0, JDK 17.
- No backend, account, telemetry or paid service.

### Remaining Phase 1 work (ordered)
1. Obtain green CI for exact current Main HEAD `72700fa6...`.
2. Review/integrate PR #20 only after fresh CI, API 35 instrumentation and code review.
3. Rebase PR #10 to current Main, resolve conflicts, run CI and verify Gecko permission/notification behavior.
4. Rebase PR #21 after #10 integration; run fresh CI and API 35 instrumentation.
5. Recover PR #16/#17 branch changes and open reviewed replacement PRs if still needed.
6. Verify Keystore AES-GCM round-trip, tampering, missing/lost key, migration and DataStore corruption/relaunch on Android.
7. Verify duplicate-URL tab restoration and private-tab exclusion after force-stop/relaunch.
8. Test permissions, notifications, downloads, all themes, 320 dp width, short screens, large fonts and TalkBack.
9. Run ARM64/ARMv7 physical-device smoke tests and 3–4 GB RAM performance benchmarks.
10. Verify 16 KB runtime behavior, dependency/SBOM and OWASP MASVS controls.
11. Complete current UI/theme/accessibility defects and regression testing.
12. Only after acceptance, proceed to Phase 2 features.

### Acceptance criteria
- Current Main CI passes assemble, lint, JVM tests, instrumentation and minified release AAB packaging.
- URL/search navigation, browser controls, tab lifecycle and duplicate-URL restoration work.
- Private sessions do not enter app-side history/bookmarks/restoration.
- Sensitive metadata is encrypted and corruption/key-loss never silently overwrites saved records.
- Downloads are confirmation-gated and safely stored/opened/shared.
- Dark/Light/Glass remain readable across all native surfaces and overlays.
- Manual device smoke tests, performance and security evidence are recorded.

## Phase 2 — Advanced browser
- Advanced Tab Universe (PR #17 is closed unmerged; verify/recover before reuse).
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
