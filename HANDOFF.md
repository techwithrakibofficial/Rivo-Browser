# Rivo Browser — Handoff

Updated: 2026-11-23

## 2026-11-23 — Release CI reconciliation (PR #64 candidate)
- Live audit confirmed PR #64 and PR #65 target the same Main SHA (cfa94c3f26e8e478bb51adf1b3b60fc6ea871006) and overlap in the signed-release workflow and dependency verification metadata. PR #64 is the preferred candidate because it additionally verifies APK/AAB signer fingerprint consistency and exact AAB output cardinality.
- PR #64 dependency bootstrap run #45171080773 passed: generated SHA-256 metadata across shipping/test configurations, strict verification, and validated lockfile/metadata artifacts. The generated files already matched the PR branch; no redundant commit was created.
- Release workflow candidate now retains the R8 mapping artifact for 90 days, explicitly requires jarsigner to report a verified AAB, and downloads published release assets to re-check their SHA-256 checksums.
- PR checks remain pending owner approval for some workflows; this candidate is not merged and no production-signed build has run. Production Environment secrets remain owner-managed.
- Active Main ruleset Protect main branch currently requires a status check named b, which is absent from Main's observed check-run names; correct the required context to actual CI check names before merge. Do not bypass the ruleset.

## Exact next steps
1. Complete approval and execution of the remaining PR #64 workflows; review every check conclusion.
2. Fix the Main ruleset required-status context b using actual check-run names, then merge PR #64 only with protections satisfied.
3. Close PR #65 as duplicate after confirming PR #64 is integrated.
4. Configure the four production signing secrets in the protected production Environment and run the signed-release workflow against a reviewed, Main-ancestral tag.
5. Verify signed APK/AAB, signer fingerprints, mapping artifact retention and remote SHA-256 checksums; record actual signed-run evidence.


## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Latest verified Main SHA at this checkpoint: `cfa94c3f26e8e478bb51adf1b3b60fc6ea871006` (Main Android CI run #44038863782 succeeded).
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


## Serial 01 final handoff — 2026-11-13 18:02 UTC

### Verified GitHub state
- PR #50 merged to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- PR #51 merged to Main as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`.
- Both PRs were merged only after their final PR-head checks completed; PR #51 final head was `3c43a36dab28c72b8344491131a74bf51de6ae38`.

### Final PR #51 verification
- Android CI run 43476946517: `success` (shipping/minified build, lint, JVM tests, Gecko policy tests, benchmark builds, static 16 KB alignment, QA artifacts and API 35 security instrumentation).
- OSV Scanner PR run 43476947044: both jobs `success`; 814 package entries across five lockfiles, `No issues found`, lockfile drift clean and lockfile artifact uploaded.
- Dependency Graph run 43476946531 and Source SBOM run 43476946667: `success`.
- Separate `osv-scanner` check 132063311666: `neutral` due scheduled-workflow configuration-not-found notice; not counted as success.
- Signed-release YAML parsed successfully; seven shell `run` scripts passed `bash -n`; policy assertions confirmed default read-only permissions and publishing-only write permission. Actionlint was not available; no production workflow run was attempted.

### Owner/admin blockers
1. Configure GitHub Environment `production` required reviewers (prevent self-review) and restrict deployment refs/tags.
2. Store all four signing values as `production` Environment secrets, not repository-level secrets: `RIVO_UPLOAD_KEYSTORE_BASE64`, `RIVO_UPLOAD_STORE_PASSWORD`, `RIVO_UPLOAD_KEY_ALIAS`, `RIVO_UPLOAD_KEY_PASSWORD`.
3. Configure Main branch protection/ruleset requiring PR review and relevant checks; Main remains unprotected.
4. Add Gradle dependency verification metadata in a separately reviewed change; `gradle/verification-metadata.xml` and keyring are absent.
5. Run and verify an actual signed APK/AAB production release after owner configuration.

### Remaining release acceptance
- No signed production APK/AAB or GitHub Release was produced.
- Physical-device privacy/function QA, TalkBack/accessibility, 3–4 GB profiling, runtime 16 KB verification and full MASTG/data-flow review remain open.
- No claim of full assignment completion beyond the two PRs' implemented and verified CI changes.


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



## Session handoff — 2026-11-16 (Main CI verification)

### Verified state
- Main SHA: `654e98b7c7ec975467b7c2af1781132eed46bb9b`.
- PR #56 merged: https://github.com/techwithrakibofficial/Rivo-Browser/pull/56. Its PR-head Android CI, OSV Scanner PR, Dependency Graph and Source SBOM all passed.
- Latest Main-head workflows all passed: Android CI #43845322821, Dependency Graph #43845322967, Source SBOM #43845322921 and OSV Scanner Scheduled #43845323444.
- Android lint, app/Gecko unit tests, shipping and benchmark builds, minified release AAB, static 16 KB checks, QA uploads and API 35 security instrumentation all succeeded. OSV reported `No issues found`.
- PR #56 migrated artifact upload/download actions to v7; legacy artifact action Node 20 deprecation warnings are gone.

### Remaining warnings / gates
- Non-blocking warnings are upstream/environmental: setup-android v4's sdkmanager deprecation, missing hosted-runner KVM (instrumentation still passed), and OSV Scanner's internal deprecated `--output` option. Emulator diagnostics were non-fatal.
- No GitHub Release or signed production APK/AAB is published. Production Environment signing secrets and reviewer configuration require an authorized maintainer; this session cannot read or configure secret values.
- Physical-device and accessibility QA, 3–4 GB profiling, runtime 16 KB verification, Gradle dependency verification metadata and full MASVS/MASTG review remain open.

### Evidence links
- Main Android CI: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322821
- Main Dependency Graph: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322967
- Main Source SBOM: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845322921
- Main OSV scan: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/43845323444
- PR #56: https://github.com/techwithrakibofficial/Rivo-Browser/pull/56
