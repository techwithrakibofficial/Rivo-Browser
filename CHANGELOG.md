# Changelog

## Unreleased — Phase 1 Functional Browser MVP
- Add process-scoped GeckoRuntime and per-tab GeckoSession browser architecture.
- Add URL/search omnibox, navigation controls, tabs and private sessions.
- Enable tracking protection and deny site/device/media permissions by default.
- Display GeckoView-reported connection security state.
- Add local bookmarks, bounded history and persisted Dark/Light/Premium Glass appearance.
- Add safe streamed downloads, 250 MB per-file cap and scoped open/share FileProvider.
- Add centralized browser chrome color, spacing and shape tokens.
- Add URL/search resolver unit tests.
- Configure ARM64 and ARMv7 APK splits.
- CI run #143 passes assemble, lint, unit tests and separate ARM64/ARMv7 artifact uploads.
- Publish ARM64 debug APK (208,688,414 bytes) and ARMv7 debug APK (169,493,492 bytes); physical-device validation is pending.

## Phase 0 — Foundation and engine evaluation
- Initialize Android project foundation and modular engine adapters.
- Add WebView and GeckoView comparison probes.
- Add benchmark protocol and engine research.
- Physical-device benchmark and final engine selection remain pending.
