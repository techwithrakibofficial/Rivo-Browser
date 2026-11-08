# ADR-0001: Browser Engine Selection

- Date: 2026-11-08
- Status: Proposed — physical-device benchmark required
- Decision owner: Rivo Browser project

## Context
Rivo targets 3–4 GB Android devices and needs browser-grade session management, privacy controls, maintainability and a realistic path to WebExtensions.

## Options
### Android System WebView
Pros: lower integration complexity, system component updates, usually avoids bundling a full browser engine.
Cons: app must build browser-level features around an embeddable web-content component; no native full WebExtensions runtime; provider versions vary across devices.

### Mozilla GeckoView
Pros: designed for embedding Gecko in apps and browsers; bundled/versioned engine; GeckoRuntime/GeckoSession lifecycle; native private sessions, content-blocking and WebExtension APIs.
Cons: larger package/runtime footprint must be measured; app owns engine update cadence and compatibility testing; extension compatibility is not universal.

## Phase 1 implementation choice
GeckoView Stable is used provisionally for the functional MVP because its session model supports private browsing and tracking protection. Published artifact: org.mozilla.geckoview:geckoview:159.0.20261022150525.
The Android compile SDK is API 37.2 due AAR metadata; targetSdk remains 36.
CI confirms assemble, lint and URL resolver unit tests pass. This is not a final engine selection.

## Required evidence before final decision
- Successful build and install of both engine candidates.
- Per-ABI APK size comparison.
- Cold/warm startup and page rendering observations.
- PSS, Java/native heap and CPU measurements.
- 1/5/10 session memory behavior.
- Stability and security update cadence review on representative 3–4 GB devices.

## Decision
Keep status Proposed. Finalize only after reproducible physical-device measurements. Do not claim universal extension compatibility or a benchmark winner from source inspection alone.
