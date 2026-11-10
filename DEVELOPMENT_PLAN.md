# Rivo Browser — Development Plan

Updated: 2026-11-09

## Phase 0 — Foundation
**Source implementation:** substantially complete.

- Android/Kotlin project foundation and modular engine boundary.
- GeckoView-backed browser runtime/session implementation.
- WebView benchmark variant retained for reproducible engine comparison.
- Centralized appearance system: Dark, Light and Premium Glass.
- Security/storage foundations and CI workflows.
- Remaining: physical-device engine comparison and final measured performance decision.

## Phase 1 — Functional Browser MVP
**Implementation:** core flows are present on Main; integration and acceptance verification remain.

- Navigation, omnibox, browser controls and connection security.
- Multi-tab lifecycle, private sessions and tab restoration.
- Bookmarks, history and encrypted local metadata.
- Confirmation-gated safe downloads and app-specific storage.
- Advanced Tab Universe with search, filters, sorting and accessible labels.
- Dark/Light/Glass palettes and secondary Quick Appearance Picker.
- Required acceptance: exact-head CI, API 35 instrumentation, physical-device smoke tests, accessibility, process-death and storage-corruption evidence.

## Phase 2 — Advanced browser capabilities
**Integrated candidate:** PR #37.

- Reader Mode using GeckoView page extraction; reject gated/non-readerable pages and never bypass access controls.
- QR-only scanning through Google Code Scanner; sanitize and confirm payloads before navigation.
- Transfer Studio with bounded recent history, search, filters, progress, best-effort cancellation and managed-file operations.
- Read-only Developer Mode diagnostics. Remote debugging, console logging and about:config remain disabled.
- Privacy Intelligence Center with real Gecko permission state and clear limitations.
- HTTPS-origin site permission mediation, default-deny unsupported capabilities and Android runtime permission mediation.
- Secure website notification handling with private/non-HTTPS denial.
- Private download metadata exclusion and tab-close cleanup.
- Extension workflows remain unsupported at the user-facing level: GeckoView engine capability does not mean Rivo has an add-on manager or marketplace.

## Phase 3 — Optimization and distribution
- Physical-device comparison on representative ARM64/ARMv7 and 3–4 GB devices.
- Memory, startup, tab switching, thermal and battery measurements.
- 16 KB runtime validation on a compatible device.
- TalkBack, 320 dp width, large-font and reduced-motion checks.
- Dependency/SBOM/advisory review and OWASP MASVS control-by-control assessment.
- Production signing, release AAB validation, privacy disclosures and store release only after all gates pass.

## Current integration dependencies
- PR #37 combines PR #35 and PR #36. Do not merge the source PRs separately.
- Exact-head PR #37 CI must pass after the latest commit.
- Re-run CI after every integration fix and verify the exact commit SHA.

## Acceptance criteria
- Navigation, tabs, private isolation, restoration, bookmarks/history and download lifecycle work on supported devices.
- Reader Mode rejects gated pages and handles unsupported extraction without stale UI.
- QR payloads are bounded, sanitized and require confirmation.
- Transfer progress is monotonic; cancellation is best-effort and private files are removed when their tab closes.
- Unsupported site permissions are denied; permission inventory/reset reflects actual Gecko state.
- Website notifications are HTTPS-only and never escape private browsing.
- Sensitive metadata remains encrypted; key loss/corruption fails closed without silently overwriting records.
- All three appearance modes remain readable and accessible.
- Automated, emulator and physical-device evidence is recorded separately.
- No release readiness claim until signing, security, accessibility and performance gates pass.
