# Changelog

## RIVO-09 release readiness audit (2026-11-09)
- Main HEAD after RIVO-02 integration: `9633351069a1c59d8bd184d4ad6f2e2603e0addb`.
- RIVO-02 theme overlay/accessibility fixes are merged; PRs #7–#13 remain in review or validation.
- PR #7, #9, #10, #12 and #13 have successful branch CI runs; PR #8 Android instrumentation is still running; PR #11 benchmark AAB packaging failed and is being corrected.
- Production release remains NO-GO: physical-device QA, real-device engine benchmark, dependency vulnerability/SBOM review, OWASP MASVS evidence and production signing credentials are outstanding.
- Added opt-in environment-only upload signing. No keystore or password is committed; ordinary QA CI remains unsigned.
- See `docs/release/RELEASE_READINESS.md` for the current blockers and verification matrix.

## Unreleased — Phase 1 Functional Browser MVP
- Integrate security QA: fail-closed encrypted metadata handling, validation before direct replacement/clear-history, malformed-record rejection and sensitive-write guard regression tests.
- Integrate Android engine fixes: selected-tab restoration by saved record index, omnibox edit preservation, repeated-history URL key correction and IDN normalization.
- Expand the three-theme design contract and align Premium Glass documentation with translucent surfaces and no real-time blur.
- Apply centralized Dark, Light and Premium Glass themes across browser overlays, improve narrow-screen controls and add theme contrast/previews.
- Physical-device validation, Android Keystore/DataStore instrumentation, responsive UI and production signing remain release gates.

## Phase 0 — Foundation and engine evaluation
- Initialize Android project foundation and modular engine adapters.
- Add WebView and GeckoView comparison probes.
- Add benchmark protocol and engine research.
- Physical-device benchmark and final engine selection remain pending.
