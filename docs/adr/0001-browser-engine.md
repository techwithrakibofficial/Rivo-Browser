# ADR-0001: Browser Engine Selection

- Date: 2026-11-08
- Status: Proposed — benchmark required
- Decision owner: Rivo Browser project

## Context
Rivo targets 3–4 GB Android devices and needs browser-grade session management, privacy controls, maintainability and a realistic path to WebExtensions.

## Options
### Android System WebView
Pros: lower integration complexity, system component updates, typically avoids bundling a full engine.
Cons: app must build browser features around a component intended primarily to display web content; extension support is not native browser WebExtensions support; lifecycle and memory must be handled carefully.

### Mozilla GeckoView
Pros: designed for embedding browser engine functionality; bundled/versioned engine; GeckoSession lifecycle; browser-specific APIs including content blocking and WebExtensions workflows.
Cons: larger package/runtime footprint likely; app owns engine update cadence and compatibility testing; extension compatibility is not universal.

## Provisional recommendation
Prototype GeckoView first, keep WebView as measured fallback. Do not finalize until stable artifact pin and device benchmark are available.

## Acceptance evidence
- Reproducible dependency resolution.
- Buildable minimal probes for both engines.
- APK size and native/Java heap/PSS measurements.
- Cold/warm startup and page navigation results.
- 1/5/10 session behavior on representative 3–4 GB hardware.
- Lifecycle cleanup and crash stability.
