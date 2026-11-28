## 2026-11-28 — v0.2.7-phase1 release candidate

- Bumped app version to `0.2.7-phase1` / versionCode 8 because `v0.2.6-phase1` is already published; this UI candidate must not reuse an immutable release tag.
- Opening Tab Universe now captures the active normal tab preview as well as previously visited tabs; private tab snapshots remain disabled.
- Home glow animation now respects Android's system animator-enabled setting and becomes static when system animations are disabled.
- Signed release requires merge to Main, then creation/push of immutable tag `v0.2.7-phase1`; the GitHub connector available in this session cannot create/push a tag or dispatch a workflow.

- Tab snapshot lifecycle now recycles replaced/closed preview bitmaps; private-tab snapshots remain disabled.

- Extended Settings with persisted JavaScript enable/disable and Global Privacy Control (GPC) controls, applied through GeckoRuntimeSettings; changing either reloads the active page.

## 2026-11-28 — PR #107 browser UI continuation

- Added animated RIVO home branding above the centered Google search field, with a violet/blue gradient emblem and subtle breathing glow.
- Changed the Desktop site state indicator to a checkmark only when enabled; disabled state has no trailing indicator.
- Added persistent GeckoView global text zoom (80–200%) with quick decrease/increase/reset actions in the overflow menu and a Settings slider.
- Replaced the tab list with a four-column tab overview grid. Normal tabs capture a downscaled in-memory GeckoView screenshot when switched away; private tabs deliberately do not capture or display page screenshots.
- Added a Settings hub linking existing Appearance, Privacy Intelligence Center, Transfer Studio and clear-browsing-data actions. Search engine remains Google; unimplemented engines/options are not shown as selectable.
- Updated the Dark and Premium Glass semantic palettes toward the flagship violet/blue visual direction.
- Candidate changes remain unmerged and require fresh CI, accessibility, visual and device QA. No signed APK or physical-device result is claimed.

## 2026-11-28 — v0.2.6-phase1 Chrome-inspired home and toolbar candidate

- Replaced the previous generic Rivo start page with a Chrome-inspired Google search start page, shortcuts, bookmarks and recent history.
- Set Google Search as the default search provider for plain-text input; direct HTTP(S) URL handling remains unchanged.
- Fixed the new-tab `about:blank` regression by preserving the internal native-home marker when Gecko reports its initial empty document.
- Removed the persistent RIVO brand row from the top browser display; moved Reload/Stop to the upper toolbar and replaced the bottom Reload control with a New tab (+) action.
- Reduced omnibox height and visual weight; introduced silver translucent Premium Glass surfaces and restrained motion that respects Android's animator-disabled setting.
- App version candidate: `0.2.6-phase1` / versionCode 7.
- Source branch: `feat/chrome-style-google-home-glass-ui`. Merge and CI/device acceptance are pending; no APK is published for this candidate.

## 2026-11-28 — v0.2.5 signed release published and verified

