# Testing Plan

## Automated CI scope
- Android compile platform API 37.2; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- Android instrumentation tests on an API 35 Google APIs emulator (`:app:connectedDebugAndroidTest`) for Keystore AES-GCM, DataStore migration/corruption/preservation, download-temp cleanup and FileProvider scope.
- JVM tests for URL/search resolution, safe restorable-tab URL policy, sensitive metadata write validation, IDN normalization and restored-tab index selection.
- Minified release AAB build with R8/resource shrinking.
- APK/AAB size reporting and unsigned QA artifact uploads for ARM64/ARMv7.

## Worker PR CI
The following GitHub Actions runs completed successfully:
- Run #237, security PR head `40fb89d801fd31da5fcae7e3a6a4f66a5e397543`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #240, Android engine PR head `90756bf69607c46f796123bb37efb9b508fea8fa`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #241, UI documentation PR head `a02bbe8c7f30ec5110e0e57f2c894545b7bbedc1`: Android CI workflow completed successfully.
These are branch-level validations, not proof that the integrated Main commit passes.

## Integrated Main validation
- Main baseline when RIVO-04 branch was created: `84303588c2560db55bfb7ae2081d8f714be532e7`.
- GitHub Actions baseline run #246: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810
- Run #246 completed successfully on the pre-RIVO-04 Main baseline; it does not include this branch's instrumentation sources or emulator workflow step.
- Check RIVO-04 branch-specific workflow results before claiming branch validation.

## Security QA coverage and limitations
- `SensitiveWriteGuardTest` verifies writes are allowed before integrity failure, remain blocked after repeated failures, and direct replacement is rejected when authentication/metadata validation fails.
- Direct open-tab/download replacement and clear-history authenticate and validate existing values inside DataStore edit transactions.
- The process guard serializes sensitive edit callbacks with integrity-failure publication.
- JVM callback-based tests validate the gate, not Android Keystore cryptography or on-device DataStore transaction durability.
- `LocalDataCipherInstrumentedTest` exercises AES-256-GCM round trip/fresh ciphertext, tampering, truncated payloads, missing alias without key regeneration, invalid encrypted JSON, missing-key write preservation after reopening DataStore with a fresh guard, legacy plaintext migration and bookmark/history/tab/download metadata persistence.
- DataStore corruption coverage reopens the same preferences file and confirms corrupt bytes remain unchanged rather than being reset. This is a DataStore lifecycle test, not an OS process kill/relaunch test.
- `DownloadTempFileCleanupInstrumentedTest` verifies only direct-child `.rivo-*.part` staging files are deleted; completed files and unrelated files remain.
- FileProvider instrumentation checks non-exported provider metadata, content URI access for allowed app-download files and rejection of files outside configured roots.
- The CI emulator test verifies the emulator Keystore implementation. Representative hardware-backed devices, secure-lock-screen invalidation and full force-stop/relaunch remain physical-device release checks.

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
20. Accessibility: TalkBack names/roles/selected states, 48 dp touch targets, font scaling, 320 dp width, panel scrolling and Glass fallback.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB Android device if available. Record model, chipset, Android build, ABI, GeckoView version, APK/AAB size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. Do not extrapolate emulator-only results to all 3–4 GB phones.

## Current status
- Worker PR CI passed on all three branches.
- Integrated Main CI run #244 was still running at the last checkpoint; integrated status is not yet confirmed.
- No physical-device hardware-backed Keystore, secure-lock-screen invalidation, OS force-stop/relaunch or 3–4 GB benchmark is claimed.
- QA AAB output is unsigned; it is not a production release artifact.
