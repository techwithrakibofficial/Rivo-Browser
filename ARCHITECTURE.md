# Rivo Browser — Architecture

Updated: 2026-11-11

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


## 2026-11-11 integration checkpoint
- PR #46 merged notification privacy mitigation: website-controlled notification content is private-visibility content, while public notification rendering uses generic Rivo-owned text. `WebNotificationPrivacyPolicyTest` covers the public representation.
- PR #43 lazy restoration and PR #46 notification privacy changes are both on Main at `b50350a241908fb1f38d2920b7a358451cb436f7`.
- PR-head Android CI passed static 16 KB alignment and API 35 emulator security instrumentation. These do not prove runtime 16 KB compatibility or OEM notification behavior; physical-device verification remains open.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`. PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC.
- Active Main ruleset `Protect main branch` requires PR review, at least one approval, CODEOWNERS review, resolved threads and strict check `build`; branch deletion/non-fast-forward updates are blocked and no bypass actors exist.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue in source and adds JVM regression/property tests. GeckoView runtime mixed-content behavior remains unverified on physical devices/fixtures.
- On this Main SHA, OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still in progress at the last captured check; do not claim success before rechecking final conclusions.
- Remaining release gates: production-signed APK/AAB and signer/checksum verification; physical-device QA, accessibility and 3–4 GB RAM profiling; runtime 16 KB verification; full MASVS/MASTG assessment and independent penetration testing where required. CI does not replace these gates; no certification is claimed.


## 2026-11-24 — Release pipeline architecture verification
- Shipping engine remains GeckoView through the existing modular engine abstraction; WebView remains benchmark-only, not the shipping engine.
- PR #85 changes signed-release artifact verification only: APKs are preserved in `dist/` before AAB build cleanup, then signature checks enumerate those APKs and the single AAB output.
- All five PR workflows passed before merge. No browser runtime architecture changed in this work unit.
- Physical-device GeckoView behavior, target-class performance, runtime 16 KB and complete MASVS/MASTG assessment remain evidence gaps.

## 2026-11-25 — Desktop Site session configuration candidate
- Desktop Site is configured on the active tab's existing GeckoSession; it does not create a second session or replace the engine.
- Both user-agent mode and viewport mode are changed independently using `GeckoSessionSettings`. Mozilla documents that desktop UA mode alone does not enable the 980 CSS-pixel desktop viewport; both settings are required.
- The tab reloads after a mode change. The state is per-tab and currently in-memory only; it resets to mobile after process restoration.
- Official API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSessionSettings.html and https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/doc-files/CHANGELOG

## 2026-11-26 — Native start page integration

- PR #100 adds a native Compose start page integrated with the existing browser controller. The internal `rivo://home` marker is an app-level navigation state and must never be passed to GeckoView or treated as a network URL.
- Restored-tab policy rejects this internal marker so it cannot be replayed as a web navigation. Private tabs must not display or persist non-private history.
- Home surfaces consume semantic appearance tokens for Dark, Light and Premium Glass; this does not by itself prove visual contrast or accessibility acceptance on physical devices.
- Desktop Site remains unmerged: its existing branch is 15 commits ahead/behind current Main. Do not cherry-pick/merge without reviewing current GeckoSession lifecycle and UI state; any port must set both GeckoView user-agent and viewport modes and remain isolated per tab.

## Correction — Desktop Site integration (2026-11-26)

Desktop Site is integrated by PR #93; merge commit `e747ded5e84b6f0179aee892b9a3fc630187f8cf` is an ancestor of current Main. The per-tab implementation sets both GeckoView user-agent and viewport modes. Remaining verification concerns lifecycle restoration and real-site behavior on physical devices.
