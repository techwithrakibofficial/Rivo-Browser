# ADR-0001: Browser Engine Selection

- Date: 2026-11-08
- Status: Proposed — physical-device benchmark and browser parity required
- Decision owner: Rivo Browser project

## Context
Rivo targets 3–4 GB Android devices and needs browser-grade session management, privacy controls, maintainability and a realistic path to WebExtensions.

## Options
### Android System WebView
Pros: lower integration complexity, system component updates, generally avoids bundling a full browser engine.
Cons: browser-level features remain app responsibilities; no native full WebExtensions runtime; provider versions vary by device.

### Mozilla GeckoView
Pros: embeddable engine with GeckoRuntime/GeckoSession, native private sessions, content-blocking and WebExtension APIs.
Cons: larger package/runtime footprint to measure; app owns update cadence and compatibility; extension compatibility is not universal.

## Phase 1 implementation
GeckoView Stable is provisional because its session model supports private browsing and tracking protection. Artifact: org.mozilla.geckoview:geckoview:159.0.20261022150525. Compile SDK API 37.2; targetSdk 36.

## Required evidence
- Both benchmark flavors build and run on the same physical devices.
- Per-ABI APK and release AAB size comparison.
- Cold/warm startup, DOM workload ready, PSS, CPU and 1/5/10 session memory.
- Navigation stability, crashes, ANRs and recovery.
- 16 KB native ELF/ZIP alignment and runtime compatibility.
- Browser feature, privacy, security, permission and download parity before production replacement.

## Decision
Keep Proposed. The benchmark harness is engine-level and is not full browser parity. Decide only after repeatable physical-device measurements and shipping-browser regression evidence.
