# Rivo Browser — Testing and Evidence

Updated: 2026-11-10

## Evidence levels
- Source review is not a test.
- JVM tests are not Android instrumentation.
- Emulator instrumentation is not physical-device acceptance.
- Static 16 KB alignment checks are not runtime verification.
- Unsigned QA artifacts are not production release artifacts.
- The signed-release workflow is not validated until a real signed build completes and GitHub Release assets are verified.

## Phase 2 integration evidence
- Source tree: `9115b9d3c607353facc15ed9b6fb4e3e53ce696d`.
- Android CI run #463 on source commit `f3abbc77cbfa7b1a0d6e1fcc46b20bbb4aee0116` passed configured shipping assemble, lint, app JVM tests, Gecko engine policy tests, benchmark builds, minified release bundle, static 16 KB checks, artifact uploads and API 35 instrumentation.
- PR #38 uses the identical source tree. Its SBOM run #151, dependency graph run #105 and OSV Scanner PR run #147 completed successfully.
- Post-PR #40 Main runs: Android #42763687243 and dependency graph #42763687505 were in progress at documentation time; SBOM #42763687253 and OSV #42763687788 succeeded. Record final conclusions after completion.
- Signed release workflow: `.github/workflows/signed-release.yml`, merged via PR #40. No signed-release runtime test has yet occurred because protected production signing secrets are not available in this session.

## Required manual/device matrix
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

## Reporting template
Record device model, Android/API level, build SHA, test case, steps, expected result, actual result, pass/fail and logs/screenshots. Do not mark a case passed without evidence.
