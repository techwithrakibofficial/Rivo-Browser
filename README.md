# Rivo Browser

A free, privacy-focused Android browser project targeting low- and mid-range devices.

## Current scope
**Phase 0 only:** foundation, engine research, WebView/GeckoView proof-of-concept and comparative benchmarking. Full browser features (Phase 1) have not started.

## Engine evaluation
- WebView and GeckoView are maintained as separate engine adapters.
- Build flavors: `webview` and `gecko`.
- Both variants load the same deterministic local HTML workload.
- GeckoView uses a pinned Stable candidate; CI must confirm dependency resolution.
- No final engine decision is made until controlled 3–4 GB device benchmarks are complete.

## Build both debug variants
Requires JDK 17, Android SDK (API 36) and Gradle 9.6.0.

```bash
gradle --no-daemon :app:assembleWebviewDebug :app:assembleGeckoDebug
```

Expected APKs:
- `app/build/outputs/apk/webview/debug/app-webview-debug.apk`
- `app/build/outputs/apk/gecko/debug/app-gecko-debug.apk`

See [BENCHMARK_PROTOCOL.md](docs/research/BENCHMARK_PROTOCOL.md) before comparing results.

## Project documentation
- [Master specification](MASTER_SPEC.md)
- [Project state](PROJECT_STATE.md)
- [Development plan](DEVELOPMENT_PLAN.md)
- [Architecture](ARCHITECTURE.md)
- [Design system](DESIGN_SYSTEM.md)
- [Feature matrix](FEATURE_MATRIX.md)
- [Decisions](DECISIONS.md)
- [Testing](TESTING.md)
- [Security](SECURITY.md)
- [Handoff](HANDOFF.md)

## Scope boundary
Do not begin Phase 1 until it is explicitly approved. No build, benchmark, security audit or release result is claimed unless documented with verifiable evidence.
