# Architecture

## Modules
- app: Compose browser chrome, Activity, BrowserViewModel, BrowserController, local data and centralized design tokens.
- engine-api: engine-neutral evaluation contract.
- engine-gecko: process-scoped GeckoRuntime provider and per-tab GeckoSession adapter.
- engine-webview: retained comparison probe; not wired into shipping Phase 1.
- engine-benchmark: isolated GeckoView/WebView host variants for controlled startup, memory and session comparison.
- data: DataStore-backed bookmarks, bounded history, restorable tabs, download metadata and appearance settings under the app data package.

## Runtime and session ownership
- One GeckoRuntime is held by a process-level provider.
- One GeckoSession per browser tab.
- BrowserViewModel owns tab/session state across Activity configuration changes.
- GeckoView UI instances use the current Activity context, are released when the active tab leaves composition and reattached to the retained session as needed.
- MainActivity forwards onStart/onStop: only the selected tab is active while the Activity is started; all tabs are deactivated and unfocused while stopped.
- Private tabs use native Gecko private-session settings; tracking protection is enabled per session.
- Closing a tab detaches its view and closes its session. Compose recomposition does not create/destroy sessions.
- MVP cap is eight tabs pending physical-device memory data.

## Engine benchmark isolation
- Shipping app remains GeckoView-only; benchmark work does not change the browser engine.
- engine-benchmark builds gecko and webview flavors with the same package, Activity, controls and offline HTML/JS workload.
- 1/5/10-tab scenarios cover activation, background/foreground, switching, reload and close.
- Results are engine-level, not feature-equivalent full-browser measurements. Browser-level parity remains a decision gate.

## Data boundaries
- Bookmarks, history, restorable-tab metadata and download metadata use AES-256-GCM encryption with an Android Keystore key; legacy plaintext values migrate on read.
- History capped at 300 entries; bookmarks at 500; restorable tabs at eight; download metadata at 100 records.
- Private tabs do not persist app-side history, bookmarks or restorable-tab state.
- Downloaded file contents remain ordinary files inside app-specific Downloads storage and are not encrypted by the metadata cipher.
- No account, cloud sync, telemetry or mandatory server.

## Security
- Omnibox accepts HTTP/HTTPS; unsupported schemes become search queries.
- No JavaScript-to-native bridge; TLS certificate validation is not overridden.
- Site, Android and media permissions are denied until user-facing mediation exists, except mediated HTTPS notification and persistent-storage prompts.
- Downloads are HTTP(S), sanitized, capped at 250 MB, app-specific and exposed through a scoped non-exported FileProvider.
- Android backup disabled; no broad storage permission.

## UI
- Jetpack Compose chrome; GeckoView hosted in AndroidView.
- Dark, Light and Premium Glass use centralized semantic tokens.
- Glass uses translucent surfaces without real-time blur.
- Status/navigation icon contrast follows appearance mode.

## Verification boundary
Main CI run #246 passed on the pre-benchmark HEAD. New benchmark flavors and 16 KB artifact checks require this PR's CI. Physical-device runtime, full browser parity and leak-free memory behavior are not yet verified.
