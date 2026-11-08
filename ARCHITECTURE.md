# Architecture

## Modules
- app: Compose UI, browser composition root and lifecycle.
- engine-api: engine-neutral browser session contract.
- engine-gecko: GeckoRuntime owner, GeckoSession tab adapter and event delegates.
- engine-webview: Phase 0 comparison adapter retained for evaluation only.
- data: local preferences, bookmarks and bounded history persistence (Phase 1).
- core/designsystem: centralized semantic color, typography, spacing, shape and motion tokens (Phase 1).

## Runtime and session ownership
- One process-scoped GeckoRuntime.
- One GeckoSession per tab.
- Private tabs use GeckoSessionSettings.usePrivateMode(true).
- Inactive sessions call setActive(false); selected session is active and focused.
- Closing a tab detaches its view and closes its session.
- Compose recomposition must never create or destroy sessions.
- Browser controller/ViewModel owns sessions across configuration changes.

## Data boundaries
- Normal browsing history and bookmarks remain local.
- Private tabs never write browsing history and do not create persistent bookmark suggestions.
- Preferences use DataStore. Keep history bounded; migrate to Room if data volume/query requirements grow.
- No account, cloud sync, telemetry or mandatory server.

## Security
- HTTPS-first navigation resolution; only http/https schemes are accepted from the omnibox unless an explicit safe internal scheme is handled.
- No unsafe JavaScript-to-native bridge.
- Do not override TLS certificate errors.
- Site permissions remain denied until an explicit permission mediation flow exists.
- Downloads are restricted to HTTP(S), filename sanitized and delegated to Android DownloadManager.
- Browser chrome security state must come from engine callbacks.

## UI
- Jetpack Compose chrome; GeckoView is hosted in AndroidView.
- Browser chrome theme is independent of web content.
- Dark, Light and Premium Glass share semantic tokens.
- Quick Appearance Picker is a secondary menu, not a permanent toolbar icon.
