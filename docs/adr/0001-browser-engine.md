# ADR-0001: Browser Engine Selection

- Date: 2026-11-08
- Status: Proposed — build and physical-device benchmark required
- Decision owner: Rivo Browser project

## Context
Rivo targets 3–4 GB Android devices and needs browser-grade session management, privacy controls, maintainability and a realistic path to WebExtensions.

## Options
### Android System WebView
Pros: lower integration complexity, system component updates, usually avoids bundling a full browser engine.
Cons: the app must build browser-level features around an embeddable web-content component; no native full WebExtensions runtime; provider versions vary across devices.

### Mozilla GeckoView
Pros: designed for embedding Gecko in apps and browsers; bundled/versioned engine; GeckoRuntime/GeckoSession lifecycle; browser-specific content-blocking and WebExtension APIs.
Cons: larger package/runtime footprint is a risk to measure; the app owns engine update cadence and compatibility testing; extension compatibility is not universal.

## Phase 0 implementation
- Added isolated `:engine-webview` and `:engine-gecko` modules.
- Added `:webview` and `:gecko` app variants that load the same deterministic local HTML workload.
- Pinned a Stable GeckoView candidate: `159.0.20261022150525`. CI must confirm dependency resolution and compilation.
- Both adapters are probes only; they are not the Phase 1 browser engine integration.

## Provisional recommendation
Keep both candidates. GeckoView remains a strong browser-oriented candidate, but no final selection is made before build and physical-device evidence.

## Acceptance evidence required
- Successful CI build of both variants.
- Installable APK size comparison per ABI.
- Cold/warm startup and page rendering observations.
- PSS, Java/native heap and CPU measurements.
- 1/5/10 session memory behavior in a future controlled probe.
- Lifecycle cleanup and crash stability on representative 3–4 GB hardware.
- Review of security update cadence and operational cost for each engine.
