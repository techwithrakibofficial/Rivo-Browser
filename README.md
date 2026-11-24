## Verified project status — 2026-11-24 (PR #86 merged)
- Latest verified Main commit: `e15ee3ce1c623f70679b9c117018d98de0b1b757` (PR #86 documentation reconciliation).
- PR #86 merged after CODEOWNER approval; all five PR workflows passed and no review threads remained unresolved.
- PR #85 release-workflow fix remains integrated at `4821c7af1255486e25b98305fd2b74cde33f488d`.
- No production-signed APK/AAB or GitHub Release is verified. Signed release still requires a new immutable Main-ancestral tag or owner-triggered workflow dispatch and successful artifact verification.
- Physical-device QA, 3–4 GB profiling, accessibility, runtime 16 KB and complete MASVS/MASTG assessment remain open.

# Rivo Browser

Rivo Browser is a free, privacy-focused Android browser designed for low- and mid-range phones, including devices with 3–4 GB RAM.

## Current project status — 2026-11-24

- Canonical source: `main`.
- Latest verified Main commit: `e15ee3ce1c623f70679b9c117018d98de0b1b757` (PR #86 documentation reconciliation).
- PR #85 is merged after CODEOWNER approval, zero unresolved review threads and five successful PR workflows.
- PR #86 documentation reconciliation is merged; CODEOWNER approval and all five PR workflows passed.
- No production release is published or verified. The protected signed-release workflow requires an existing new version tag reachable from Main, valid production Environment secrets and a repository-owner-triggered workflow run.
- The owner reports successful phone installation/testing; this is owner-reported acceptance, not independent device-lab evidence.
- CI/emulator evidence is not physical-device acceptance. 3–4 GB profiling, accessibility, runtime 16 KB and full MASVS/MASTG assessment remain release gates.


## Features integrated in Main

- GeckoView-based browsing, omnibox and navigation controls.
- Multi-tab browsing, native private sessions and Advanced Tab Universe.
- Local bookmarks/history and encrypted browser metadata.
- Confirmation-gated HTTPS downloads and Transfer Studio.
- Reader Mode and QR-only scanning with explicit navigation confirmation.
- Site permission controls, Privacy Intelligence Center and secure website notifications.
- Dark, Light and Premium Glass appearance modes.
- Read-only Developer Mode diagnostics.
- Lazy loading of restored background tabs.
- Security status disclosure for active/passive mixed content and qualified unknown state.

## Honest limitations and release gates

- Reader Mode depends on GeckoView page readability and does not bypass gated content.
- QR scanning requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume/retry are not implemented.
- No end-user WebExtensions manager/marketplace is provided.
- Downloaded file bytes are not encrypted by the browser metadata cipher.
- Static 16 KB packaging checks are distinct from runtime validation on compatible hardware.
- Production signing and public release require an authorized version tag/dispatch and successful signer/checksum verification.
- Source-based security review and CI do not equal an independent penetration test or MASVS certification.

## Engineering documentation

- `PROJECT_STATE.md` — implementation state and blockers.
- `HANDOFF.md` — session handoff and next steps.
- `DEVELOPMENT_PLAN.md` — phase status and release gates.
- `ARCHITECTURE.md` — module and lifecycle architecture.
- `DESIGN_SYSTEM.md` — appearance and accessibility rules.
- `FEATURE_MATRIX.md` — feature implementation status.
- `TESTING.md` — test evidence and device matrix.
- `SECURITY.md` and `SECURITY_ASSESSMENT.md` — security controls, limitations and preliminary gap register.
- `SECURITY_AUDIT.md` — source audit and manual verification matrix.
- `CHANGELOG.md` — notable project changes.

## Build

Open the project in Android Studio with a compatible Android SDK and JDK 17, or use the repository's Gradle wrapper. CI artifacts are for QA only and must not be treated as signed production releases. See `.github/workflows/signed-release.yml` for the protected signing workflow.


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
