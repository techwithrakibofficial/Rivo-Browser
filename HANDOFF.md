# Handoff

## Current state
Phase 0 is active. Phase 1 browser feature implementation has not started and remains outside the current authorization.

## Completed
- Android project foundation, engine API and initial project documentation.
- WebView and GeckoView adapter modules.
- Separate app build flavors with a shared deterministic local HTML workload.
- WebView probe defaults: JavaScript/DOM storage enabled for comparison, Safe Browsing enabled, mixed content disallowed, no native bridge.
- GeckoView probe uses a process-scoped GeckoRuntime and closes its GeckoSession on disposal.
- CI workflow now builds both debug flavors.
- Engine comparison and benchmark documentation updated.

## Tested / verified
- GitHub source files and commit history were inspected.
- Mozilla's official quick-start/API documentation and stable channel naming were reviewed.
- Public Maven version index lists GeckoView Stable 159.0.20261022150525.
- The dependency pin is declared but not yet confirmed by a successful Gradle build.
- No local Android build, APK install, physical device test or benchmark result is claimed.

## Remaining
1. Confirm CI build and resolve compile/dependency issues.
2. Install both flavor APKs on representative 3–4 GB hardware.
3. Collect at least five repeat runs per workload and preserve raw outputs.
4. Compare size, startup, PSS, frame behavior and stability.
5. Update ADR-0001 only after evidence review.

## Known blockers
- No attached Android emulator/device is available in this workflow.
- CI run result has not yet been independently verified.
- Physical-device benchmark remains outstanding.

## Scope boundary
Do not start Phase 1 until the user explicitly approves it.
