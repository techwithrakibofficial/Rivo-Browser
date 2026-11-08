# Testing Plan

## Phase 0 automated checks
- Resolve and compile the WebView and GeckoView variants.
- Run unit-test tasks.
- Verify GeckoView stable artifact availability in CI.
- Keep CI permissions read-only.

## Phase 0 manual checks
- Install one engine variant at a time.
- Confirm the same deterministic local HTML workload renders.
- Verify Activity recreation and background/foreground behavior.
- Confirm WebView destroy and GeckoSession close behavior.
- Capture APK size, cold launch, PSS, frame diagnostics and crash/ANR observations.

## Benchmark controls
Use the same physical device, OS build, display/power conditions, test page and test order. Run cold and warm cases separately. Repeat at least five times where practical. Record device RAM, thermal state, battery state and WebView provider version.

## Required scenarios
- Cold launch.
- Warm launch.
- Local HTML workload render.
- Separate network navigation workload.
- Background/foreground transition.
- Session close/disposal and memory recovery.
- Future 1/5/10 session workload after a multi-session harness is implemented.

## Status
Probe source and build variants are committed. CI build status, APK installation, device tests and benchmark measurements are not yet confirmed.
