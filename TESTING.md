# Testing Plan

## Evidence policy
Build, lint, JVM tests, emulator instrumentation, physical-device tests and release signing are separate gates. Report only results tied to the exact commit/PR head tested.

## Current Main checkpoint
- Main HEAD after PR #27 merge: `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`.
- PR #27 head `94e635cc77b93d859016238347811ef5da0d12f4` passed CI run [#306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42562505360): assembleDebug, lintDebug, JVM unit tests, minified bundleRelease, APK/AAB size reporting and QA artifact uploads. PR #27 merged as `ad607a7...`; no separate push-triggered workflow result for the merge SHA has been returned.
- Main HEAD after PR #23 merge: `d192baf0f38db835904aa76a901d7e7cb2a24e31`.
- PR #23 head `2f4027ac5dd5f8b60241485527c290b8b27916fa` passed Android CI run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026): assembleDebug, lintDebug, testDebugUnitTest and minified bundleRelease.
- PR #24 head CI #300 passed all configured build/test/report/upload steps. No push-triggered workflow run has been returned for the merge commit; do not conflate PR-head CI with post-merge CI.
- Historical Main run #246 passed on `84303588c2560db55bfb7ae2081d8f714be532e7`; it does not validate later Main commits.

## PR and branch evidence
- PR #10, recorded head `757a34d50a74f841a43bead906ec4be9a923b5b7`: CI run #268 completed successfully through assembleDebug, lintDebug, testDebugUnitTest, minified bundleRelease, size reports and QA AAB/ARM64/ARMv7 uploads. This workflow had no Android instrumentation step; no device permission/notification validation is claimed.
- PR #20 recorded head `71af60e...`: CI run #288 completed successfully through shipping assemble, lint, JVM tests, GeckoView/WebView benchmark APK/AAB builds, minified shipping bundle, static APK/native ELF 16 KB alignment checks, artifact size reports/uploads and `connectedDebugAndroidTest` on the configured API 35 emulator. It is five commits behind current Main and mergeability is false; rebase/conflict resolution and fresh CI on the rebased head are still required. Emulator instrumentation/static alignment do not prove physical hardware or 16 KB runtime behavior.
- PR #21: description explicitly requires fresh Android CI. Added Keystore instrumentation tests are not claimed passing until executed.
- PR #23 (merged): CI #293 passed assembleDebug, lintDebug, JVM unit tests and minified release bundle on its PR head. The test suite added `TabUniversePolicyTest` coverage for title/URL search, Normal/Private/Active filters and three sorting modes. No Compose UI or device-level tab interaction test is claimed.
- PR #24 (merged as `350d4f3...`): CI #300 passed assembleDebug, lintDebug, JVM tests, minified release bundle, size reports and QA uploads. Source adds semantic FilterChips, horizontal filter scrolling, persistent search label, active/inactive state and tab-specific close labels. No Compose UI or physical-device/TalkBack test is claimed.
- Core follow-up PR #27: added JVM regression cases for trimmed search input combined with Normal/Private filters and empty-result behavior. CI #306 passed all configured steps on PR head; merged as `ad607a7...`.

## Tab Universe policy regression additions (PR #27)
- `trimsSearchInputAndCombinesItWithTheSelectedFilter`: verifies trimmed search text combines with the selected Normal/Private filter.
- `returnsNoTabsWhenTheSearchHasNoMatches`: verifies an unmatched query returns an empty tab list.
- Both tests passed in CI #306 on PR head `94e635c...`; this is JVM policy coverage, not Compose UI or device interaction coverage.

## Required automated checks
1. `:app:assembleDebug`
2. `:app:lintDebug`
3. `:app:testDebugUnitTest`
4. `:app:connectedDebugAndroidTest` on API 35 emulator where configured
5. Minified `:app:bundleRelease`
6. Benchmark debug APK and release AAB tasks where benchmark module is included
7. APK ZIP alignment and 64-bit native ELF 16 KB alignment checks
8. Dependency vulnerability and SBOM review

## Required functional / instrumentation matrix
- URL/search resolution, HTTP/HTTPS, IDN, unsupported schemes.
- Back/forward/reload/stop and loading/security callbacks.
- Create/switch/close tabs; inactive session lifecycle.
- Advanced Tab Universe: search by title/URL, combine search with All/Normal/Private/Active filters, sort by Active-first/Title/Type, select and close filtered tabs; verify on 320 dp screens and with TalkBack.
- Duplicate-URL selected-tab restoration after process death.
- Omnibox draft preservation while Gecko callbacks arrive.
- Private session excluded from history/bookmarks/restorable tabs.
- Keystore AES-GCM round-trip, tampering, truncated payload, missing/lost key and legacy migration.
- DataStore corruption/relaunch must fail closed and preserve prior ciphertext.
- Download cancellation, response closure, size limit, filename sanitization, canonical path and private metadata policy.
- Permission grant/deny/revoke, notification runtime permission/delivery/click, private-mode isolation.
- Dark/Light/Glass dialogs and panels, 320 dp width, short screens, large font scale.
- TalkBack semantics, focus order and contrast.
- Tab Universe filters: verify horizontal scrolling on narrow layouts, selected chip announcement, persistent search label, active/inactive state and uniquely labelled close actions.
- 16 KB Android runtime launch and real-device performance.

## Device QA required
- Representative ARM64/ARMv7 devices where supported.
- 3–4 GB RAM target devices: startup, PSS, CPU, thermal, tab switching, crash/ANR and battery.
- Physical permission and notification workflows.
- Force-stop/relaunch and private session lifecycle.
- 16 KB page-size Android image/device.

## Release gate
Production release remains NO-GO until current Main CI is green, security/instrumentation checks pass, device QA is recorded, dependency/MASVS review is complete, and a production-signed artifact is produced securely.
