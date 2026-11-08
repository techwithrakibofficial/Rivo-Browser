# Testing Plan

## Automated CI scope
- Android compile platform API 37.2; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- JVM tests for URL/search resolution, safe restorable-tab URL policy, sensitive metadata write validation, IDN normalization, restored-tab index selection and WCAG palette contrast in all three appearance modes.
- Minified release AAB build with R8/resource shrinking.
- APK/AAB size reporting and unsigned QA artifact uploads for ARM64/ARMv7.

## Worker PR CI
The following GitHub Actions runs completed successfully:
- Run #237, security PR head `40fb89d801fd31da5fcae7e3a6a4f66a5e397543`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #240, Android engine PR head `90756bf69607c46f796123bb37efb9b508fea8fa`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #241, UI documentation PR head `a02bbe8c7f30ec5110e0e57f2c894545b7bbedc1`: Android CI workflow completed successfully.
These are branch-level validations, not proof that the integrated Main commit passes.

## Integrated Main validation
- Current pre-RIVO-03 Main baseline: `84303588c2560db55bfb7ae2081d8f714be532e7`.
- GitHub Actions run #246: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810
- Run #246 completed successfully on 2026-11-08; it validates the main baseline only, not this RIVO-03 change set.
- Run #244 for the previous integrated commit was cancelled and is superseded by the successful #246 run.
- Verify branch/PR CI before treating RIVO-03 changes as build/lint/unit-test verified.

## Accessibility and responsive UI regression coverage
- `RivoColorSchemesTest` computes WCAG relative luminance and asserts at least 4.5:1 for primary, secondary, on-surface and error palette roles against Light and Dark surfaces and the composited Glass surface.
- The unit test checks source palette values; it does not inspect rendered UI compositing, disabled/focused states, icons, non-text contrast or every actual text usage.
- The existing Android workflow runs JVM tests but does not provision an emulator or run Compose instrumentation tests. Full BrowserScreen UI tests were not added because BrowserScreen initializes a GeckoView-backed controller and the project has no Compose instrumentation runner/emulator step.
- Manual matrix required: 320 dp width, standard width, short display height, font scale 1.0/1.3/2.0, Dark/Light/Glass, TalkBack, hardware keyboard/focus traversal, panel scrolling and Glass fallback.

## Security QA coverage and limitations
- `SensitiveWriteGuardTest` verifies writes are allowed before integrity failure, remain blocked after repeated failures, and direct replacement is rejected when authentication/metadata validation fails.
- Direct open-tab/download replacement and clear-history authenticate and validate existing values inside DataStore edit transactions.
- The process guard serializes sensitive edit callbacks with integrity-failure publication.
- JVM callback-based tests validate the gate, not Android Keystore cryptography or on-device DataStore transaction durability.
- Android Keystore AES-GCM round-trip, tampering, missing/lost key, legacy migration and DataStore corruption/relaunch instrumentation remain required.

## Phase 1 functional test matrix
1. Initial home page.
2. HTTPS URL, explicit HTTP URL, bare domain, internationalized domain and search term.
3. Unsupported schemes (javascript:, file:, content:) resolve safely as search queries.
4. Back, forward, reload and stop.
5. Create/switch/close tabs; verify inactive sessions deactivate.
6. Duplicate-URL tabs: restore selected tab by saved record index, not URL equality.
7. Omnibox: edit text while page navigation callbacks arrive; ensure draft text is preserved until submission.
8. Private session: verify app-side history/bookmarks/restorable-tab state remain unchanged.
9. Bookmark add/remove and encrypted persistence.
10. History persistence, duplicate visits and clear.
11. Dark/Light/Glass persistence and system-bar contrast.
12. Open all dialogs/panels in all three appearance modes; verify theme scope and contrast.
13. Download confirmation/cancel, response-stream closure, filename sanitization, duplicate names, atomic finalization, 250 MB cap, metadata restoration and open/share.
14. HTTPS notification and persistent-storage requests show explicit Allow/Deny prompts; unsupported hardware/media requests remain denied.
15. Certificate errors are not bypassed; security label comes from GeckoView callback.
16. Activity recreation/background/foreground; verify all sessions deactivate on stop and only selected tab reactivates on start.
17. Force-stop/relaunch: regular HTTP(S) tabs restore; selected normal tab restores; private tabs do not reappear.
18. Release bundle builds with R8/resource shrinking and minified runtime behavior is smoke-tested.
19. Encrypted storage: legacy migration, fresh-IV round-trip, tampered/truncated ciphertext, unavailable key and read-modify-write preservation.
20. Accessibility: TalkBack names/roles/selected states, 48 dp touch targets, font scale 1.0/1.3/2.0, 320 dp width, short-height dialogs/panels, keyboard/focus traversal and Glass fallback. This remains a manual device matrix; do not infer a pass from JVM CI.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB Android device if available. Record model, chipset, Android build, ABI, GeckoView version, APK/AAB size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. Do not extrapolate emulator-only results to all 3–4 GB phones.

## Current status
- Worker PR CI passed on all three branches.
- Integrated Main baseline run #246 passed on commit `84303588c2560db55bfb7ae2081d8f714be532e7`; it does not cover RIVO-03.
- RIVO-03 branch-specific CI, emulator/screenshots, TalkBack and Accessibility Scanner checks remain outstanding until their results are recorded.
- No physical-device install, force-stop/relaunch, TalkBack, Accessibility Scanner or 3–4 GB benchmark is claimed.
- QA AAB output is unsigned; it is not a production release artifact.
