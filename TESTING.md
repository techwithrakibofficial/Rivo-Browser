# Testing Plan

## Evidence policy
Build, lint, JVM tests, emulator instrumentation, physical-device tests and release signing are separate gates. Report only results tied to the exact commit/PR head tested.

## Current Main checkpoint
- Current Main HEAD is documentation-only merge `159643bb358c5bf9e92d537acfe9ba5ee1c39f00`; latest application source merge is PR #27 `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`.
- PR #10 and #20 have diverged from current Main (11 and 9 commits behind respectively); their earlier CI results are not evidence for rebased integration. PR #21 remains stacked on #10.
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


## Supply-chain verification
- OSV-Scanner pull-request workflow and SPDX source-SBOM workflow ran successfully on the initial PR #28 head.
- Latest-Main integration PR #34 requires its own fresh CI run; do not infer its result from PR #28.
- Review OSV output and SBOM artifact contents. No committed Gradle dependency lockfiles currently exist, so transitive dependency coverage is not yet complete or proven.

## Account B candidate validation — 2026-11-09

- PR #36: https://github.com/techwithrakibofficial/Rivo-Browser/pull/36
- Candidate head after the latest documentation-independent source work must be validated by its exact SHA.
- Added JVM tests cover HTTP(S)-only download URLs, URL user-info rejection, private metadata persistence policy, canonical restored-file root and size constraints, and Gecko site-permission policy.
- Android CI run #42570107226 on the initial candidate was cancelled by a subsequent branch update before build tasks; do not count it as passed.
- Source SBOM and OSV scan completed successfully on the initial candidate head. Re-run both after the final branch commit.
- Required candidate CI: assembleDebug, lintDebug, testDebugUnitTest, benchmark APK/AAB builds, minified app bundle, static 16 KB APK/ELF alignment, API 35 connected instrumentation.
- Required manual checks: HTTPS permission grant/deny, notification runtime permission/channel/delivery/click, private notification denial, permission reset/read-back, private tab close/process death, download path/metadata migration, and actual 16 KB device runtime.
- Production release remains NO-GO until current-head CI, device QA, dependency review and signing verification are complete.


## Account B security candidate tests — PR #36

- DownloadSecurityPolicyTest covers HTTP(S)-only URL acceptance, host requirement, user-info rejection, private metadata policy and canonical restored-file root/size constraints.
- GeckoSitePermissionPolicyTest covers HTTPS-only mediated permission prompts, unsupported capability default-deny, private notification denial, private/prompt permission inventory exclusion and non-revocable persistent-storage policy.
- WebNotificationPolicyTest covers normal-mode HTTPS-only Android notification display and rejects private, HTTP, file, user-info and malformed sources.
- LocalDataCipherInstrumentedTest covers AES-256-GCM round-trip/randomized ciphertext, tampering, truncated payload, missing-key no-regeneration, DataStore corruption, migration and FileProvider scope. Added a regression for ambiguous legacy download-index migration and preservation of actual downloaded files/new metadata writes.
- CI now runs :engine-gecko:testDebugUnitTest in addition to app JVM tests.
- PR and Main builds generate a resolved Gradle dependency graph artifact with read-only permissions. GitHub Dependency Review was tested but reports repository Dependency Graph is disabled; its workflow was removed to avoid a permanent failing check. Enable Dependency Graph in repository settings before restoring automated advisory review/submission.
- PR #36 exact-head CI/OSV/SBOM results must be rechecked after the last source/doc commit. Earlier runs on previous SHAs are not evidence for the final head.
- Historical OSV runs without lockfiles were not a clean dependency scan. PR #36 now commits five resolved Gradle lockfiles and its latest OSV lockfile scan reports no issues across 788 package instances.
- Physical-device notification grant/deny/click, permission inventory/reset, private-tab process-death, and 16 KB runtime are still required.

- Added RestorableTabPolicyTest.processRestorationSnapshotExcludesPrivateTabsAndUnsafeUrls: verifies a process-restoration snapshot excludes private tabs and unsafe URLs, preserves duplicate normal tabs, and does not restore a private selected-tab ID.


## PR #36 follow-up regression coverage — 2026-11-09

- `DownloadSecurityPolicyTest` now verifies HTTPS-only downloads, rejection of HTTP/file/user-info URLs, MIME normalization, rejection of malformed MIME values and private metadata exclusion.
- Clear Browsing Data implementation closes active Gecko sessions before clearing Gecko site data/site settings, then clears local history and download metadata. Verify cookies, DOM storage, caches and permissions are absent after clearing; confirm bookmarks and actual download files remain.
- OSV workflow now resolves Gradle dependency locks for app, engine-api, engine-webview, engine-gecko and engine-benchmark, validates lockfile contents, scans them with OSV-Scanner and commits lockfiles to PR #36. Exact-head OSV result is pending.
- Latest Android CI must complete all build, lint, JVM, release bundle, static 16 KB and API 35 instrumentation steps before this candidate can be marked CI-verified. Physical-device permission and privacy checks remain separate acceptance items.


## PR #36 current verification checkpoint — 2026-11-09

- Source tests added/expanded: HTTPS-only URL policy, MIME normalization/rejection, private download metadata exclusion, private download directory cleanup, Gecko secure-origin/runtime permission policy, location site-permission policy and encrypted Android permission-denial persistence.
- Existing instrumentation coverage includes AES-256-GCM fresh ciphertext/nonce behavior, authentication-tag tamper rejection, truncated payload rejection, missing-key fail-closed behavior, legacy migration, DataStore corruption preservation and encrypted metadata round trips.
- Five module lockfiles are committed and verified non-empty. OSV run #42578249254 scanned app (284), engine-api (101), engine-gecko (152), engine-webview (101) and engine-benchmark (150): 788 package instances, no issues found.
- Source SBOM run #42578248119 uploaded an SPDX artifact containing 809 packages. Gradle Dependency Graph run #42578248161 succeeded; GitHub Dependency Graph submission is unavailable while the repository setting is disabled.
- The latest full Android run on the code-equivalent head passed assemble, lint, JVM tests, Gecko policy tests, benchmark builds, minified AAB, static 16 KB alignment and all configured artifact uploads. Instrumentation was cancelled during emulator-runner reconfiguration, not reported as passed. A fresh run with KVM setup, AOSP API 35 image and a bounded emulator timeout is required on the final documentation commit.
- Physical-device camera/microphone/location grant/deny/revoke, private process-death behavior, and 16 KB runtime behavior remain separate manual QA; no physical-device validation is claimed.
