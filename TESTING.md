# Rivo Browser — Testing and Evidence

Updated: 2026-11-13

## Evidence levels
- Source review is not a test.
- JVM tests are not Android instrumentation.
- Emulator instrumentation is not physical-device acceptance.
- Static 16 KB alignment checks are not runtime verification.
- Unsigned QA artifacts are not production release artifacts.
- The signed-release workflow is not validated until a real signed build completes and GitHub Release assets are verified.

## Main-head automated CI evidence
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
