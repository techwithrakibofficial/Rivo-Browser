# Decisions

## ADR-0001 — Engine selection
Status: Proposed, not final.
WebView and GeckoView comparison adapters and app flavors are implemented. GeckoView Stable candidate is pinned to 159.0.20261022150525; dependency resolution still needs CI confirmation. Final selection requires successful builds and controlled physical-device measurements for APK size, startup, memory, rendering and stability.

## ADR-0002 — Application architecture
Use Kotlin + Jetpack Compose for application UI and an engine-neutral boundary. Engine lifecycle is owned by lifecycle-aware holders, not by Compose recomposition.

## ADR-0003 — Scope
Only Phase 0 is authorized. No Phase 1 browser feature implementation before a separate approval.

## ADR-0004 — Infrastructure
Prefer local-first functionality and avoid mandatory paid backend services.
