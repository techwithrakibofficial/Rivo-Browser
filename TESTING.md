# Testing Plan

## Phase 0
- Gradle configuration/build validation.
- Lint and unit-test task validation.
- Engine probe startup and disposal.
- WebView and GeckoView same-page test matrix.
- Cold start, warm start, navigation and tab/session memory measurements.
- APK/AAB size comparison.

## Benchmark controls
Use same physical device, OS build, network, test pages, cache state and test order. Repeat each run; report median and p95 where meaningful. Record engine version, Android version, RAM, thermal state and battery state.

## Required scenarios
- Cold launch.
- Warm launch.
- First navigation and repeat navigation.
- 1, 5 and 10 sessions/tabs (where both prototypes support comparable sessions).
- Background/foreground transition.
- Session close/disposal and memory recovery.

## Status
No build, device test or benchmark has been executed yet.
