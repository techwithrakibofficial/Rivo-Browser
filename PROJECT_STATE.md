# Project State

Updated: 2026-11-08

## Current phase
Phase 0 — research, architecture, foundation and engine evaluation.

## Repository
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: main
- Phase 1 remains explicitly unauthorized and has not started.

## Completed
- Android Gradle/Compose foundation skeleton and project documentation.
- Engine-neutral API module.
- WebView probe with explicit disposal, safe browsing, no mixed content and no native JavaScript bridge.
- GeckoView probe module using a pinned Stable candidate artifact and process-scoped runtime ownership.
- Separate WebView and GeckoView app flavors.
- Same deterministic local HTML probe workload in both flavors.
- CI workflow updated to assemble both debug variants.
- Benchmark protocol and engine ADR updated.

## Pending verification
- GitHub Actions result for the latest commits has not yet been confirmed.
- GeckoView Maven dependency resolution and compilation are pending CI confirmation.
- No APK has yet been downloaded, installed or manually launched in this session.
- No physical 3–4 GB device benchmark has been performed.
- No measured APK size, RAM/PSS, startup, rendering, CPU, battery or crash data exists.
- Engine selection remains provisional; ADR-0001 is Proposed.

## Next steps
1. Verify GitHub Actions build of both flavors and fix any build failures.
2. Download/install each APK on physical 3–4 GB devices.
3. Run the documented controlled benchmark and save raw observations.
4. Review results and update ADR-0001; do not infer a winner from documentation alone.
5. Complete Phase 0 acceptance evidence and prepare a handoff.
