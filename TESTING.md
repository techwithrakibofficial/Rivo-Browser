# Testing Plan

## Automated CI scope
- Android compile platform API 37.2; targetSdk 36.
- Assemble GeckoView MVP debug APKs.
- Android lint.
- JVM tests for URL/search resolution, safe restorable-tab URL policy, sensitive metadata write validation, IDN normalization, restored-tab index selection, Gecko site-permission policy, and download URL/filename/path/private-metadata policies.
- Android instrumentation tests for Android Keystore AES-GCM round-trip, AES-256 key size, ciphertext tampering and missing-key fail-closed behavior.
- Minified release AAB build with R8/resource shrinking.
- APK/AAB size reporting and unsigned QA artifact uploads for ARM64/ARMv7.

## Worker PR CI
The following GitHub Actions runs completed successfully:
- Run #237, security PR head `40fb89d801fd31da5fcae7e3a6a4f66a5e397543`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #240, Android engine PR head `90756bf69607c46f796123bb37efb9b508fea8fa`: assemble, lint, JVM tests, minified release bundle and QA artifact steps passed.
- Run #241, UI documentation PR head `a02bbe8c7f30ec5110e0e57f2c894545b7bbedc1`: Android CI workflow completed successfully.
These are branch-level validations, not proof that the integrated Main commit passes.

## Integrated Main validation
- Integrated HEAD at handoff: `1683ffb57dda1d1a9d15f85e892cb8139b06d23c`.
- GitHub Actions run #244: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481557821
- At the time of this update, debug APK assembly had passed and Android lint was in progress; unit tests, minified release AAB, size reporting and artifact uploads were pending.
- Update this section only after checking the final run conclusion and job steps.
- A later documentation-only commit may trigger a new Main CI run; always validate the current HEAD rather than assuming an earlier run covers it.

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
7. Omnibox: edit text while navigation callbacks arrive; ensure draft text is preserved until submission.
8. Private session: verify app-side history/bookmarks/restorable-tab state remain unchanged.
9. Bookmark add/remove and encrypted persistence.
10. History persistence, duplicate visits and clear.
11. Dark/Light/Glass persistence and system-bar contrast.
12. Open all dialogs/panels in all three appearance modes; verify theme scope and contrast.
13. Download confirmation/cancel, response-stream closure, HTTP(S)-only response URL validation, filename sanitization, duplicate names, atomic finalization, streamed 250 MB cap, canonical app-root metadata restoration and scoped read-only open/share.
14. Private-tab downloads: the file remains in app-specific storage after download, its in-memory list entry is removed when the originating private tab closes, and its new metadata is not saved to persistent browser download history; confirm the UI discloses that file bytes are not erased or encrypted by private mode.
15. Normal-mode HTTPS site-notification and supported autoplay requests show explicit Allow/Deny prompts; Android 13+ notification approval also requests POST_NOTIFICATIONS. Private-mode website notifications are denied.
16. Geolocation, persistent-storage, XR, DRM, tracking exceptions, third-party storage, camera, microphone and unsupported media/runtime requests are denied by default.
17. Privacy Intelligence Center lists actual GeckoView records, excludes private-mode records, and resets revocable records to VALUE_PROMPT; persistent-storage records are shown as non-revocable.
18. Private site permission decisions do not appear in the normal site-permission manager; no private permanent permission API is used.
19. Notification channel delivery is gated by Android notification permission; notification clicks are routed back to Gecko.
20. Tracking-protection status reflects GeckoSessionSettings and the active page's Gecko ContentPermission exception; no tracker count is displayed without a verified per-page engine count.
21. Certificate errors are not bypassed; security label comes from GeckoView callback.
22. Activity recreation/background/foreground; verify all sessions deactivate on stop and only selected session reactivates on start.
23. Force-stop/relaunch: regular HTTP(S) tabs restore; selected normal tab restores; private tabs do not reappear.
24. Release bundle builds with R8/resource shrinking and minified runtime behavior is smoke-tested.
25. Encrypted storage: legacy migration, fresh-IV round-trip, AES-256 Keystore key size, tampered/truncated ciphertext, unavailable key without replacement, and read-modify-write preservation.
26. Accessibility: TalkBack names/roles/selected states, 48 dp touch targets, font scaling, 320 dp width, panel scrolling and Glass fallback.

## Physical-device benchmark
Test at least one 3 GB and one 4 GB Android device if available. Record model, chipset, Android build, ABI, GeckoView version, APK/AAB size, cold/warm startup, PSS, CPU, thermal state and crashes/ANRs. Do not extrapolate emulator-only results to all 3–4 GB phones.

## Current status
- Worker PR CI passed on all three branches.
- Integrated Main CI run #244 was still running at the last checkpoint; integrated status is not yet confirmed.
- No physical-device install, site-permission grant/revoke, notification runtime prompt, private permission isolation, force-stop/relaunch, TalkBack, Accessibility Scanner or 3–4 GB benchmark is claimed.
- QA AAB output is unsigned; it is not a production release artifact.


## RIVO-11 additions (execution pending)
- `DownloadSecurityPolicyTest` covers URL scheme/host/user-info rejection, filename sanitization, canonical restored-file root/size checks, and the private-download metadata persistence rule.
- `LocalDataCipherInstrumentedTest` exercises the Android Keystore provider directly for AES-256-GCM round-trip, tamper detection and missing-key failure without replacement.
- These are newly added tests; do not report them as passing until the corresponding CI job completes. No hardware-backed/StrongBox assertion is made.
