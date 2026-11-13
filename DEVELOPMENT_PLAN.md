# Rivo Browser — Development Plan

Updated: 2026-11-13

## Phase 0 — Foundation
**Source implementation: substantially complete; measured engine decision remains open.**
- Modular Kotlin Android project and engine abstraction.
- GeckoView runtime/session implementation; WebView comparison adapter and benchmark variants retained.
- Centralized Dark, Light and Premium Glass appearance system.
- Encrypted local metadata and CI/security workflow foundations.
- Remaining: repeatable physical-device engine comparison and low-RAM measurements.

## Phase 1 — Functional Browser MVP
**Source implementation: integrated in Main; acceptance validation remains.**
- Navigation, omnibox, back/forward/reload/stop and connection security.
- Multi-tab lifecycle, native private sessions and tab restoration.
- Bookmarks, history and encrypted metadata.
- Safe confirmation-gated downloads.
- Advanced Tab Universe and secondary Quick Appearance Picker.
- Remaining: physical device lifecycle, accessibility, data-loss and low-memory validation.

## Phase 2 — Advanced Browser Features
**Source implementation: integrated in Main by PR #38.**
- Reader Mode via GeckoView page extraction.
- QR-only Google Code Scanner with confirmation-gated navigation.
- Transfer Studio with progress, filters and best-effort cancellation.
- Read-only Developer Mode.
- Gecko site permissions, Privacy Intelligence Center and website notifications.
- Private download metadata exclusion and Clear Browsing Data integration.
- Remaining: runtime/device acceptance and UX/accessibility verification.

## Phase 3 — Security, performance and release
**Automated Main validation is green; production release remains blocked by non-CI gates.**
- Main Android CI #510 passed on Main SHA `5845e1b7804ad2cb102af16acd58af4b77aec3cb`; this included shipping build, lint, JVM tests, Gecko policy tests, benchmark builds, minified release bundle, static 16 KB checks and API 35 instrumentation.
- On that same Main SHA, OSV Scanner Scheduled #31, Gradle Dependency Graph #149 and Source SBOM #195 succeeded. These are point-in-time automated checks, not a full security audit.
- Complete OWASP MASVS control-by-control assessment.
- Physical device and 3–4 GB RAM profiling.
- 16 KB runtime validation on compatible hardware.
- Validate notification/permission/private-session edge cases.
- Signed-release automation is implemented in `.github/workflows/signed-release.yml`; it requires protected keystore secrets, builds and verifies signed APK/AAB, generates checksums and publishes a GitHub Release.
- Configure signing secrets, execute a reviewed release tag, complete physical-device acceptance and staged rollout.

## Release gates
A release candidate is acceptable only when:
1. Main-head Android CI, JVM tests and API 35 instrumentation pass.
2. No unresolved high/critical dependency advisories remain without documented mitigation.
3. Manual privacy, permissions, downloads, notifications and private-session tests pass.
4. Accessibility and all appearance modes pass on narrow and large-font layouts.
5. 3–4 GB performance is measured and meets agreed budgets.
6. 16 KB runtime is validated on compatible hardware.
7. Security review and signing provenance are documented; protected signing secrets are configured and a signed APK/AAB release workflow run succeeds.


## 2026-11-11 execution checkpoint
- PR #43 lazy restored-tab loading and PR #46 notification privacy/MASVS documentation integration are merged into Main (`b50350a241908fb1f38d2920b7a358451cb436f7`).
- PR #46 head passed Android CI, OSV Scanner, Gradle Dependency Graph and Source SBOM. Android CI included JVM tests, Gecko policy tests, static 16 KB alignment and API 35 emulator security instrumentation.
- Next release phase is gated by protected production signing secrets, physical-device/accessibility QA, 3–4 GB performance measurements, runtime 16 KB verification, complete MASTG/data-flow review and main-branch ruleset. Do not mark these as complete without evidence.


## 2026-11-13 signed-release workflow hardening

- PR #51 (`security/harden-signed-release-ci-2026-11-13`) separates the production-signing build from release publication: the owner-gated `build-and-sign` job uses `contents: read` and the `production` environment; the separate `publish` job alone receives `contents: write`. Checkout credentials are not persisted, the selected tag commit must be reachable from `main`, and verified APK/AAB plus SHA-256 assets are passed to the publisher as a short-retention artifact.
- Before production signing is enabled, configure required reviewers and branch/tag restrictions on the GitHub Environment named `production`, and store all four signing values as `production` Environment secrets (not repository-level secrets). The workflow's environment declaration alone is not an approval policy.
- PR checks on the earlier head passed Android build and supply-chain checks, but the latest workflow refactor requires fresh checks. A real signed build, APK/AAB signature verification and published checksum evidence remain required.
