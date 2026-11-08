# Rivo Browser

A free, privacy-focused Android browser for low- and mid-range devices.

## Current phase
**Phase 1 — Functional Browser MVP.** The app source now includes GeckoView browser sessions, navigation, tabs, private sessions, local bookmarks/history, downloads and three appearance modes. CI and device verification are still in progress; this is not yet a production release.

## Features in the current MVP source
- URL/search omnibox, back, forward, reload and stop.
- Multiple tabs; inactive Gecko sessions are deactivated and the MVP currently caps tabs at eight.
- Native GeckoView private sessions; private tabs do not enter local history or bookmarks.
- Tracking protection enabled per session.
- Site/device/media permissions denied until a proper permission mediation UI exists.
- Local bookmarks and bounded history.
- Downloads stored in Rivo's app-specific Downloads directory.
- Dark, Light and Premium Glass browser chrome themes.

## Build
Requires JDK 17, Android SDK API 36 and Gradle 9.6.0.

```bash
gradle --no-daemon :app:assembleDebug
gradle --no-daemon :app:lintDebug
gradle --no-daemon test
```

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

## Important limitations
- No production APK or device test is claimed until verified.
- Download open/share/export and per-site permission controls are pending.
- Private browsing does not hide traffic from websites, ISPs or network operators.
- GeckoView remains the provisional MVP engine; final engine choice awaits 3–4 GB device benchmark.
- Phase 2 and Phase 3 features have not started.
