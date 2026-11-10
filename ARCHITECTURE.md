# Rivo Browser — Architecture

Updated: 2026-11-09

## Modules
- `app`: Activity, Compose browser chrome, BrowserViewModel, BrowserController, local storage integration and design-system tokens.
- `engine-api`: engine-neutral browser capability contract.
- `engine-gecko`: process-scoped GeckoRuntime and per-tab GeckoSession adapter, page extraction, site permission mediation and website notifications.
- `engine-webview`: retained WebView comparison adapter; not the shipping engine.
- `engine-benchmark`: GeckoView/WebView benchmark variants and artifact workflows.
- Local data currently lives under the app data package and uses Preferences DataStore plus Android Keystore encryption.

## Runtime and session ownership
- One GeckoRuntime is held by a process-level provider.
- Each browser tab owns one GeckoSession.
- BrowserViewModel retains controller/session state across Activity configuration changes.
- GeckoView UI instances use the current Activity context and are detached/re-attached without recreating the underlying tab session.
- Only the selected tab on a started host is activated/focused; sessions are deactivated while the Activity is stopped.
- Private tabs use native Gecko private-session settings.
- Tracking protection is enabled per session.
- Closing a tab disposes its session; private download files are removed with the originating private tab.
- The tab cap remains eight pending physical-device memory measurements.
- Android system Back navigates page history when available; otherwise normal Android back behavior applies.

## Navigation and browser features
- Omnibox resolves HTTP(S) URLs and treats unsupported schemes as search input.
- Reader Mode uses GeckoView SessionPageExtractor metadata/content APIs; extracted text is bounded, ephemeral and not persisted.
- QR scanning uses Google Code Scanner and never navigates automatically; sanitized results require explicit confirmation.
- Transfer Studio is a Compose view over bounded in-memory transfer state and managed app-specific files.
- Developer Mode exposes read-only diagnostics only; remote debugging, console output and about:config are disabled.
- Extension engine capability is not surfaced as end-user extension support because Rivo has no add-on manager/marketplace.

## Data boundaries
- Preferences DataStore stores theme, bookmarks, bounded history, normal-tab restoration, download metadata and supported Android permission-denial state.
- Sensitive browser metadata uses AES-256-GCM with an Android Keystore key; legacy migration and fail-closed write guards protect saved records.
- Bookmarks are bounded to 500, history to 300, restored tabs to eight and download metadata to 100.
- Private tabs do not write app-side history, bookmarks or restorable-tab state.
- Private download metadata is excluded from the persisted download index. Private file bytes are kept in a dedicated app-specific directory and removed on tab close.
- Theme preference is non-sensitive and stored as a local DataStore value.
- Downloaded file contents are ordinary app-specific files for explicit open/share; the metadata cipher does not encrypt file bytes.
- No account, cloud sync, telemetry or mandatory server.

## Permissions and notifications
- Gecko content permission requests are restricted to supported secure-origin capabilities.
- Unsupported site permission and Android permission requests default-deny.
- Android runtime permission prompts are limited to the explicit location/camera/microphone allowlist and require user interaction.
- Website notifications require HTTPS and Android notification availability; private-mode notifications are rejected.
- Notification receiver is non-exported and uses immutable PendingIntent.
- Privacy Intelligence Center reports Gecko's actual permission inventory and revocation limitations.

## Download pipeline
- Download response is confirmation-gated and validated before file creation.
- Only HTTPS downloads are accepted by the integrated security policy.
- Filename and MIME metadata are normalized; files use app-specific storage, temporary staging, atomic finalization and a 250 MB cap.
- Transfer progress is based on bytes read; unknown Content-Length is indeterminate.
- Cancellation is best-effort by closing the response stream; no pause/resume or resumable queue is claimed.
- Restored file records must resolve beneath canonical app-specific roots and remain within the size cap.
- Open/share uses scoped FileProvider grants.

## UI and appearance
- Jetpack Compose hosts native browser chrome; GeckoView is embedded via AndroidView.
- Website content is not recolored by the browser chrome theme.
- Dark, Light and Premium Glass use centralized semantic color, spacing and shape tokens.
- Glass uses restrained translucent surfaces without expensive real-time blur.
- Quick Appearance Picker is a secondary overflow control, not a permanent main-screen theme icon.
- Reader Mode, Transfer Studio, Privacy Intelligence Center, permission prompts and dialogs inherit the selected MaterialTheme.

## Verification boundary
PR #37 combines PR #35 and PR #36 but is still an integration candidate. Exact-head CI, API 35 instrumentation, physical-device lifecycle/security tests, 3–4 GB performance measurements, 16 KB runtime verification and MASVS review remain separate acceptance gates. CI AABs are unsigned QA artifacts only.
