# Rivo Browser — Testing and Evidence

Updated: 2026-11-09

## Validation levels
Keep PR-head CI, Main push CI, emulator instrumentation and physical-device QA distinct. A green build is not equivalent to manual device acceptance.

## Main baseline
- Prior Main-integrated browser, tab, storage, theme and download changes have passed their recorded PR-head build/lint/JVM checks.
- Advanced Tab Universe policy regression tests are integrated.
- No current physical-device or 3–4 GB RAM performance result is claimed.

## Source PR evidence
- PR #35 exposed GeckoView API compile issues and malformed AndroidManifest metadata. The GeckoView builder call and nullable extraction handling were corrected; the ML Kit metadata element was moved inside the application element.
- PR #36 Android CI passed assemble, lint, JVM tests, Gecko policy tests, benchmark APK/AAB builds, minified release bundle, static 16 KB alignment and QA artifact uploads.
- PR #36 API 35 emulator instrumentation failed at `:app:connectedDebugAndroidTest`. The workflow log confirms failing tests but did not expose the failing test name/assertion; its XML/HTML report was not uploaded in that source run.
- PR #36 source SBOM retry succeeded. Verify the exact artifact and resolved dependency graph before claiming dependency coverage.
- OSV Scanner results must be reviewed for package-source/lockfile coverage; skipped or neutral is not a clean scan.

## Integration PR #37
- PR #37 combines the two source workstreams into one integration candidate.
- Android CI workflow now uploads `app/build/reports/androidTests/connected/debug` and connected-test XML reports even when instrumentation fails.
- Exact-head Android CI must be checked after the latest integration commit. Required tasks include shipping assemble, lint, JVM tests, Gecko policy tests, benchmark APK/AAB builds, minified release bundle, static 16 KB alignment, artifact upload and API 35 instrumentation.
- Source SBOM and Gradle dependency graph workflows must complete on the same exact head.
- If instrumentation fails, inspect the uploaded `rivo-android-test-reports` artifact and fix the exact failing assertion before merge.

## Manual/device QA still required
- Process death and normal-tab restoration; duplicate URLs; private-tab exclusion.
- Keystore round-trip, tampering, missing/lost key and DataStore corruption/relaunch.
- Site permission grant/deny/revoke and Android permission flows.
- Website notifications, notification channel disabled state and private/insecure-origin rejection.
- Download confirmation, path traversal, filename sanitization, size cap, cancellation, private file cleanup, open/share/remove.
- Reader Mode gated/non-readerable/stale extraction and long-content handling.
- QR scanner availability, malformed/oversized payload and user confirmation.
- Dark/Light/Glass readability, overlays, 320 dp width, short screens, large fonts, TalkBack and reduced motion.
- ARM64/ARMv7 smoke tests and 3–4 GB RAM performance/memory benchmarks.
- 16 KB runtime behavior on compatible hardware.

## Artifact policy
Unsigned CI AABs are QA artifacts only. Do not publish or sign a production release until all security, device, accessibility and signing gates pass.
