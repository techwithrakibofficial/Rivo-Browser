# Rivo Browser — Handoff

Updated: 2026-11-10

## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Latest validated Main CI head: `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`.
- PR #38 integrated Phase 2; PR #40 merged signed-release automation.

## Completed and verified
- Main-head Android CI run [#42763857175](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42763857175) passed all configured steps, including shipping build, lint, JVM tests, Gecko policy tests, GeckoView/WebView benchmark builds, minified release bundle, static 16 KB checks, QA artifact uploads and API 35 Android security instrumentation.
- Same-head OSV Scanner, Gradle Dependency Graph and Source SBOM workflows succeeded.
- QA artifacts uploaded: ARM64/ARMv7 debug APKs, unsigned AAB, Android test reports and engine benchmark artifacts.
- Phase 2 browser features remain integrated as described in PROJECT_STATE.md.
- Signed-release workflow exists and validates signatures before publishing; it has not yet been run with production signing credentials.

## Unfinished release gates
1. Configure protected Actions secrets `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, `RIVO_UPLOAD_KEY_PASSWORD`; run and verify a signed GitHub Release. These credentials are not accessible to this session.
2. Perform physical-device QA on 3–4 GB and modern Android devices.
3. Validate permission/notification/private-session/download lifecycle and reader/QR/transfer flows on device.
4. Validate TalkBack, 320 dp width, large fonts, contrast and Dark/Light/Glass appearance.
5. Record repeatable GeckoView/WebView memory, startup, page-load and battery measurements.
6. Test 16 KB runtime on compatible hardware.
7. Complete OWASP MASVS control mapping and dependency advisory triage.

## Known limitations / blockers
- No connected physical device is available through this session; hardware QA cannot be truthfully claimed.
- Production signing credentials are not accessible; never commit signing material.
- Transfer cancellation is best-effort; no pause/resume/retry.
- WebExtensions are not exposed to end users.
- CI APK/AAB artifacts are unsigned QA outputs.
- No MASVS certification or full anonymity/tracker-blocking claim.

## Exact next steps
- Complete the protected signing setup and signed release execution.
- Execute the manual/device test matrix and record device/build-specific evidence.
- Complete security assessment, resolve/record dependency advisories, and only then approve a production release.


## Governance finding
- GitHub currently reports Main as unprotected (`protected=false`, protection disabled, no required status checks). This is a production governance gap.
- The connected GitHub interface available in this session does not provide branch-protection/ruleset write operations; only read access is exposed for those endpoints. Do not claim the setting was changed.
- Owner/admin follow-up before release: enable a main-branch ruleset requiring PR review and successful Android CI, block force-push/deletion, and restrict release/tag creation.
- Documentation commit head `56b14acf0755d27c0f90035204ffef0a2502d493` has Android CI run #481 in progress at handoff; the preceding source CI run #480 was successful.

## Next security hardening package — website notification privacy
- Review PR for the lock-screen redaction change: `Notification.VISIBILITY_PRIVATE` plus a generic public notification version.
- Verify CI and inspect the notification builder diff.
- Device acceptance: confirm sensitive website title/body/origin are not shown on secure lock screens or screen sharing, while full content remains available after unlock according to system settings.
