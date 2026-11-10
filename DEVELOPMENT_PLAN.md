# Rivo Browser — Development Plan

Updated: 2026-11-10

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
**In progress; not release-ready. Signed-release automation is merged, but cannot execute until protected signing secrets are configured.**
- Resolve and scan Gradle dependencies; review OSV results and SBOM.
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
