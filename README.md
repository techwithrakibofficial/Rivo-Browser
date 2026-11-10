# Rivo Browser

Rivo Browser is a free, privacy-focused Android browser designed for low- and mid-range phones, including devices with 3–4 GB RAM.

## Project status
- Canonical source: `main`
- Latest integration: PR #38 merged to Main.
- Current Main merge commit: `a40a64460f51c6c4652e133d35e99da5a89d44d4`
- Phase 2 source features are integrated; physical-device QA, low-RAM profiling, 16 KB runtime validation, complete MASVS review and production signing remain open.
- This repository is not yet a production release.

## Features
- GeckoView-based browsing, omnibox and navigation controls.
- Multi-tab browsing, native private sessions and Advanced Tab Universe.
- Local bookmarks/history and encrypted browser metadata.
- Confirmation-gated HTTPS downloads and Transfer Studio.
- Reader Mode and QR-only scanning with explicit confirmation.
- Site permission controls, Privacy Intelligence Center and secure website notifications.
- Dark, Light and Premium Glass appearance modes.
- Read-only Developer Mode diagnostics.

## Honest limitations
- Reader Mode depends on GeckoView page readability and does not bypass gated content.
- QR scanning requires compatible Google Play services.
- Transfer cancellation is best-effort; pause/resume/retry are not implemented.
- No end-user WebExtensions manager/marketplace is provided.
- CI artifacts are unsigned QA builds; production signing and release are pending.

## Engineering documentation
- `PROJECT_STATE.md` — current implementation and blockers.
- `HANDOFF.md` — session handoff and next steps.
- `DEVELOPMENT_PLAN.md` — phase status and release gates.
- `ARCHITECTURE.md` — module and lifecycle architecture.
- `DESIGN_SYSTEM.md` — appearance and accessibility rules.
- `FEATURE_MATRIX.md` — feature implementation status.
- `TESTING.md` — test evidence and device matrix.
- `SECURITY.md` — security controls and limitations.
- `CHANGELOG.md` — notable project changes.

## Build
Open the project in Android Studio with a compatible Android SDK and JDK 17, or use the repository's Gradle wrapper. CI artifacts are for QA only and must not be treated as signed production releases.
