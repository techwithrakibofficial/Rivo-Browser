# Rivo Browser — Handoff

Updated: 2026-11-10

## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Latest validated Main CI head: `45611c3a42ae14f82089f8ba49c5840a9548a0b6` (Android CI #485 passed).
- PR #38 integrated Phase 2; PR #40 merged signed-release automation.

## Completed and verified
- Main-head Android CI run [#485](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42765445314) passed on `45611c3a42ae14f82089f8ba49c5840a9548a0b6`, including shipping build, lint, JVM tests, Gecko policy tests, GeckoView/WebView benchmark builds, minified release bundle, static 16 KB checks, QA artifact uploads and API 35 Android security instrumentation.
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
7. Complete the preliminary MASVS register with MASTG evidence, dependency advisory triage and privacy/data-flow review.

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
- Main Android CI #485 passed on `45611c3a42ae14f82089f8ba49c5840a9548a0b6`.
- PR #43 remains open (not merged); its Android CI #497 passed on branch head `b4e08a5fd8c25ba30632ad5f7ba223e28115842f`.


## Current work in review
- PR #43: lazy initial navigation for restored background tabs; CI #497 passed. Not merged into Main.
- A separate preliminary OWASP MASVS assessment/documentation change is in progress on a review branch. It must not be described as a completed security audit.
