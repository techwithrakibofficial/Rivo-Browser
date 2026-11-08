# Architecture

## Modules
- app: Compose browser chrome, Activity, BrowserViewModel, BrowserController, local data and centralized design tokens.
- engine-api: engine-neutral evaluation contract.
- engine-gecko: process-scoped GeckoRuntime provider and per-tab GeckoSession adapter.
- engine-webview: retained Phase 0 comparison adapter; not wired into the Phase 1 app.
- data: DataStore-backed local bookmarks, bounded history and appearance settings (currently under app data package).

## Runtime and session ownership
- One GeckoRuntime is held by a process-level provider.
- One GeckoSession per browser tab.
- BrowserViewModel owns tab/session state across Activity configuration changes.
- GeckoView UI instances are created with the current Activity context, released when the active tab view leaves composition, and reattached to the retained session as needed.
- MainActivity forwards onStart/onStop to BrowserController: only the selected tab is activated while the Activity is started; all tab sessions are deactivated and unfocused while the Activity is stopped.
- Tab selection respects host visibility, preventing a tab switch while backgrounded from reactivating Gecko.
- Private tabs use native Gecko private-session settings.
- Tracking protection enabled per session.
- Inactive sessions call setActive(false) and setFocused(false); selected foreground session is activated/focused.
- Closing a tab detaches and closes its session.
- Compose recomposition does not create or destroy sessions.
- System Back navigates browser history when available; otherwise Android's normal back behavior applies.
- MVP tab cap is eight pending physical-device memory data.

## Data boundaries
- Normal browsing history and bookmarks remain local in Preferences DataStore.
- History max 300; bookmarks max 500.
- Private tabs never write app-side history or bookmarks.
- Theme preference stored locally.
- Download files stored in app-specific Downloads; metadata is in-memory only.
- No account, cloud sync, telemetry or mandatory server.

## Security
- Omnibox parser accepts HTTP/HTTPS URLs; unsupported schemes become search queries.
- No JavaScript-to-native bridge.
- TLS certificate validation is not overridden.
- Site content, Android runtime and media permissions are denied until user-facing mediation exists.
- Downloads restricted to HTTP(S) response streams, filenames sanitized, 250 MB per file, app-specific storage and scoped non-exported FileProvider.
- Browser chrome security state comes from GeckoView callbacks.
- Android backup disabled; no broad storage permission.

## UI
- Jetpack Compose chrome; GeckoView hosted in AndroidView.
- Browser chrome theme is independent of website content.
- Dark, Light and Premium Glass use centralized semantic color, spacing and shape tokens.
- Glass uses translucent surfaces without expensive real-time blur.
- Quick Appearance Picker is a secondary overflow control.
- Status/navigation system icon contrast follows appearance mode.
