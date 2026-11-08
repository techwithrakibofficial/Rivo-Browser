# Changelog

## 2026-11-08 — CI and documentation synchronization
- Verified integrated Main commit `84303588c2560db55bfb7ae2081d8f714be532e7` with [GitHub Actions run #246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810): assembleDebug, lintDebug, testDebugUnitTest, minified bundleRelease, size reporting and unsigned QA artifact uploads passed.
- Reconciled project status, handoff, plan, architecture, design, feature matrix, testing and security documentation with the verified checkpoint.
- No application source or CI workflow was changed. Device, accessibility, instrumentation and production-signing gates remain open.

## Unreleased — Phase 1 Functional Browser MVP
- Integrate security QA: fail-closed encrypted metadata handling, validation before direct replacement/clear-history, malformed-record rejection and sensitive-write guard regression tests.
- Integrate Android engine fixes: selected-tab restoration by saved record index, omnibox edit preservation, repeated-history URL key correction and IDN normalization.
- Expand the three-theme design contract and align Premium Glass documentation with translucent surfaces and no real-time blur.
- Add source-level UI/UX and accessibility audit with severity-ranked findings and device verification checklist.
- Retain GeckoView runtime/session architecture, navigation, private sessions, tracking protection, local bookmarks/history, encrypted metadata and confirmation-gated downloads.
- CI worker runs #237, #240 and #241 and integrated Main run #246 completed successfully at their recorded commit checkpoints.
- Physical-device validation, Android Keystore/DataStore instrumentation, responsive UI fixes and production signing remain outstanding.

## Phase 0 — Foundation and engine evaluation
- Initialize Android project foundation and modular engine adapters.
- Add WebView and GeckoView comparison probes.
- Add benchmark protocol and engine research.
- Physical-device benchmark and final engine selection remain pending.
