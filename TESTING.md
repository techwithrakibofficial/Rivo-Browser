# Rivo Browser — Testing and Evidence

Updated: 2026-11-10

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
