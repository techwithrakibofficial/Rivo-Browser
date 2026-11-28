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


## 2026-11-28 — Main UX correction verification and 0.2.8 release preparation

- Main includes PR #108 at merge commit `8b2f9adc8b6a2828cbfa1d144828b3c367a8c668`.
- PR #108 CODEOWNER review and merge completed. Exact-merge-commit Android CI run [46147059192](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059192) completed successfully; Gradle dependency-verification bootstrap [46147059274](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059274) also completed successfully.
- Same-revision supply-chain checks succeeded: OSV Scanner [46147059324](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059324), Source SBOM [46147059195](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059195), and Gradle Dependency Graph [46147059212](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46147059212).
- Android CI passed its configured build, lint, JVM/policy tests, release-bundle build, static 16 KB alignment checks and security instrumentation steps. These are CI/emulator checks, not physical-device acceptance or runtime 16 KB proof.
- CI produced unsigned QA/debug artifacts only. The latest public signed release remains `v0.2.7-phase1.1`; do not distribute unsigned CI artifacts as a production APK.
- Next release identity is `0.2.8-phase1` / versionCode 9. Production signing and publishing require a new immutable tag or manual workflow dispatch, then signer/checksum verification. The available GitHub connection in this session cannot create/push tags or dispatch workflows.
- Physical TECNO Pova Slim 5G / Android 16 / HyperOS 16.3.0 acceptance, supplied-video visual matching, accessibility/TalkBack, 3–4 GB performance profiling, runtime 16 KB validation and independent MASVS/MASTG/penetration assessment remain unverified. No certification is claimed.

- PR #108 changes navigation input resolution, bottom browser chrome layout, semantic Glass surface tokens and settings navigation; it does not change the GeckoView engine boundary or add unsupported engine capabilities.


## 2026-11-28 — PR #110 candidate (not yet merged)
- Reworked the native start page toward a Chrome-style Google home: Google search field, local bookmark/history shortcuts, History and Incognito actions, plus Bookmarks and Tabs quick actions. No external news feed or fake account profile is presented.
- Browser top chrome now exposes Home/New tab, Guest profile/settings, open-tab count and overflow controls.
- Settings panel surface is opaque in Premium Glass to prevent website pixels bleeding through text; explicit semantic foreground colors are used.
- GeckoView's documented Android Autofill bridge is enabled on created GeckoView instances. This allows a separately configured Android Autofill provider to participate; it is not a Rivo password manager, and it does not implement credential storage or sync.
- Rivo account sign-in/cloud sync, a built-in password vault, and an extension marketplace remain unavailable and are not represented as shipped features.
- Candidate app version: 0.2.9-phase1 / versionCode 10. PR #110 requires fresh CI/review/merge; physical-device visual and autofill acceptance remain pending.


## Authoritative project checkpoint — 2026-11-28 (PR #110 merged)

- Canonical Main commit: `e10220c0c09711d5b5c5575bef106687797f9bae`; PR #110 is merged. The earlier “PR #110 candidate / not yet merged” entries above are historical and superseded by this checkpoint.
- App metadata on Main: `0.2.9-phase1`, versionCode 10.
- Android CI run [#46171158253](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158253) completed successfully. Verified steps include shipping debug assemble, Android lint, app unit tests, Gecko engine policy tests, GeckoView/WebView benchmark builds, minified shipping release bundle, static native-library/APK 16 KB alignment checks, QA artifact uploads and Android security instrumentation tests. Gradle dependency-verification bootstrap [#46171158244](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158244) and OSV scan [#46171158469](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46171158469) also completed successfully.
- CI outputs are QA/debug artifacts and an unsigned QA AAB; they are not production-signed release files. The immutable tag `v0.2.9-phase1` is not present, and no signed v0.2.9 GitHub Release is verified. Do not distribute unsigned CI artifacts as the production APK.
- PR #110 delivered the Chrome-inspired native home, local History/Bookmarks shortcuts, toolbar Home/profile/tab/overflow controls, opaque Settings surface with explicit foreground colors, and GeckoView Android Autofill integration. Autofill depends on the Android provider configured by the user; this is not a built-in password vault or account sync.
- Physical TECNO Pova Slim 5G / Android 16 / HyperOS 16.3.0 acceptance remains unverified in this audit. Also outstanding: TalkBack/large-font/narrow-width/reduced-motion checks, 3–4 GB memory profiling, runtime 16 KB validation on compatible hardware, GeckoView runtime security fixtures, and complete control-by-control MASVS/MASTG review. No independent penetration test or MASVS certification is claimed.
- Keep historical checkpoints above for traceability; this dated checkpoint is authoritative for the current Main revision only.

PR #110 architecture note: home shortcuts read local browser data; profile menu is Guest/settings navigation only; Android Autofill is delegated to the OS Autofill framework via GeckoView integration, not stored by a Rivo credential vault.

## 2026-11-29 — Chrome-style browser toolbar candidate
- Compact primary row: Back, Forward, Reload/Stop, persistent omnibox, tab switcher and overflow.
- Scrollable secondary actions: Home, bookmark toggle, Android share, Downloads, new/private tabs, History, Bookmarks and profile/autofill entry.
- Overflow exposes existing supported Reader Mode, QR scan, Desktop Site, text zoom, Privacy Intelligence Center, Developer Mode, clear-data confirmation and Settings. Unsupported Chrome services are not simulated.
