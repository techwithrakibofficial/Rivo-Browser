# Rivo Browser — Handoff

Updated: 2026-11-13

## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Latest Main CI head: `45611c3a42ae14f82089f8ba49c5840a9548a0b6`; Android CI #485 completed successfully.
- PR #38 integrated Phase 2; PR #40 merged signed-release automation.

## Completed and verified
- Main-head Android CI run [#42765445314](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42765445314) passed all configured steps, including shipping build, lint, JVM tests, Gecko policy tests, GeckoView/WebView benchmark builds, minified release bundle, static 16 KB checks, QA artifact uploads and API 35 Android security instrumentation.
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
- Latest Main-head Android CI #485 completed successfully.


## Current PR #43 — lazy restored background tabs
- Branch: `perf/lazy-restored-tab-loading-2026-11-10`.
- Restored tabs are created without loading every saved URL; only the selected restored tab loads at startup. Other restored tabs load on first selection. New tabs remain unchanged.
- Added JVM coverage in `RestoredTabSelectionTest.kt` for selected/background/pending load decisions.
- Android CI run [#42771971849](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42771971849) completed successfully (build, lint, JVM tests, Gecko policy tests, benchmark builds, minified bundle, static 16 KB checks and API 35 instrumentation). OSV Scanner, Source SBOM and Dependency Graph runs also succeeded.
- PR #43 remains open and unmerged. No physical-device performance measurement is claimed.


## 2026-11-11 integration update
- PR #43 was merged as `7c24dd337ed96f0d8bbc70831a9e88fa8bf6ad99`; lazy background-tab restoration is now in Main.
- Consolidation branch `integration/security-release-gates-2026-11-11` carries website notification public-preview redaction, its JVM regression test, and the preliminary OWASP MASVS register. Fresh CI is required before this branch can merge.
- PR #44 and #45 became non-mergeable after Main advanced; their changes are being consolidated on the integration branch rather than force-merging stale heads.
- Hard blockers not executable from this session: production keystore secrets, physical-device test access, and repository branch-protection mutation. Never claim these completed without evidence.


## Authoritative handoff — 2026-11-11 13:09 UTC

- Canonical `main` is `b50350a241908fb1f38d2920b7a358451cb436f7`.
- PR #46 is merged: https://github.com/techwithrakibofficial/Rivo-Browser/pull/46. It includes website notification public-preview redaction, its JVM regression test, preliminary MASVS register and synchronized release-readiness docs.
- PR-head checks all passed: Android CI #43024973140, OSV Scanner PR #43024973788, Gradle Dependency Graph #43024973190, Source SBOM #43024973177.
- Android CI passed build, lint, JVM tests, Gecko policy tests, benchmark builds, static 16 KB alignment and API 35 emulator security instrumentation. QA artifacts: ARM64 debug APK `13857117441`, ARMv7 debug APK `13857660007`, unsigned QA AAB `13857067490`, test reports and benchmarks.
- Post-merge Main CI runs were still in progress at checkpoint; verify them before reporting green Main CI.
- PR #43 is merged as `7c24dd337ed96f0d8bbc70831a9e88fa8bf6ad99`.
- Remaining blockers: protected production signing secrets (four `RIVO_UPLOAD_*` secrets), physical-device and accessibility acceptance, 3–4 GB profiling, runtime 16 KB test, complete MASTG/data-flow review and branch-protection/ruleset configuration. No physical device or signing material is accessible in this session.
- Do not distribute unsigned CI artifacts as a production release. Run `.github/workflows/signed-release.yml` only after authorized signing secrets are configured.

> This handoff supersedes earlier text below claiming PR #43 or the security integration PR was still open/unmerged.


## Session handoff — 2026-11-13

### Verified repository state
- Main SHA: `5845e1b7804ad2cb102af16acd58af4b77aec3cb`.
- No open PRs were returned by GitHub PR search.
- Main Android CI #510 passed on that SHA.
- Same-SHA OSV Scanner Scheduled #31, Gradle Dependency Graph #149 and Source SBOM #195 passed.
- Main remains unprotected; repository rulesets query returned no rulesets.
- README was found stale (still described PR #38 as latest and an older Main SHA); this branch updates it to the PR #46/current-Main truth.

### Work completed in this session
- Re-audited current Main metadata, open PRs, recent commits, Actions runs, signed-release workflow and branch/ruleset state.
- Prepared documentation-only release-readiness synchronization on branch `docs/release-readiness-checkpoint-2026-11-13`.
- No application source code or release credentials were changed.

### Still unfinished / blockers
1. Configure the four protected production signing secrets in GitHub Actions; secrets are not readable/writable through this session.
2. Run the signed-release workflow for an approved version tag and verify APK/AAB signatures and SHA-256 assets.
3. Complete physical-device functional and privacy QA.
4. Complete TalkBack, 320 dp, large-font, contrast and all-theme accessibility validation.
5. Profile startup/memory/page load/battery on real 3–4 GB devices.
6. Verify 16 KB runtime on compatible hardware.
7. Complete manual MASTG/data-flow/privacy review and dependency advisory triage.
8. Enable main branch ruleset/branch protection with PR review and required CI checks; no rulesets are currently present and this integration cannot write the protection endpoint.

### Exact next steps
- Review and merge this documentation PR after CI.
- An authorized repository maintainer configures signing secrets and branch protection.
- Run signed release workflow, then verify published release assets and checksums.
- Execute the device test matrix and attach reproducible device/build evidence before marking release gates complete.

### Evidence boundary
Automated CI success is verified. It is not evidence of physical-device acceptance, runtime 16 KB behavior, full MASVS compliance or signed production publication.


## Serial 01 handoff — 2026-11-13 17:34 UTC

### Verified baseline
- Main SHA: `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af`; Main Android CI run #513 / 43470442172 succeeded.
- PR #50: https://github.com/techwithrakibofficial/Rivo-Browser/pull/50
- PR #51: https://github.com/techwithrakibofficial/Rivo-Browser/pull/51
- Both PRs were open and mergeable at inspection; no merge was performed.

### Changes prepared on the PR branches
- PR #50: scoped `security-events: write` to the OSV reusable scan job; the workflow default and Gradle resolution job remain read-only. Checkout credentials are not persisted; the workflow does not push; lockfile drift and artifacts are handled explicitly.
- PR #51: split signed build and publication so only the publisher has `contents: write`; signing uses `contents: read`, owner-only execution and the `production` environment. Added tag format validation, Main ancestry verification, signature checks, SHA-256 manifest and verified-artifact handoff.

### Checks and blockers
- Earlier PR heads had Android build, OSV scan, dependency graph and SBOM successes. One OSV check was `neutral`, not success.
- Final commits changed after those checks; wait for and inspect new-head checks before merging.
- Owner/admin must configure production environment reviewers (prevent self-review), allowed branch/tag policy, and four protected signing secrets. Secret values must never be exposed.
- Main remains unprotected; the connected integration does not expose branch-protection/environment mutation. No signed production run, APK/AAB release, or physical-device QA is claimed.

### Exact next steps
1. Verify final PR #50/#51 head SHAs and every latest check/job result.
2. Fix any failing or pending checks; merge #50 only when safe.
3. Rebase/update #51 against the new Main, rerun checks, then merge only when safe.
4. Recheck Main CI and all security workflows after each merge.
5. Owner/admin configures production environment protections and signing secrets; run and independently verify a signed release.
