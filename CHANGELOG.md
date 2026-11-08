# Changelog\n\n## RIVO-09 release readiness audit (2026-11-09)\n- Main CI run #246 passed assembleDebug, lintDebug, JVM unit tests, minified bundleRelease, and QA uploads for ARM64/ARMv7 debug APKs and unsigned AAB.\n- Production release remains NO-GO: Phase 1 PRs #6, #8, #9 and #10 are open; device QA, Android Keystore instrumentation, dependency vulnerability/SBOM review, OWASP MASVS evidence and production signing are incomplete.\n- Added environment-only upload signing configuration; no signing keys or passwords are committed. CI output remains unsigned unless explicitly configured.\n- See docs/release/RELEASE_READINESS.md for artifact archive sizes, blockers, test matrix, signing procedure and rollback plan.\n

## Unreleased — Phase 1 Functional Browser MVP
- Integrate security QA: fail-closed encrypted metadata handling, validation before direct replacement/clear-history, malformed-record rejection and sensitive-write guard regression tests.
- Integrate Android engine fixes: selected-tab restoration by saved record index, omnibox edit preservation, repeated-history URL key correction and IDN normalization.
- Expand the three-theme design contract and align Premium Glass documentation with translucent surfaces and no real-time blur.
- Add source-level UI/UX and accessibility audit with severity-ranked findings and device verification checklist.
- Retain GeckoView runtime/session architecture, navigation, private sessions, tracking protection, local bookmarks/history, encrypted metadata and confirmation-gated downloads.
- CI worker runs #237, #240 and #241 completed successfully; integrated Main run #244 is the separate validation gate and was in progress at the time of this update.
- Physical-device validation, Android Keystore/DataStore instrumentation, responsive UI fixes and production signing remain outstanding.

## Phase 0 — Foundation and engine evaluation
- Initialize Android project foundation and modular engine adapters.
- Add WebView and GeckoView comparison probes.
- Add benchmark protocol and engine research.
- Physical-device benchmark and final engine selection remain pending.
