# Architecture

## Modules
- app: Compose browser chrome, Activity, BrowserViewModel, BrowserController, local data and centralized design tokens.
- engine-api: engine-neutral evaluation contract.
- engine-gecko: GeckoRuntime owner and per-tab GeckoSession adapter.
- engine-webview: retained Phase 0 comparison adapter; not wired into the Phase 1 app.
- data: DataStore-backed local bookmarks, bounded history and appearance settings (currently located under app data package).

## Runtime and session ownership
- One GeckoRuntime is owned by the BrowserViewModel's process-lifetime browser controller.
- One GeckoSession and GeckoView per browser tab.
- Private tabs use GeckoView native private-session settings.
- Tracking protection is enabled per session.
- Inactive sessions call setActive(false) and setFocused(false); selected session is activated/focused.
- Closing a tab detaches and closes its GeckoSession.
- Compose recomposition does not create or destroy sessions.
- MVP tab cap is eight until physical-device measurements establish a better policy.

## Data boundaries
- Normal browsing history and bookmarks remain local in Preferences DataStore.
- History is bounded to 300 entries; bookmarks to 500.
- Private tabs never write app-side history or bookmarks.
- Theme preference is stored locally.
- No account, cloud sync, telemetry or mandatory server.

## Security
- Omnibox parser accepts HTTP/HTTPS URLs; unsupported schemes are treated as search queries.
- No JavaScript-to-native bridge.
- TLS certificate validation is not overridden.
- Site content, Android runtime and media permissions are denied until user-facing permission mediation exists.
- Downloads are restricted to HTTP(S) response bodies, filenames sanitized and files written to app-specific Downloads storage.
- Browser chrome security state comes from GeckoView callbacks.
- Android backup disabled; no broad storage permission.

## UI
- Jetpack Compose chrome; GeckoView is hosted in AndroidView.
- Browser chrome theme is independent of website content.
- Dark, Light and Premium Glass use centralized semantic color schemes.
- Glass uses translucent surfaces without expensive real-time blur.
- Quick Appearance Picker is a secondary overflow control, not a permanent toolbar icon.
