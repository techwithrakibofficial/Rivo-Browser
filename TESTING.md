## 2026-11-25 — PR #91 exact-head validation

- Tested commit: `21c16f7495d18bb9030aa249485315277515e602`.
- Android CI run `45602982534`: **success**. Passed shipping debug assemble, lint, app unit tests, Gecko policy unit tests, Gecko/WebView benchmark builds, minified shipping AAB build, static APK/AAB 16 KB alignment checks, diagnostic APK build/upload and API 35 Android security instrumentation tests.
- OSV Scanner PR run `45602982377`: **success**; scanner completed and committed Gradle lockfiles matched resolved dependencies.
- Gradle Dependency Graph run `45602982512`: **success**.
- Source SBOM run `45602982369`: **success**.
- Gradle Dependency Verification Bootstrap run `45602982354`: **success**; generated checksum metadata passed strict-mode validation across the configured shipping and test task set.
- Startup-diagnostic APK uploaded as artifact `15016269540` in run `45602982534`. Artifact is installable QA output, not production-signed.
- This is CI/emulator evidence only. Physical-device crash reproduction, copied report inspection, accessibility/manual privacy QA and production signing remain separate gates.

# Rivo Browser — Testing and Evidence

## 2026-11-24 — Release workflow follow-up evidence
- PR #83 documentation update merged after CODEOWNER approval.
- PR #84 head `cfbb9ef6d226ce0725e555fbc7e1bb2c028261b9`: Android CI, Gradle dependency verification bootstrap, OSV Scanner PR, Gradle Dependency Graph and Source SBOM all completed successfully; CODEOWNER approval was recorded. It was closed as superseded because it was behind Main.
- PR #85 head `256468fd266e2710de1d8f5c4e3d4c82d4f44b30` is based on Main `2126dd3c692da6d85553015d2064a414df90e207`; its five workflows are running at documentation time. Fresh PR #85 CODEOWNER review is requested.
- The workflow source correction changes APK verification to use preserved `dist/*.apk` after the AAB build's `gradle clean`. This is source inspection only until PR #85 CI and a signed release run pass.
- No signed production artifact, physical-device acceptance, runtime 16 KB test or full MASVS/MASTG assessment is evidenced.


## 2026-11-24 — PR #82 and post-merge validation
- PR #82 head SHA `62c1185d405ed32d7ce074da23d4d899eab434e4`: Android CI, OSV Scanner PR, Gradle Dependency Graph, Source SBOM and Gradle dependency-verification bootstrap all completed successfully.
- Merge commit: `5ac3a6853cd20df47813acc580fcae2dbb34cbe9`.
- Post-merge Main: OSV Scanner Scheduled, Source SBOM and Gradle Dependency Graph completed successfully. Android CI run #45394811390 and Gradle Dependency Verification Bootstrap run #45394811440 were still in progress at this checkpoint.
- Previous signed-release run #45385076282 failed at `:app:buildReleasePreBundle` on the old workflow revision; it must not be counted as a successful release test.
- No production-signed artifact, physical-device acceptance, runtime 16 KB validation or complete MASVS assessment is evidenced here.


Updated: 2026-11-24

## 2026-11-23 — Release CI reconciliation (PR #64 candidate)
- Live audit confirmed PR #64 and PR #65 target the same Main SHA (cfa94c3f26e8e478bb51adf1b3b60fc6ea871006) and overlap in the signed-release workflow and dependency verification metadata. PR #64 is the preferred candidate because it additionally verifies APK/AAB signer fingerprint consistency and exact AAB output cardinality.
- PR #64 dependency bootstrap run #45171080773 passed: generated SHA-256 metadata across shipping/test configurations, strict verification, and validated lockfile/metadata artifacts. The generated files already matched the PR branch; no redundant commit was created.
- Release workflow candidate now retains the R8 mapping artifact for 90 days, explicitly requires jarsigner to report a verified AAB, and downloads published release assets to re-check their SHA-256 checksums.
- PR checks remain pending owner approval for some workflows; this candidate is not merged and no production-signed build has run. Production Environment secrets remain owner-managed.
- Active Main ruleset Protect main branch currently requires a status check named b, which is absent from Main's observed check-run names; correct the required context to actual CI check names before merge. Do not bypass the ruleset.

## PR #64 dependency verification evidence — 2026-11-23
- Workflow run #45171080773, attempt 2: success.
- Passed steps: Java/Android SDK/Gradle setup; clean metadata baseline; SHA-256 bootstrap across shipping and test configurations; strict-mode validation; metadata artifact upload; lockfile artifact upload; comparison/commit job.
- The commit job reported “Bootstrap artifacts already match the PR branch”; no generated metadata or lockfile commit was necessary.
- This validates dependency verification for PR #64's source tree. It does not validate production signing, nor does it mean the PR is merged.


