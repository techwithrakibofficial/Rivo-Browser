# Rivo Browser — Architecture

Updated: 2026-11-10

## Modules
- `app`: Activity, Compose browser chrome, BrowserViewModel, BrowserController, local storage integration and design tokens.
- `engine-api`: engine-neutral browser capability contract.
- `engine-gecko`: process-scoped GeckoRuntime, per-tab GeckoSession, page extraction, site permission mediation and website notifications.
- `engine-webview`: WebView comparison adapter retained for benchmark evaluation.
- `engine-benchmark`: GeckoView/WebView benchmark variants.
- Local data currently lives in the app data package using Preferences DataStore and Android Keystore encryption.

## Runtime and session ownership
- One GeckoRuntime is held by a process-level provider.
- Each tab owns one GeckoSession.
- BrowserViewModel retains controller/session state across Activity configuration changes.
- GeckoView views are detached/re-attached while sessions remain owned by tabs.
- Only the selected tab on a started host is activated/focused.
- Process restoration creates saved normal tabs without initial page navigation; the selected tab loads at startup and background restored tabs load on first selection. User-created tabs retain immediate loading.
- Closing a tab disposes its session; private-download cleanup is tied to private tab lifecycle.
- The current tab cap is eight pending physical-device memory measurements.

## Browser features
- Omnibox resolves HTTP(S) URLs; unsupported schemes become search input.
- Reader Mode uses GeckoView page extraction; readability/gated-content checks and a 120,000-character cap bound memory.
- QR scanning uses Google Code Scanner; decoded payloads are sanitized and require explicit confirmation.
- Transfer Studio manages existing download records and app-specific files; cancellation is best-effort.
- Developer Mode exposes read-only diagnostics; remote debugging, console output and about:config remain disabled.
- GeckoView WebExtensions are not exposed as a user feature because there is no Rivo add-on manager.

## Data and privacy boundaries
- Preferences DataStore stores appearance, bookmarks, bounded history, normal-tab restoration, download metadata and supported Android permission-denial state.
- Sensitive browser metadata uses AES-256-GCM with Android Keystore; corruption/missing-key failures fail closed.
- Bookmarks are bounded to 500, history to 300, restored tabs to eight and download metadata to 100.
- Private tabs do not persist app-side history, bookmarks or restorable-tab state.
- Private download metadata is not persisted; private file bytes live in a dedicated app-specific directory and are removed on tab close.
- Downloaded files are ordinary files to support explicit open/share; metadata encryption does not encrypt file bytes.
- No account, cloud sync, telemetry or mandatory server.

## Permissions and notifications
- Supported permission prompts are limited to secure origins and an explicit capability allowlist.
- Unsupported site and Android permissions default-deny.
- Website notifications require HTTPS and normal mode; Android notification permission/channel availability is checked.
- Notification receiver is non-exported and uses immutable PendingIntent.
- Privacy Intelligence Center reads actual Gecko permission inventory and reports revocation limitations.

## Appearance and performance
- Jetpack Compose hosts browser chrome; GeckoView is embedded through AndroidView.
- Dark, Light and Premium Glass share centralized semantic tokens.
- Glass uses restrained translucent surfaces without expensive real-time blur.
- Quick Appearance Picker is a secondary overflow control.
- Physical contrast, TalkBack, narrow-screen, large-font and low-RAM verification remain open.

## Verification boundary
- Main Android CI run #42763857175 passed on source SHA `ca8eddb2b0ce7f8252c639805f4227c6aeb938d1`, including API 35 security instrumentation and static 16 KB alignment checks.
- Same-head OSV Scanner reported **No issues found** after scanning five Gradle lockfiles (814 resolved package entries across modules); SBOM and dependency graph jobs succeeded.
- These are automated CI results only. Physical-device acceptance, low-RAM profiling, 16 KB runtime verification and full OWASP MASVS assessment remain separate gates.
- CI APK/AAB outputs are unsigned QA artifacts, not production release packages.


## Website notification disclosure boundary
- Native website notifications keep full content in the normal notification representation but use Android private visibility and a generic public-version representation for insecure display contexts. Device/OEM behavior remains subject to manual verification.
