# Architecture

## Modules
- app: Compose browser chrome, Activity, BrowserViewModel, BrowserController, local data and centralized design tokens.
- engine-api: engine-neutral evaluation contract.
- engine-gecko: process-scoped GeckoRuntime provider and per-tab GeckoSession adapter.
- engine-webview: retained Phase 0 comparison adapter; not wired into the Phase 1 app.
- data: DataStore-backed local bookmarks, bounded history, restorable tabs, download metadata and appearance settings (currently under app data package).

## Runtime and session ownership
- One GeckoRuntime is held by a process-level provider.
- One GeckoSession per browser tab.
- BrowserViewModel owns tab/session state across Activity configuration changes.
- GeckoView UI instances are created with the current Activity context, released when the active tab view leaves composition, and reattached to the retained session as needed.
- MainActivity forwards onStart/onStop to BrowserController: only the selected tab is activated while the Activity is started; all tab sessions are deactivated and unfocused while the Activity is stopped.
- Tab selection respects host visibility, preventing a tab switch while backgrounded from reactivating Gecko.
- Private tabs use native Gecko private-session settings.
- Tracking protection is enabled per session.
- Inactive sessions call setActive(false) and setFocused(false); selected foreground session is activated/focused.
- Closing a tab detaches and closes its session.
- Compose recomposition does not create or destroy sessions.
- System Back navigates browser history when available; otherwise Android's normal back behavior applies.
- MVP tab cap is eight pending physical-device memory data.

## Data boundaries
- Regular browsing history, bookmarks, restorable tabs and download metadata are stored locally in Preferences DataStore.
- Bookmarks, history, restorable-tab metadata and download metadata use AES-256-GCM encryption with an Android Keystore key; legacy plaintext values migrate on read.
- History is capped at 300 entries; bookmarks at 500; restorable tabs at eight; download metadata at 100 records.
- Private tabs never write app-side history, bookmarks or restorable-tab state.
- Theme preference is a non-sensitive local DataStore value and is not included in the encrypted metadata set.
- Downloaded file contents remain ordinary files inside app-specific Downloads storage so the user can explicitly open/share them; file contents are not encrypted by this metadata cipher.
- No account, cloud sync, telemetry or mandatory server.

## Security
- Omnibox parser accepts HTTP/HTTPS URLs; unsupported schemes become search queries.
- No JavaScript-to-native bridge.
- TLS certificate validation is not overridden.
- Site content, Android runtime and media permissions are denied until user-facing mediation exists, except currently mediated HTTPS notification and persistent-storage prompts.
- Downloads are restricted to HTTP(S) response streams, filenames sanitized, 250 MB per file, app-specific storage and scoped non-exported FileProvider.
- Browser chrome security state comes from GeckoView callbacks.
- Android backup disabled; no broad storage permission.

## UI
- Jetpack Compose chrome; GeckoView hosted in AndroidView.
- Browser chrome theme is independent of website content.
- Dark, Light and Premium Glass use centralized semantic color, spacing and shape tokens.
- Glass uses translucent surfaces without expensive real-time blur.
- Quick Appearance Picker is a secondary overflow control.
- Status/navigation system icon contrast follows appearance mode.

## Verification boundary
This document describes the current source architecture. Latest encryption, tab-restoration and Activity-lifecycle changes still require CI and physical-device validation before being treated as verified behavior.