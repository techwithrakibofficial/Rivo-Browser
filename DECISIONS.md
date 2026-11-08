# Decisions

## ADR-0001 — Engine selection
Status: Proposed, not final.
The WebView and GeckoView comparison probes remain available. GeckoView Stable is the provisional Phase 1 implementation candidate, pinned to 159.0.20261022150525. The dependency and application build passed CI run 42460649170; verification of the newer separate ARM64/ARMv7 APK workflow is pending. Final selection requires controlled physical-device measurements for APK size, startup, memory, rendering and stability.

## ADR-0002 — Application architecture
Use Kotlin + Jetpack Compose for application UI and an engine-neutral boundary. Engine lifecycle is owned by lifecycle-aware holders, not by Compose recomposition.

## ADR-0003 — Scope authorization
Phase 1 — Functional Browser MVP was explicitly authorized by the user on 2026-11-08. Phase 0 foundation work is retained; Phase 1 implementation may continue within the approved scope. Phase 2 and Phase 3 are not authorized or started.

## ADR-0004 — Infrastructure
Prefer local-first functionality and avoid mandatory paid backend services.
