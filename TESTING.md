# Rivo Browser — Testing and Evidence

Updated: 2026-11-13

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