## Evidence levels
- Source review is not a test.
- JVM tests are not Android instrumentation.
- Emulator instrumentation is not physical-device acceptance.
- Static 16 KB alignment checks are not runtime verification.
- Unsigned QA artifacts are not production release artifacts.
- The signed-release workflow is not validated until a real signed build completes and GitHub Release assets are verified.

## Historical Main-head automated CI evidence — 2026-11-10
- Main source SHA: `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`.
- Android CI run [#42763857175](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42763857175) completed with conclusion `success`.
- Passed steps: shipping assemble, Android lint, unit tests, Gecko engine policy tests, GeckoView and WebView benchmark builds, minified shipping release bundle, native library/APK static 16 KB alignment, artifact-size report, QA artifact uploads and API 35 Android security instrumentation.
- Uploaded artifacts (all from the above source SHA): ARM64 debug APK (110,066,243 bytes), ARMv7 debug APK (106,089,320 bytes), unsigned shipping QA AAB (249,326,269 bytes), Android test reports, GeckoView benchmark and WebView benchmark. These are temporary QA outputs, not production-signed deliverables. GitHub artifact retention is limited; do not treat the artifact URLs as permanent releases.
- Main OSV Scanner run [#42763857162](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42763857162) scanned five Gradle lockfiles: app 310, engine-gecko 152, engine-benchmark 150, engine-api 101 and engine-webview 101 package entries (814 total). The reporter printed **No issues found** and uploaded SARIF to Code Scanning. This only means no known advisories were found in those scanned lockfiles at scan time.
- Main Gradle Dependency Graph run #42763856885 and Source SBOM run #42763856868 completed successfully.
- Earlier integrated source tree `9115b9d3c607353facc15ed9b6fb4e3e53ce696d` also passed PR Android CI run #463 and API 35 instrumentation.
- Signed-release workflow `.github/workflows/signed-release.yml` is merged but has not had a signed runtime execution; production signing secrets are required.

## Required manual/device matrix — not yet evidenced
- Reader extraction: readable article, non-readerable page, gated/paywalled page, navigation during extraction.
- QR scanner: compatible Play services, unavailable scanner module, invalid/oversized payload, confirmation before navigation.
- Transfer Studio: progress with/without Content-Length, cancel, file open/share/remove, path traversal and private-tab close cleanup.
- Permissions: secure/insecure origins, allow/deny, Android runtime denial, revoke/read-back, unsupported capabilities default-deny.
- Notifications: HTTPS normal-mode delivery/click/close, HTTP and private-mode denial, Android notifications disabled.
- Private browsing: no app-side history/bookmarks/tab restore/download metadata; cleanup of private downloads on tab close.
- Data lifecycle: process death, DataStore corruption, Keystore key loss, restore and Clear Browsing Data.
- Accessibility: TalkBack, focus order, 320 dp width, large font, contrast, reduced motion.
- Performance: cold start, memory, page load and battery on 3–4 GB and modern devices.
- 16 KB runtime on compatible hardware.

## Release verification
- Configure protected production signing secrets only in repository Actions settings; never commit keystore/passwords.
- Execute the signed-release workflow for a reviewed version tag.
- Verify APK signatures with apksigner, AAB signature with jarsigner, SHA-256 checksums and published GitHub Release assets.
- Do not distribute unsigned CI artifacts as the final release.

## Reporting template
Record device model, Android/API level, build SHA, test case, steps, expected result, actual result, pass/fail and logs/screenshots. Do not mark a case passed without evidence.


## Website notification privacy regression
- JVM test `WebNotificationPrivacyPolicyTest` verifies that the public notification version contains generic Rivo-owned text rather than website-controlled title/body/origin.
- Device test required: trigger a notification with a unique sensitive phrase; confirm the unlocked notification remains functional while secure lock-screen and screen-sharing surfaces show only generic content.
- Repeat across supported Android versions and OEM lock-screen preview settings.


## 2026-11-11 PR #46 verification evidence

- PR #46 head SHA: `10add6d2753b3e4b1740b332e22b1c3723a42000`; merged into Main by commit `b50350a241908fb1f38d2920b7a358451cb436f7`.
- Android CI run [#43024973140](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43024973140): `success`. Confirmed steps: shipping assemble, Android lint, app unit tests, Gecko engine policy unit tests, GeckoView/WebView benchmark builds, static APK/native-library 16 KB alignment, API 35 Android security instrumentation.
- OSV Scanner PR [#43024973788](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43024973788), Gradle Dependency Graph [#43024973190](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43024973190) and Source SBOM [#43024973177](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43024973177): all `success` on the same PR head SHA.
- QA artifact IDs from Android CI: ARM64 debug APK `13857117441`; ARMv7 debug APK `13857660007`; unsigned shipping QA AAB `13857067490`; Android test reports `13855974581`; Gecko benchmark `13857132203`; WebView benchmark `13856148877`. Artifacts are temporary QA outputs, not signed production deliverables.
- Post-merge workflows for Main SHA `b50350a241908fb1f38d2920b7a358451cb436f7` were still running when this entry was recorded. Recheck Actions for final Main-head conclusions.
- Static 16 KB alignment and API 35 emulator instrumentation are verified; runtime 16 KB behavior and physical-device acceptance are not.


## 2026-11-13 Serial 01 PR validation evidence

- Main SHA `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af`; Android CI run #513 (run ID 43470442172) completed with conclusion `success`.
- PR #50 earlier head `c0f90cd9c9538e26a016db488339c11583700b7d`: Android build, resolved Gradle lockfile/OSV, reusable OSV scan, dependency graph and source SBOM jobs completed successfully. The separate `osv-scanner` check concluded `neutral`; it is recorded as neutral, not success.
- PR #51 earlier head `e6ba664c0b115e9213fe57cd33cbda2a875780ec`: Android build, OSV, dependency graph and source SBOM jobs completed successfully. A separate OSV check on the later associated run concluded `neutral`.
- These are historical results for the exact listed heads. Subsequent workflow/documentation commits invalidate them as evidence for the latest PR heads; fresh checks must complete before merge.
- No signed-release workflow execution, APK/AAB signature proof from production credentials, published release, or production Environment reviewer configuration is evidenced by these checks. For reviewer gating to protect signing credentials, all four signing values must be configured as `production` Environment secrets, not repository-level secrets.
- GitHub workflow-run checks validate CI only. YAML/security semantics must still be reviewed; physical-device and hardware tests remain separate release gates.


## 2026-11-13 — PR #50 final merge evidence and PR #51 refresh

- PR #50 head `f1b3212e8b68317d2a40aeb37958cb35e51312e6` Android CI run #521 / 43474361388: `success`. Passed shipping assemble, lint, app unit tests, Gecko policy unit tests, benchmark debug/release builds, minified shipping bundle, static APK/native library 16 KB alignment, artifact size report, QA artifact uploads, KVM setup and API 35 Android security instrumentation.
- PR #50 OSV workflow run 43474361869: both introduced-dependency and resolved-Gradle-lockfile jobs `success`; scanned app (310), engine-gecko (152), engine-benchmark (150), engine-api (101), engine-webview (101) = 814 package entries; OSV output `No issues found`; lockfile drift check passed; artifact `rivo-gradle-lockfiles` uploaded (artifact ID 14062770541).
- PR #50 Dependency Graph run 43474361291 and Source SBOM run 43474361406: `success`.
- Check `osv-scanner` run 132054413821 concluded `neutral` with a configuration-not-found notice referencing the scheduled scanner workflow. It is recorded as neutral, not success; the actual PR OSV scan jobs succeeded.
- PR #50 squash-merged to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`. Main post-merge CI and supply-chain runs were queued at the time of this checkpoint; verify their final conclusions.
- PR #51 final head `3c43a36dab28c72b8344491131a74bf51de6ae38` passed Android CI run 43476946517, OSV jobs 43476947044, Dependency Graph 43476946531 and Source SBOM 43476946667; PR #51 merged as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Signed-release workflow static checks: PyYAML parsed the workflow successfully; `bash -n` passed for all seven `run` scripts; assertions confirmed default `contents: read`, signing job `contents: read` + `production`, and publishing-only `contents: write`. Actionlint was not installed/run; no production workflow execution was attempted.
- Main post-PR #51 OSV Scanner run 43478503114 and Source SBOM run 43478502714 succeeded; Android CI 43478502506 and Dependency Graph 43478502631 were in progress at the checkpoint. No production signing secrets, signed release, published APK/AAB, or production Environment reviewer configuration is evidenced. All four signing values must be `production` Environment secrets to be protected by Environment reviewers.


## Gradle dependency verification — not configured
- Inspected Main SHA `d93f598464a48351ca0440ad2cfeaa768e55d3f9`: `gradle/verification-metadata.xml` and `gradle/verification-keyring.keys` are absent.
- Gradle dependency verification is not enabled, so no checksum/signature verification test can be reported as passed. Existing lockfile resolution and OSV scans are separate controls.
- Follow-up: bootstrap SHA-256 verification metadata from reviewed dependency artifacts, review the generated trust entries, commit the metadata, then rerun all Gradle tasks with verification enabled.


## 2026-11-13 — PR #51 final verification
- PR #51 final head `3c43a36dab28c72b8344491131a74bf51de6ae38`: Android CI 43476946517 `success`; includes API 35 Android security instrumentation and all QA artifact uploads.
- OSV Scanner PR 43476947044: introduced-dependency and resolved-lockfile jobs `success`; 814 package entries scanned, `No issues found`, lockfile drift check passed and artifact uploaded.
- Dependency Graph 43476946531 and Source SBOM 43476946667: `success`.
- Separate check `osv-scanner` 132063311666: `neutral` because the scheduled workflow configuration was not found in the PR Code Scanning comparison. It is not counted as success.
- PR #51 merged as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Static signed-release checks: PyYAML parse, seven `bash -n` scripts and permission-policy assertions passed. Actionlint and a real production signing execution were not run.


## Serial 01 post-merge verification — 2026-11-13 18:21 UTC

- Current Main SHA at verification: `528a914db56c75c19ca1c4e03d919ee42b355580`.
- PR #50 is merged: `d93f598464a48351ca0440ad2cfeaa768e55d3f9`. PR #51 is merged: `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Main commit check-runs on `528a914`: Android `build`, OSV `Scan known dependency vulnerabilities / osv-scan`, `dependency-graph`, and `Generate source SBOM` all completed with conclusion `success`. These are the four reported check-runs; no pending, failed, neutral or skipped conclusion was present in this check-run set.
- Main branch metadata reports `protected=false`; branch protection is disabled. The available GitHub integration cannot configure branch rules or Environment protection.
- No GitHub Releases exist at verification time. The signed-release workflow is present, but no production signing run, signed APK/AAB, checksum release assets, or published release is evidenced.
- Production Environment reviewer policy, deployment ref restrictions, and presence of all four secrets specifically in the `production` Environment could not be verified with the available integration. Owner/admin must configure and verify these controls before production signing.
- Gradle artifact dependency verification remains unconfigured: `gradle/verification-metadata.xml` and a verification keyring are absent. Lockfiles and OSV scans are separate controls.
- This checkpoint updates CI state only; it does not close physical-device QA, accessibility, 3–4 GB performance profiling, runtime 16 KB validation, or the full MASVS/MASTG review.


## Latest Main update — 2026-11-13 20:45 UTC

- Main is at `2082f9ed46ec5357d22a81540030fb69bb8d59a7` (confirmed identical to `main`).
- Latest application change fixes an Android instrumentation-test DataStore fixture shutdown race in `LocalDataCipherInstrumentedTest`: teardown now cancels and joins each fixture coroutine scope before deleting/reopening its preferences file. This is test reliability hardening; it does not alter production browser behavior.
- The commit is present on Main. A successful post-commit CI result was not available from the connected GitHub checks interface at this checkpoint; do not claim this commit passed CI until its Actions run is verified.
- Existing release blockers remain unchanged: protected production signing secrets and Environment approval, signed APK/AAB publication, physical-device functional/privacy and accessibility QA, 3–4 GB profiling, runtime 16 KB validation, Gradle artifact verification metadata, full MASTG/data-flow review, and Main branch protection.
- No device acceptance or production release is claimed by this checkpoint.


## Authoritative Main verification — 2026-11-14

- Main SHA: `54febd57265ccc165740d9ebf42198270a92346b` (PR #54 squash merge commit).
- PR #54 (documentation synchronization) is merged; merge timestamp: 2026-11-13 21:03:14 UTC; merge commit: `54febd57265ccc165740d9ebf42198270a92346b`.
- All four post-merge workflows on this exact Main SHA completed successfully: Android CI #544 (run ID `43507177835`), Gradle Dependency Graph #183 (run ID `43507177822`), OSV Scanner Scheduled #38 (run ID `43507178272`), and Source SBOM #229 (run ID `43507177919`).
- GitHub PR search returned no open pull requests at this verification.
- No GitHub Releases are published (releases API returned an empty list). No signed APK/AAB or public APK download URL is available from this verification.
- These results close the PR #54 documentation/CI synchronization task only. Production signing/secrets and Environment approval, physical-device and accessibility QA, 3–4 GB RAM profiling, runtime 16 KB verification, Gradle artifact dependency verification, full MASVS/MASTG review, and branch protection remain outstanding as described above. Do not represent CI success as production release readiness.



## 2026-11-16 — Latest Main CI verification

- Verified Main SHA: `654e98b7c7ec975467b7c2af1781132eed46bb9b`.
- Latest Main runs: Android CI [#43845322821](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322821), Dependency Graph [#43845322967](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322967), Source SBOM [#43845322921](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322921), OSV Scanner Scheduled [#43845323444](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845323444): all completed with conclusion `success`.
- Android CI step evidence: shipping debug assemble, Android lint, app unit tests, Gecko policy unit tests, GeckoView/WebView benchmark APK and release AAB builds, minified shipping release AAB, static APK/native-library 16 KB alignment, artifact uploads and API 35 Android security instrumentation all passed.
- Uploaded artifacts: `rivo-browser-release-aab-unsigned`, `rivo-browser-arm64-debug`, `rivo-browser-armv7-debug`, `rivo-engine-benchmark-gecko`, `rivo-engine-benchmark-webview` and `rivo-android-test-reports`. These are temporary QA outputs; the shipping AAB is unsigned and is not a public release.
- OSV scanner output: `No issues found`. This covers the scanned dependency inputs only and does not establish full MASVS/MASTG compliance.
- PR #56 head SHA `fb38196f644c56560aebee91ddaf965900e0ff49` passed Android CI #43839170714, Dependency Graph #43839170834, OSV Scanner PR #43839171295 and Source SBOM #43839170716 before merge. PR #56 migrated artifact actions to v7.
- Log warnings: setup-android@v4 still invokes deprecated sdkmanager; `/dev/kvm` was unavailable on the hosted runner but API 35 instrumentation passed; OSV Scanner v2.7.1 emitted a deprecated `--output` CLI-option warning. Emulator/X11/QEMU messages were non-fatal. No Kotlin compilation, lint, unit-test, bundle, 16 KB static check or instrumentation failure was found. Legacy upload-artifact v4/Node 20 warnings were eliminated by the v7 migration.
- No GitHub Release is published. Production signing requires the four `RIVO_UPLOAD_*` secrets in the protected `production` Environment; presence/value and reviewer policy were not verifiable from this session. Physical-device, accessibility, low-RAM, runtime 16 KB and full security assessment remain separate release acceptance gates.


## 2026-11-17 — Android 16 KB and accessibility source audit
- Repository source audit: AGP is pinned to 9.5.1 (above the 8.5.1 packaging threshold); target SDK is 36; shipped ABIs include arm64-v8a and armeabi-v7a. No project-owned native build sources, CMakeLists.txt or Android.mk files exist, so an NDK version is not applicable to project compilation. GeckoView 159 contributes prebuilt native shared libraries.
- App and engine-benchmark packaging now explicitly sets `jniLibs.useLegacyPackaging = false`. Existing Android CI builds APK/AAB artifacts and runs `tools/verify_16kb_alignment.py`, which checks every packaged 64-bit native ELF PT_LOAD alignment (>=16 KB) and APK ZIP alignment using `zipalign -P 16`.
- Compose audit found no XML layout files. The unlabeled bookmark-remove glyph was given a contextual content description; appearance choices now expose RadioButton role/selected state and a 48 dp minimum row; Reader Mode slider has a name; dialog titles are semantic headings. Material 3 components retain their minimum interactive touch targets and normal hierarchy traversal order.
- Not claimed as passed: fresh CI for this commit, 16 KB hardware/runtime behavior, TalkBack traversal on-device, 320 dp layout, large-font scaling, and physical-device accessibility checks. Run these against the exact release candidate before acceptance.


## Security audit regression additions — 2026-11-23

### Deterministic hostile-input property tests

- `SecurityInputPropertyTest` exercises seeded malicious URI schemes, credentials, malformed ports, control characters, Unicode, separators and 512 deterministic generated omnibox inputs. Expected invariant: resolution always produces a parseable HTTP(S) URL with a host and without user-info; unsupported schemes are search queries rather than active protocol navigation.
- The same test exercises seeded path traversal, Windows path syntax, reserved staging names, control characters and 512 deterministic generated download names. Expected invariant: sanitized names contain no slash/backslash, are nonblank and bounded, are not dot path components, and cannot collide with the interrupted-download cleanup namespace.
- These are deterministic property-style JVM regression tests, not coverage-guided fuzzing. They do not fuzz GeckoView's native parser or Android platform intent dispatch.

### Existing CI security execution path

The Android CI workflow runs:
1. `tools/verify_security_static.py` manifest/network/bridge/TLS/Gecko/WebView guardrails.
2. Gradle dependency verification with `--dependency-verification strict`.
3. Android lint, app JVM tests and Gecko policy JVM tests.
4. Shipping app and benchmark builds, minified release bundle and static 16 KB alignment checks.
5. API 35 emulator instrumentation tests, including encrypted-storage and temporary-download cleanup coverage.

A workflow being configured is not evidence that the latest commit passed. Record the exact run URL and conclusion only after the run completes. No physical-device MASTG or independent penetration test is claimed by this test suite.

### Manual security test matrix still required

- GeckoView: invalid/expired/self-signed/hostname-mismatched certificates; HTTP redirects; active/passive mixed content; WebSocket; proxy/VPN; hostile popup and custom scheme handling.
- IPC: merged release manifest, malformed external intents, FileProvider URI grants/read-only access/revocation, notification PendingIntent behavior.
- Storage/privacy: backup/restore, Keystore key invalidation, tampered ciphertext, private tab process death/download cleanup, logs, clipboard, recents and screenshot snapshots.
- Permissions: deny/revoke location, camera, microphone and notifications; verify origin and active-tab binding.
- Release: inspect exact signed APK/AAB, manifest, signer certificate, checksums and SBOM.


### Mixed-content security-state regression

- `BrowserSecurityStatusPolicyTest` verifies certificate-exception precedence, active mixed content warning, passive mixed content disclosure, unknown mixed-content qualification, normal secure label and insecure label.
- GeckoView's runtime mixed-content behavior still requires HTTPS test fixtures containing HTTP script/style/frame/image/media/fetch resources and redirects. The UI policy unit test does not prove what Gecko blocks or loads.


## 2026-11-24 — PR #67 verification closeout (authoritative latest checkpoint)

- Audited source branch: `security/comprehensive-android-audit-2026-11-23`; verified PR head: `1cde602ae030b44c260770734205b0186deb4e28`; base Main: `0df6760287b0de5c07d2468b247145dae9c0ea4b`.
- GitHub Actions Android CI run [45242372450](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372450) completed successfully. Its build job completed all steps successfully, including strict Gradle dependency verification, static security configuration audit, shipping and benchmark builds, Android lint, JVM tests, Gecko policy tests, minified shipping release bundle, static 16 KB alignment checks, and API 35 emulator Android security instrumentation tests.
- PR-head supply-chain workflows also completed successfully: [Gradle Dependency Verification Bootstrap](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372194), [Source SBOM](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372086), [OSV Scanner](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372048), and [Gradle Dependency Graph](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242371982).
- The Medium mixed-content security-indicator finding is remediated in source and covered by JVM policy tests. CI success verifies the automated test path; GeckoView mixed-content fixture behavior on physical devices remains unverified.
- Correction to earlier preliminary audit wording: a follow-up review DID identify the Medium mixed-content status presentation issue; it is fixed in this PR. Do not interpret earlier “no confirmed Medium issue” wording as the final finding.
- PR #67 remains open and unmerged. The trusted reviewer `techsomadhanmedia` has write permission and is already requested as reviewer, but the PR currently has no submitted review/approval. Do not self-approve, bypass required review, or merge before independent approval and required checks.
- Scope boundary: successful emulator instrumentation is verified; physical-device MASTG/runtime tests, merged release manifest/IPC inspection, exact signed APK/AAB inspection, production signing/release, and independent penetration testing are NOT completed. This is not MASVS certification.
- Manual next gate: `techsomadhanmedia` must open PR #67, inspect the changes, and submit **Approve**. After approval, recheck all required checks and mergeability before merging. Physical-device checks must then be executed using the documented matrix before production security acceptance.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`.
- PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC; merge commit is the current Main SHA above.
- PR #66 governance hardening is merged. Active ruleset `Protect main branch` applies to `main`, requires a pull request, at least one approving review, CODEOWNERS review, resolved review threads and strict required check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- PR #64 signed-release workflow hardening is merged. PR #65 is closed unmerged. Live open-PR listing was empty at this checkpoint.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue by propagating GeckoView active/passive mixed-mode state into tab/UI status, qualifying unknown status, resetting status on navigation, and adding JVM regression/property tests. Runtime engine behavior still needs device/fixture verification.
- Post-merge workflows on this SHA: OSV Scanner Scheduled [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Gradle Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Gradle Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still running at the last captured check. Do not mark them successful until final conclusions are rechecked.
- Android CI steps already completed at last snapshot: security static audit, shipping assemble, lint, JVM unit tests and Gecko policy tests. Benchmark/release builds, static 16 KB artifact checks, QA uploads and Android security instrumentation were still pending/running; no result is claimed for them yet.
- Remaining release gates: final Main CI and dependency-verification results; protected production signing run and verification of signed APK/AAB, signer fingerprints and checksums; physical-device QA (including 3–4 GB RAM, accessibility, private-session/data lifecycle, TLS/mixed-content and runtime 16 KB where supported); complete MASVS/MASTG control assessment and independent penetration testing if required. CI success is not physical-device proof, production signing or certification. No MASVS certification or independent penetration test is claimed.


## Authoritative readiness checkpoint — 2026-11-24

- Main includes PR #68, squash-merged after CODEOWNER approval by `techsomadhanmedia`: https://github.com/techwithrakibofficial/Rivo-Browser/pull/68. Merge commit: `e44cae6fb2e9a34bcc40d6769e78626df1ea40c5`.
- Main ruleset `Protect main branch` is active. It requires pull requests, at least one approval, CODEOWNERS review, resolved review threads and strict required check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- No open pull requests were returned by the live repository query at this checkpoint.
- Post-merge Source SBOM [run 45251818989](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251818989) and OSV Scanner Scheduled [run 45251819550](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819550) completed successfully.
- Post-merge Android CI [run 45251819071](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819071) was still running at this checkpoint. Security static audit, shipping debug assemble, lint, app JVM tests, Gecko policy JVM tests and engine benchmark debug APK builds had succeeded; engine benchmark release bundle build was in progress. Do not claim the full Android CI, minified bundle, static 16 KB artifact verification, QA artifact uploads or Android instrumentation passed until the run concludes.
- Owner-reported device QA: the project owner states they installed and exercised the app on their Android phone and that the phone-based checks were successful. This is owner-reported acceptance, not independently reproduced evidence. It does not by itself establish runtime 16 KB compatibility on a dedicated 16 KB device or independent penetration-test completion.
- Production signing: the owner previously reported configuring four production signing secrets. This integration cannot read secret values or verify Environment protection. No signed production APK/AAB, signer fingerprint verification, checksum publication or GitHub Release is evidenced at this checkpoint. The signed-release workflow validates signer consistency before publishing, but no authorized version/tag execution has occurred.
- Remaining gates: final post-merge Android CI conclusion; authorized version/tag and production signing/release execution; independent penetration testing and any required control-by-control MASVS/MASTG assessment. No certification is claimed.


## Verified Main CI closeout — 2026-11-24 03:27 UTC

- Canonical Main revision for this checkpoint: `ecc1ce211c189d6c4e78aa58a7bb32d1d5b86380` (PR #69 squash merge).
- Main Android CI run [45318499605](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499605): **completed successfully**. Every job step succeeded, including security configuration static audit, shipping assemble, Android lint, app unit tests, Gecko engine policy tests, GeckoView/WebView benchmark builds, minified shipping release bundle, native library/APK 16 KB static alignment, QA artifact uploads and Android security instrumentation tests.
- Gradle Dependency Verification Bootstrap run [45318499764](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499764): **completed successfully**. Both metadata generation/strict validation and the follow-up metadata review job succeeded; verification metadata and lockfile artifacts were uploaded.
- Same-revision supply-chain workflows succeeded: OSV Scanner Scheduled [45318499840](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499840), Source SBOM [45318499754](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499754), and Gradle Dependency Graph [45318499608](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45318499608).
- CI artifacts include an **unsigned QA-only** AAB, ARM64/ARMv7 debug APKs, test reports and GeckoView/WebView benchmark outputs. These are not production-signed deliverables.
- The owner previously reported successful installation and phone-based checks. Record this as owner-reported device acceptance only; it is not independently reproduced laboratory evidence and does not establish 3–4 GB performance profiling, TalkBack/narrow-screen acceptance, or runtime 16 KB compatibility.
- The signed-release workflow is present and validates the production keystore, APK/AAB signer consistency and SHA-256 assets. The owner reports that the four production signing secrets were configured; this integration cannot inspect secret presence/values or Environment protection. No release tag/dispatch has been executed, no signed production APK/AAB has been generated, and no GitHub Release is published.
- The current GitHub integration exposes no tag-creation, workflow-dispatch, or release-creation action. The release workflow therefore cannot be triggered from this session; do not represent the unsigned CI AAB as a release.
- Remaining production-acceptance gates: execute the protected signed-release workflow from an owner-created reviewed version tag or GitHub Actions manual dispatch; verify downloaded signed APK/AAB, matching signer fingerprints and published checksums; complete the documented device matrix for 3–4 GB performance, accessibility, runtime 16 KB and GeckoView TLS/mixed-content/private-data behavior; complete independent penetration testing and control-by-control MASVS/MASTG assessment. No certification is claimed.


## Android QA evidence — 2026-11-24

- Run: [Android CI #628 / 45326960699](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45326960699)
- Revision: `3422fd94aa20f77de6a81bc61c6c4d09ba07a656`
- Result: **completed / success**; all workflow steps passed.
- Successful tasks: `:app:assembleDebug`, `:app:lintDebug`, `:app:testDebugUnitTest`, `:engine-gecko:testDebugUnitTest`, GeckoView/WebView benchmark APK and bundle tasks, `:app:bundleRelease`, static native ELF and APK ZIP 16 KB alignment verifier, and `:app:connectedDebugAndroidTest` on API 35 x86_64 emulator.
- Android instrumentation generated an XML test result and the report upload step completed. Checked-in test sources at this revision include `DownloadTempFileCleanupInstrumentedTest` and `LocalDataCipherInstrumentedTest`. Do not infer coverage of unrelated browser journeys from these tests.
- Artifacts: `rivo-browser-arm64-debug`, `rivo-browser-armv7-debug`, `rivo-browser-release-aab-unsigned`, `rivo-android-test-reports`, and GeckoView/WebView benchmark artifacts.
- 16 KB result is static package/native-library alignment evidence only; runtime on 16 KB hardware remains untested.
- No physical device was connected or tested by this run. No claim is made for 3–4 GB performance, TalkBack, OEM notifications, real-world site compatibility, full browser journey coverage, penetration testing or MASVS certification.


## 2026-11-24 — Main CI evidence after PR #72

- Main SHA 39c35e64ab4260adb7787fed919c29cd098ac40a: Android CI #45334566292 success; Gradle verification bootstrap #45334566241 success.
- Dependency Graph #45334566254, OSV Scanner #45334566666 and Source SBOM #45334566273: success.
- Verified workflow artifacts: ARM64/ARMv7 debug APKs, universal debug APK, unsigned QA AAB, Android test reports and Gecko/WebView benchmark outputs. The QA AAB is unsigned; no production signing is implied.
- Universal APK and static 16 KB alignment checks are merged and validated in Main CI. Static alignment does not prove runtime 16 KB compatibility on hardware.
- Not yet tested: physical-device browser journeys, 3–4 GB RAM, TalkBack/OEM behavior, runtime 16 KB, private-session/data lifecycle under process death, and GeckoView TLS/mixed-content fixtures. User has deferred device testing until production APK availability.
- Release remains pending: no version tag, production-signed APK/AAB, signer fingerprint/checksum publication or GitHub Release exists. Follow the signed-release workflow only from an owner-authorized Main-ancestral version tag; verify production Environment protection and downloaded release assets.


## 2026-11-24 — PR #75 and Main verification checkpoint

- PR #75 head `fcb92d06f99e1bfa4b1fa5aa15cf06586ad2872a` passed Android CI, Gradle dependency-verification bootstrap, OSV Scanner, Dependency Graph and Source SBOM before independent CODEOWNER approval and merge.
- PR #75 merged as `8f8bafd8ebfdec97c2fe1e24007feeabd8ff19c4`; the cleanup step was verified in Main source to remove `$RUNNER_TEMP/rivo-upload.p12`.
- Post-merge Main OSV Scanner [45360601078](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360601078), Dependency Graph [45360600467](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600467) and Source SBOM [45360600609](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600609) succeeded.
- Post-merge Android CI [45360600469](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600469) and Gradle Dependency Verification Bootstrap [45360600491](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45360600491) were in progress when recorded. No pass/fail conclusion is claimed for those two runs here.
- The CI evidence above does not test the production keystore. No signed APK/AAB, signer fingerprint proof or GitHub Release is available until the owner-triggered signed-release workflow completes.
- Physical-device journeys, 3–4 GB profiling, TalkBack/OEM behavior, runtime 16 KB hardware validation, GeckoView runtime security fixtures and complete MASVS/MASTG/independent penetration testing remain unverified.


## 2026-11-24 — PR #85 final validation
- PR #85 head: `a4356a6301cd1f2fa48725e2131b35cfee318a1c`; merge commit: `4821c7af1255486e25b98305fd2b74cde33f488d`.
- CODEOWNER review: APPROVED by `techsomadhanmedia`; review threads: none unresolved.
- Five PR workflows succeeded: Android CI (#45401723431), Source SBOM (#45401723326), OSV Scanner PR (#45401723339), Gradle Dependency Graph (#45401723368), Gradle Dependency Verification Bootstrap (#45401723346).
- Android CI build job passed security static audit, shipping build, lint, JVM tests, Gecko policy tests, benchmark/release bundle builds, static 16 KB alignment, and Android security instrumentation.
- Evidence boundary: PR-head CI/emulator only. No production signing, signed artifact verification, physical-device acceptance, runtime 16 KB hardware test or independent penetration test is evidenced.


## 2026-11-24 — PR #86 documentation validation
- PR #86 head `247aee5605727ee4b161971457c8d8984b8ffcd0` passed all five PR workflows: Android CI, Source SBOM, OSV Scanner PR, Gradle Dependency Graph and Gradle Dependency Verification Bootstrap.
- CODEOWNER `techsomadhanmedia` approved; review threads: zero unresolved.
- PR #86 merged to Main as `e15ee3ce1c623f70679b9c117018d98de0b1b757`.
- This validates the documentation PR checks only; no post-merge workflow result, signed release, physical-device acceptance or security certification is claimed.
