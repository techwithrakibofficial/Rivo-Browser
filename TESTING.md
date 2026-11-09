# Testing Plan

## Evidence policy
Build, lint, JVM tests, emulator instrumentation, physical-device tests and release signing are separate gates. Report only results tied to the exact commit/PR head tested.

## Current Main checkpoint
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

## Supply-chain CI (new workflows; execution pending)
- `.github/workflows/osv-scanner-pr.yml`: official OSV-Scanner PR comparison workflow; requires inspection of the resulting check before merge.
- `.github/workflows/osv-scanner-scheduled.yml`: official full vulnerability scan on Main push, weekly schedule and manual dispatch.
- `.github/workflows/source-sbom.yml`: SPDX JSON source SBOM artifact, retained for 14 days.
- No scan or SBOM result is claimed until the workflows execute on a commit and their outputs are reviewed.
- The current repository has no committed Gradle dependency lockfiles. OSV/Syft coverage may therefore be incomplete for resolved/transitive Gradle dependencies; add and review Gradle lockfiles before treating the scan as a complete dependency audit.
