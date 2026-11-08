# Rivo Browser

A free, privacy-focused Android browser targeting low- and mid-range devices.

## Current phase
**Phase 1 — Functional Browser MVP.** Source includes GeckoView browser sessions, navigation, tabs, private sessions, local bookmarks/history, downloads and Dark/Light/Premium Glass chrome. CI has passed build, lint and unit tests; physical-device validation is still pending.

## MVP source capabilities
- URL/search omnibox, back, forward, reload and stop; Android system Back follows page history when available.
- Multiple tabs; inactive Gecko sessions are deactivated; Activity backgrounding now deactivates and unfocuses all sessions; current cap is eight.
- Native private sessions; private tabs do not enter app-side history or bookmarks.
- Tracking protection enabled per session.
- Site/device/media permissions denied until proper permission mediation is implemented.
- Local bookmarks and bounded history.
- Downloads saved to app-specific storage (250 MB per file), with Open/Share actions.
- Dark, Light and Premium Glass themes with a secondary Quick Appearance Picker.

## Build environment
- JDK 17, Gradle 9.6.0.
- Android compile SDK: API 37.2.
- targetSdk: 36.
- GeckoView Stable: org.mozilla.geckoview:geckoview:159.0.20261022150525.

```bash
gradle --no-daemon :app:assembleDebug
gradle --no-daemon :app:lintDebug
gradle --no-daemon :app:testDebugUnitTest
```

The app creates separate ARM64 and ARMv7 debug APK outputs. Install only the APK matching the device ABI.

## Verification
- Successful CI checkpoint: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42460649170
- CI passed assemble, lint, unit tests and artifact upload.
- No physical-device install or performance benchmark is claimed yet.

## Documentation
- [Master specification](MASTER_SPEC.md)
- [Project state](PROJECT_STATE.md)
- [Development plan](DEVELOPMENT_PLAN.md)
- [Architecture](ARCHITECTURE.md)
- [Design system](DESIGN_SYSTEM.md)
- [Feature matrix](FEATURE_MATRIX.md)
- [Testing](TESTING.md)
- [Security](SECURITY.md)
- [Handoff](HANDOFF.md)
- [Engine benchmark protocol](docs/research/BENCHMARK_PROTOCOL.md)

## Limitations
- Download files are app-specific and private mode does not erase user-saved downloads.
- Per-site permission UI and process-death tab restoration are pending.
- Private browsing does not hide traffic from websites, ISPs or network operators.
- GeckoView remains the provisional MVP engine; final choice awaits controlled 3–4 GB device benchmark.
- Phase 2 and Phase 3 features have not started.