- PR #104 merged; documentation baseline Main commit: `3a284aa9b11dd4e43d8dde6f56baaabea276c84d`.
- Immutable annotated tag `v0.2.5-phase1` resolves to source commit `4d344128fcecac3b64476a71606b0e05d92051f9` (PR #103 crash-remediation source).
- Signed Android Release workflow run #46111644854 completed successfully: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46111644854.
- Both release jobs passed. Build job verified production signing configuration, signed/minified ARM64 and ARMv7 APKs, signed AAB, signer identity/signatures, retained R8 mapping, and removed temporary keystore. Publish job uploaded verified assets.
- Retained workflow artifacts: signed release bundle (artifact ID `15250660159`) and R8 mapping (artifact ID `15249902905`); both reported unexpired at verification time.
- Public release: https://github.com/techwithrakibofficial/Rivo-Browser/releases/tag/v0.2.5-phase1. Assets: `app-arm64-v8a-release.apk`, `app-armeabi-v7a-release.apk`, `app-release.aab`, and `SHA256SUMS`.
- PR #104 documentation merge and subsequent Main workflows #46112116262 (Android CI), #46112116297 (dependency verification bootstrap), #46112116318 (Gradle Dependency Graph), #46112116254 (Source SBOM), and #46112116518 (OSV Scanner) completed successfully. Main SHA at this checkpoint is `3a284aa9b11dd4e43d8dde6f56baaabea276c84d`.
- Physical TECNO KM9 (Android SDK 36) cold-start/repeated-launch/QR-scan acceptance remains **not yet evidenced**. Do not state the device-specific startup crash is confirmed fixed until this test passes.
- Remaining release-quality gates: physical-device feature QA, 3–4 GB memory/performance profiling, TalkBack and large-font/accessibility checks, runtime 16 KB validation on target hardware, and a documented MASVS/MASTG assessment. No independent penetration test or MASVS certification is claimed.

---

## 2026-11-28 — v0.2.5-phase1 source changes (PR #103)

- Deferred Google ML Kit Code Scanner initialization until the user selects Scan QR code, preventing QR scanner initialization from running during initial BrowserScreen composition.
- Added handling for synchronous scanner initialization errors with a user-facing notice.
- Crash diagnostics now print the actual `BuildConfig.VERSION_NAME`, replacing stale hard-coded version text.
- App version bumped to `0.2.5-phase1` / versionCode 6.
- PR #103 merged to Main as `4d344128fcecac3b64476a71606b0e05d92051f9`; all PR-head CI and supply-chain workflows passed.
- Signed v0.2.5 release and TECNO KM9 physical-device confirmation are still pending.

## 2026-11-24 — Release version alignment candidate

- Audited the existing `v0.2.2-phase1` tag: it points to the pre-fingerprint-parser-fix revision and its app metadata reports `0.2.1-phase1` / version code 3.
- Do not use or move that immutable tag for production signing.
- This PR aligns the next candidate to `0.2.3-phase1` / version code 4. Create the new tag only after merge and successful CI.
- The signed-release workflow on Main contains the corrected apksigner fingerprint parser. Manual dispatch must be started from Main with the new tag as input.
- No production-signed artifacts or published release are claimed.

# Changelog

## 2026-11-25 — Launcher icon
- Added the Rivo adaptive launcher icon (background, foreground, Android 13 themed monochrome layer; round variant) and wired `android:icon` / `android:roundIcon` in the manifest. minSdk is 26, so no legacy PNG fallbacks are needed.
- Brand source files (SVG, 512 px Play Store PNG) live in `docs/design/brand/`.
- Verification: XML well-formedness and a rendered preview of the artwork only. Build/lint come from CI; on-device appearance (launcher masks, themed icon) still needs a device check.

## 2026-11-24 — Release workflow verification follow-up
- PR #83 documentation reconciliation merged after CODEOWNER approval.
- Post-merge audit identified that `gradle clean` before AAB assembly removes the APK intermediate directory; the subsequent verification must use the APKs already preserved in `dist/`.
- The correction passed all five CI workflows and CODEOWNER review on PR #84. PR #85 reapplies it against latest Main to satisfy strict branch freshness; its own checks/review are pending.
- No production-signed artifacts or GitHub Release are claimed.


## 2026-11-24 — PR #82 release-build fix integrated
- PR #82 merged to `main` as `5ac3a6853cd20df47813acc580fcae2dbb34cbe9`; the signed-release workflow now builds ABI-split APKs and the AAB in separate Gradle invocations to avoid the AGP shrunk-resource collision.
- PR #82 head CI passed Android CI, OSV Scanner PR, Gradle Dependency Graph, Source SBOM and Gradle dependency-verification bootstrap.
- Post-merge Main: OSV Scanner Scheduled, Source SBOM and Gradle Dependency Graph passed; Android CI and Gradle dependency-verification bootstrap were still running at this checkpoint.
- Previous signed-release run #45385076282 failed at `:app:buildReleasePreBundle` before this fix; it is not evidence of a successful release. Re-run the workflow using the corrected workflow from current `main` and tag input `v0.2.1-phase1`.
- No production-signed APK/AAB or GitHub Release is claimed. Physical-device QA and full security/accessibility/performance acceptance remain open.


## 2026-11-23 — Release CI hardening candidate
- PR #64 is the preferred reconciliation candidate over overlapping PR #65; it validates production keystore/alias configuration, signed APK/AAB verification, R8 mapping output, signer fingerprint consistency, and retry-safe release publishing.
- Dependency verification bootstrap and strict validation passed on PR #64 (Actions run #45171080773); generated metadata and lockfiles already matched the branch.
- Follow-up hardening retains the R8 mapping artifact for 90 days, requires an explicit verified AAB result, and re-downloads published assets to verify SHA-256 checksums.
- PR remains unmerged pending remaining workflow approvals and correction of the Main ruleset's invalid required check context b. Production signing secrets and a signed release run remain outstanding.


## 2026-11-10 — Main CI and dependency validation
- Main Android CI run #42763857175 passed on `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`: shipping build, lint, unit tests, Gecko policy tests, GeckoView/WebView benchmark builds, minified release bundle, static 16 KB alignment checks and API 35 Android security instrumentation.
- QA artifacts uploaded: ARM64 and ARMv7 debug APKs, unsigned QA AAB, Android test reports and engine benchmark artifacts.
- Same-head OSV Scanner scanned five Gradle lockfiles (814 resolved package entries) and reported **No issues found**; dependency graph and source SBOM workflows succeeded.
- These automated checks do not replace physical-device acceptance, low-RAM profiling, 16 KB runtime verification or a full OWASP MASVS assessment.

## 2026-11-10 — Phase 2 integration merged
- Merged PR #38 (Reader Mode, QR, Transfer Studio, Developer Mode, site permissions, Privacy Intelligence Center, website notifications and private-download hardening) into Main.
- Reconciled the integration candidate with latest Main history using a two-parent commit; the combined source tree is `9115b9d3c607353facc15ed9b6fb4e3e53ce696d`.
- Preserved Gecko remote debugging and console output as disabled.
- Added resolved Gradle dependency lockfiles and dependency graph/OSV/SBOM workflow improvements.
- Exact source tree passed Android CI run #463 on its PR #37 head. PR #38 SBOM, dependency graph and OSV workflows passed.
- Physical-device QA, low-RAM profiling, 16 KB runtime verification, MASVS assessment and production signing remain outstanding.

## 2026-11-10 — Signed release automation
- Merged PR #40: `.github/workflows/signed-release.yml` builds signed ABI APKs and AAB, verifies signatures, creates SHA-256 checksums and publishes assets for a version tag or manual dispatch.
- Workflow requires four protected Actions secrets; no keystore or credential is stored in Git.
- Physical-device QA and signed-release execution remain outstanding.

## Earlier changes
See Git history for prior Tab Universe, storage hardening, browser navigation, theme and security increments.


## 2026-11-10 — Repository governance audit
- Verified GitHub reports `main` as unprotected, with no required status checks enforced by branch protection.
- Recorded required governance remediation in PROJECT_STATE.md, HANDOFF.md and SECURITY.md. The available GitHub integration does not expose branch-protection write operations, so the setting remains unchanged.
- Latest Main-head Android CI run #485 completed successfully.


## 2026-11-10 — Lazy background-tab restoration (PR #43)
- Changed process restoration so saved background tabs are reconstructed without immediately navigating every saved URL; the selected tab loads at startup and other restored tabs load on first selection.
- Preserved immediate loading for user-created tabs and added JVM tests for the pending restored-tab load policy.
- PR Android CI #496 passed, including JVM tests, lint, shipping/benchmark builds, minified bundle, static 16 KB checks and API 35 instrumentation. Physical 3–4 GB RAM profiling remains outstanding.


## 2026-11-11 — Release-readiness integration
- Merged PR #43: restored background tabs defer navigation until first selection, reducing avoidable startup work; physical low-RAM profiling remains outstanding.
- Integrated website notification public-preview redaction using `VISIBILITY_PRIVATE` and generic Rivo-owned public content, with a JVM regression test.
- Added a preliminary source-based OWASP MASVS control gap register in `SECURITY_ASSESSMENT.md`; it is not a certification or independent audit.
- Physical-device QA, production signing, runtime 16 KB validation and branch protection remain release gates.


## 2026-11-11 — Security/release-readiness PR #46 merged
- Squash-merged PR #46 to Main as `b50350a241908fb1f38d2920b7a358451cb436f7`.
- Website-controlled notification previews now use private visibility and a generic Rivo-owned public version; added `WebNotificationPrivacyPolicyTest`.
- Added preliminary source-based `SECURITY_ASSESSMENT.md` MASVS gap register; it is not a penetration test or certification.
- All PR-head CI workflows passed: Android CI, OSV Scanner PR, Gradle Dependency Graph and Source SBOM. Android CI included lint, JVM tests, Gecko policy tests, static 16 KB checks and API 35 emulator security instrumentation.
- Uploaded ARM64/ARMv7 debug APKs and unsigned QA AAB; these are not production-signed releases.
- Post-merge Main workflows were still running at the time of this entry. Production signing, physical-device QA, low-RAM profiling, runtime 16 KB verification and branch protection remain open.


## 2026-11-13 — Security CI and signed-release workflow hardening (Serial 01)
- PR #50 hardens OSV PR scanning: Gradle dependency resolution is read-only, checkout credentials are not persisted, workflow pushes are removed, lockfile drift is surfaced, and resolved lockfiles are uploaded. OSV SARIF write access is scoped to the reusable scanner job.
- PR #51 separates production signing/build from publication; only the publishing job receives `contents: write`. The signing job is owner-gated, uses the `production` environment, verifies Main ancestry and APK/AAB signatures, and prepares SHA-256 checksums.
- Main Android CI #513 succeeded on `5b32fcf67ca69f264e4e90fb6d95e8a6042b72af`. Earlier PR heads passed relevant Android/supply-chain checks; one OSV status was `neutral`, and latest amended heads require fresh CI.
- No production signing secrets, signed release, or APK/AAB publication is claimed. Environment reviewer policy, production Environment-scoped secret configuration (not repository-level secrets), branch protection and physical-device QA remain release gates.


## 2026-11-13 — PR #50 merged; PR #51 synchronized
- Squash-merged PR #50 to Main as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- Verified PR #50 Android CI #521, OSV introduced/resolved dependency scans, Dependency Graph and Source SBOM succeeded. Five lockfiles (814 package entries) reported no OSV issues; lockfile drift check and artifact upload succeeded.
- The separate `osv-scanner` status was `neutral` due a configuration-not-found notice and is not reported as success.
- PR #51 remains open and is synchronized with the new Main base; its final checks must complete before merge.
- Production Environment reviewers, Environment-scoped signing secrets, branch protection, physical-device acceptance and a real signed release remain outstanding.


## 2026-11-13 — PR #51 merged (Serial 01)
- Squash-merged PR #51 to Main as `89d0b2beb5ccdaf6bf265fd222418a65fcba100a`; PR #50 had already merged as `d93f598464a48351ca0440ad2cfeaa768e55d3f9`.
- Final PR #51 Android CI, OSV introduced/resolved scans, Dependency Graph and Source SBOM succeeded. OSV scanned 814 package entries with no issues; lockfile drift was clean and the artifact was uploaded.
- The separate `osv-scanner` check was `neutral` due scheduled-workflow configuration-not-found; it is not reported as success.
- Main post-merge OSV Scanner and Source SBOM succeeded; Android CI and Dependency Graph were running at the checkpoint.
- Production Environment reviewers/secrets, branch protection, Gradle dependency verification, physical-device QA and an actual signed release remain outstanding.


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



## 2026-11-16 — CI artifact actions and Main verification
- PR #56 merged the GitHub Actions artifact migration to upload-artifact/download-artifact v7.
- Verified PR #56 checks and all four latest Main-head workflows passed on Main SHA `654e98b7c7ec975467b7c2af1781132eed46bb9b`.
- Main Android CI passed lint, app/Gecko unit tests, shipping and benchmark builds, minified release AAB, static 16 KB alignment, artifact uploads and API 35 security instrumentation.
- Main OSV scan reported `No issues found`; dependency graph and source SBOM succeeded.
- Remaining sdkmanager, KVM/emulator and OSV CLI option warnings are upstream/environmental and non-blocking. No signed production release is claimed; signing and manual/device release gates remain open.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`. PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC.
- Active Main ruleset `Protect main branch` requires PR review, at least one approval, CODEOWNERS review, resolved threads and strict check `build`; branch deletion/non-fast-forward updates are blocked and no bypass actors exist.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue in source and adds JVM regression/property tests. GeckoView runtime mixed-content behavior remains unverified on physical devices/fixtures.
- On this Main SHA, OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still in progress at the last captured check; do not claim success before rechecking final conclusions.
- Remaining release gates: production-signed APK/AAB and signer/checksum verification; physical-device QA, accessibility and 3–4 GB RAM profiling; runtime 16 KB verification; full MASVS/MASTG assessment and independent penetration testing where required. CI does not replace these gates; no certification is claimed.


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


## 2026-11-24 — Android QA CI closeout

- Android CI run [#45326960699](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45326960699) completed successfully on Main `3422fd94aa20f77de6a81bc61c6c4d09ba07a656`.
- Passed security static audit, strict dependency verification, shipping debug build, lint, app/Gecko JVM tests, GeckoView/WebView benchmark builds, minified release AAB, static 16 KB alignment and API 35 x86_64 emulator instrumentation.
- Uploaded ARM64/ARMv7 debug APKs, unsigned QA AAB, test reports and benchmark artifacts.
- No production-signed release or physical-device QA is implied by this CI closeout.


## 2026-11-24 — Main CI and QA artifact closeout

- Merged PR #72: universal no-ABI-split debug APK build, static 16 KB alignment check and artifact publication.
- Main Android CI #45334566292 and Gradle verification bootstrap #45334566241 passed on 39c35e64ab4260adb7787fed919c29cd098ac40a; Dependency Graph #45334566254, OSV #45334566666 and Source SBOM #45334566273 also passed.
- CI artifacts include ARM64/ARMv7 debug APKs, universal debug APK, unsigned QA AAB, Android test reports and engine benchmark outputs. No production-signed APK/AAB or GitHub Release is available yet.
- PR #66 governance hardening is merged; .github/CODEOWNERS and active one-approval/code-owner ruleset are in Main.
- Remaining: owner-authorized version tag and signed-release workflow; verify signatures/fingerprints/checksums. Physical-device QA is deferred until the production APK is ready; low-RAM, accessibility, runtime 16 KB and complete MASVS/MASTG assessment remain open.


## 2026-11-24 — Signed-release workflow reconciliation

- PR #75 merged to Main as `8f8bafd8ebfdec97c2fe1e24007feeabd8ff19c4` after CODEOWNER approval.
- Fixed temporary signing keystore cleanup: the workflow now removes the actual `rivo-upload.p12` PKCS#12 file rather than a stale `.jks` path.
- PR #74's Base64 normalization (whitespace and URL-safe alphabet) and strict decode validation are present on Main.
- Post-merge OSV Scanner, Dependency Graph and Source SBOM passed. Android CI and Gradle dependency-verification bootstrap were still in progress at the checkpoint; their final outcomes must be rechecked.
- No production-signed APK/AAB or GitHub Release has been created. A new reviewed version tag is required; the old `v0.2.0-phase1` tag predates these workflow fixes and must not be moved.


## 2026-11-24 — PR #85 release verification fix merged
- Merged PR #85 as `4821c7af1255486e25b98305fd2b74cde33f488d` after CODEOWNER approval and zero unresolved review threads.
- Fixed APK verification after AAB build's `gradle clean`: validate APKs already preserved in `dist/`, not the deleted APK build directory; removed duplicate copying.
- All five PR workflows passed: Android CI, Source SBOM, OSV Scanner PR, Gradle Dependency Graph and Gradle Dependency Verification Bootstrap.
- No signed production artifacts or GitHub Release are claimed. Protected workflow dispatch/tag and physical-device/security acceptance remain open.


## 2026-11-24 — Documentation reconciliation merged
- PR #86 merged as `e15ee3ce1c623f70679b9c117018d98de0b1b757` after CODEOWNER approval by `techsomadhanmedia`.
- All five PR workflows succeeded; no unresolved review threads remained.
- The nine project handoff/status documents and README were reconciled with the PR #85 release-workflow fix and remaining release/QA gates.
- Documentation-only change; no production-signed artifacts or GitHub Release are claimed.

## Unreleased — 2026-11-25
- Added a per-tab Desktop Site toggle using GeckoView's desktop user-agent and viewport modes, with a mobile-site reset. Automated and device validation pending.

## 2026-11-26 — Unreleased / Main integration

- Merged PR #100: integrated the native Compose Rivo start page with app-internal `rivo://home` handling; the marker is not sent to GeckoView/network and is rejected during restored-tab navigation. Start-page history is omitted in private mode. Dark, Light and Premium Glass semantic surfaces are supported.
- Desktop Site remains a separate unmerged candidate and is not included in this Main entry.

## 2026-11-26 — Desktop Site integration correction

- Desktop Site per-tab mode was merged to Main by PR #93. The implementation maps both GeckoView user-agent and viewport modes and supports returning to mobile mode. Physical-device compatibility validation remains outstanding.


## Unreleased — Minimal home and translucent browser chrome (2026-11-28)

- Simplified the start page to a single centered Google search/address field; removed the wordmark, shortcuts, bookmark/history lists and secondary home-page content.
- Removed the standalone top reload control and placed Reload at the top of the three-dot menu, immediately before New tab.
- Hid the top omnibox on the home page while retaining the three-dot menu; kept the top omnibox on web pages.
- Reduced the bottom navigation strip to a compact 48 dp translucent bar and tuned surface opacity for readable controls over page content.
- Applied translucent surfaces to the browser header, home search field, bottom controls, Reader Mode and native browser panels. Website-rendered page content is not modified.
- Physical-device visual/accessibility QA remains pending; transparency and contrast must be checked in Dark, Light and Premium Glass modes.
