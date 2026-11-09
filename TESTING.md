# Testing Plan

## Evidence policy
Build, lint, JVM tests, emulator instrumentation, physical-device tests and release signing are separate gates. Report only results tied to the exact commit/PR head tested.

## Current Main checkpoint
- Main HEAD after PR #23 merge: `d192baf0f38db835904aa76a901d7e7cb2a24e31`.
- PR #23 head `2f4027ac5dd5f8b60241485527c290b8b27916fa` passed Android CI run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026): assembleDebug, lintDebug, testDebugUnitTest and minified bundleRelease.
- GitHub's commit workflow lookup returned no push-triggered run for the merge commit; do not describe PR-head CI as a separate Main push validation.
- Historical Main run #246 passed on `84303588c2560db55bfb7ae2081d8f714be532e7`; it does not validate later Main commits.

## PR and branch evidence
- PR #10, recorded head `757a34d50a74f841a43bead906ec4be9a923b5b7`: PR description reports run #268 assembleDebug, lintDebug and testDebugUnitTest passed. Release bundle was still in progress in that description. No device permission/notification validation claimed.
- PR #20: description reports run #280 passed app assemble, lint, JVM tests, benchmark APK/AAB and minified shipping AAB builds, but the workflow failed at the 16 KB checker because it incorrectly checked 32-bit ELF alignment. Run #283 found/fixed an injected DataStore reference. Fresh CI on the current PR head is mandatory, including API 35 instrumentation.
- PR #21: description explicitly requires fresh Android CI. Added Keystore instrumentation tests are not claimed passing until executed.
- PR #23 (merged): CI #293 passed assembleDebug, lintDebug, JVM unit tests and minified release bundle on its PR head. The test suite added `TabUniversePolicyTest` coverage for title/URL search, Normal/Private/Active filters and three sorting modes. No Compose UI or device-level tab interaction test is claimed.
- PR #24 (open): semantic FilterChip selection, horizontal filter scrolling, persistent search label, active/inactive tab state and tab-specific close labels. Fresh CI is not yet returned; verify 320 dp width, large font and TalkBack before merge.

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
