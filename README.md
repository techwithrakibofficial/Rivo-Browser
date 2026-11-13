# Rivo Browser

Rivo Browser is a free, privacy-focused Android browser designed for low- and mid-range phones, including devices with 3–4 GB RAM.

## Current project status — 2026-11-13

- Canonical source: `main`.
- Latest Main commit at this checkpoint: `5845e1b7804ad2cb102af16acd58af4b77aec3cb`.
- PR #46 security/privacy integration is merged; no open pull requests were returned by the repository PR search at this checkpoint.
- Main-head Android CI run #510 passed on the current Main SHA. OSV Scanner Scheduled #31, Gradle Dependency Graph #149 and Source SBOM #195 also passed on that SHA.
- **This is not yet a production release.** CI success does not replace physical-device QA, runtime 16 KB testing, full security review or production signing.

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

## Honest limitations and outstanding release gates

- Reader Mode depends on GeckoView page readability and does not bypass gated content.
- QR scanning requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume/retry are not implemented.
- No end-user WebExtensions manager/marketplace is provided.
- Production signing secrets are not accessible to this session; no signed production APK/AAB is verified.
- Physical-device functional, accessibility and 3–4 GB RAM performance QA remain outstanding.
- 16 KB page-size checks are static only; runtime verification on compatible hardware remains outstanding.
- OWASP MASVS assessment is preliminary source-based gap analysis, not an independent audit or certification.
- Main branch protection is disabled; a repository ruleset/branch protection requiring PR review and CI remains to be configured.

## Engineering documentation

- `PROJECT_STATE.md` — current implementation and blockers.
- `HANDOFF.md` — session handoff and next steps.
- `DEVELOPMENT_PLAN.md` — phase status and release gates.
- `ARCHITECTURE.md` — module and lifecycle architecture.
- `DESIGN_SYSTEM.md` — appearance and accessibility rules.
- `FEATURE_MATRIX.md` — feature implementation status.
- `TESTING.md` — test evidence and device matrix.
- `SECURITY.md` and `SECURITY_ASSESSMENT.md` — security controls, limitations and preliminary gap register.
- `CHANGELOG.md` — notable project changes.

## Build

Open the project in Android Studio with a compatible Android SDK and JDK 17, or use the repository's Gradle wrapper. CI artifacts are for QA only and must not be treated as signed production releases. See `.github/workflows/signed-release.yml` for the protected signing workflow and required repository Actions secrets.
