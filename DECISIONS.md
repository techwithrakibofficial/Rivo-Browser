# Decisions

## ADR-0001 — Engine selection
Status: Proposed, not final.
Candidate: GeckoView, because it is browser-oriented and provides browser-specific APIs. WebView remains the lower-integration alternative. Final decision requires an actual GeckoView stable artifact pin, identical test pages, controlled device conditions, APK size and RAM/startup/navigation measurements.

## ADR-0002 — Application architecture
Use Kotlin + Jetpack Compose for application UI and an engine-neutral boundary. Engine lifecycle is not controlled by Compose recomposition.

## ADR-0003 — Scope
Only Phase 0 is authorized. No Phase 1 browser feature implementation before a separate approval.

## ADR-0004 — Infrastructure
Prefer local-first functionality and avoid mandatory paid backend services.
