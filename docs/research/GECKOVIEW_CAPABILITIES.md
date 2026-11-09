# GeckoView Capability Review — Rivo Browser

Audit date: 2026-11-09  
Pinned runtime: `org.mozilla.geckoview:geckoview:159.0.20261022150525`

This note distinguishes capabilities exposed by the pinned engine from features Rivo actually exposes in its UI. An engine API existing does not mean a complete browser feature or store is implemented.

## Reader Mode

**Decision: implement a native, read-only Reader Mode using GeckoView page extraction.**

The pinned GeckoView generation includes `GeckoSession.getSessionPageExtractor()`, `SessionPageExtractor.getPageMetadata()`, and `getPageContent(ContentParams)`. The page metadata includes `isReaderable`; the extraction options can request boilerplate removal and plain text. These APIs were introduced before the pinned 159 release (GeckoView changelog v151).

Rivo's first implementation:
- Checks Gecko's `isReaderable` metadata before extraction.
- Requests boilerplate removal and plain text.
- Displays the extracted article in a native, theme-aware reader surface with a user-adjustable text size.
- Caps extracted text at 250,000 characters to bound memory use.
- Does not inject JavaScript, modify the source page or persist extracted text.
- Returns to the original live page when the reader surface is closed.

This is article extraction, not a promise that every page can be converted. Paywalled, dynamic, unsupported or non-article pages can fail or return no readable content.

## QR scanning

**Decision: use Google Code Scanner rather than requesting camera permission or shipping a custom camera pipeline.**

The Google Code Scanner API delegates scanning UI to Google Play services, requires no camera permission from Rivo, processes images on-device and returns the decoded barcode value. Rivo restricts formats to QR codes, bounds payload length, removes control characters, and asks the user before opening/searching the result. The navigation resolver only opens supported HTTP(S) destinations; unsupported schemes become search text.

Operational limitation: the scanner is an unbundled Play services module. Devices without compatible Google Play services or with an unavailable scanner module cannot scan; Rivo displays a failure notice and retains manual omnibox navigation. No camera permission is declared.

## Transfer Studio

**Decision: provide a richer manager for the existing safe-download pipeline, without claiming unsupported transfer controls.**

The first increment provides filename/MIME search, All/Active/Completed/Failed filters, completed file size, Open, Share and safe Remove actions. Remove is limited to files whose canonical parent is one of Rivo's app-specific Downloads roots.

The current Gecko external-response integration provides an input stream and does not expose a resumable transfer contract to this UI. Therefore this increment does **not** claim pause, resume, retry, queue scheduling or byte-accurate live progress. Those require a separately designed download transport with validated range requests, server support and durable transfer state.

Private browsing does not erase files that a user explicitly saves. The private-download metadata exclusion work remains tracked separately until its integration is verified.

## Developer Mode

**Decision: expose read-only diagnostics only.**

The diagnostics panel reports the pinned engine, Android API/ABIs, active session type, JavaScript and tracking-protection settings. Remote debugging and console output are explicitly disabled in the shared GeckoRuntime. Rivo does not expose `about:config`, arbitrary preference mutation, a remote DevTools endpoint or production console logging.

GeckoView does expose remote-debugging configuration and `about:config` enablement APIs. Their existence is not sufficient reason to expose them in a privacy-focused production browser: remote inspection can expose page contents and session state. A future debug-only workflow would require build-variant gating, explicit user opt-in and a documented transport/security boundary.

## WebExtensions

**Decision: engine capability is real; a general-purpose extension store is not implemented.**

GeckoView documents:
- APK-bundled built-in WebExtensions installed from `resource://android/assets/...` using `installBuiltIn` / `ensureBuiltIn`.
- External `.xpi` installation through `WebExtensionController.install`; distributed add-ons must be Mozilla-signed.
- Extension permission prompts and runtime management APIs.

Rivo currently has no extension manager, marketplace, download/signature/update UX, permission review UI or private-browsing policy. Therefore the feature matrix must not claim end-user extension support yet. A later first release should start with a tightly scoped built-in extension or a Mozilla-signed XPI flow and explicit permission review, rather than arbitrary unsigned add-on installation.

## Sources

- Mozilla GeckoView page extraction API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/PageExtractionController.SessionPageExtractor.html
- Mozilla GeckoView readerability metadata: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/PageExtractionController.PageMetadata.html
- Mozilla GeckoView v151 API changelog: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/doc-files/CHANGELOG
- Mozilla GeckoView WebExtensions consumer guide: https://mozilla.github.io/geckoview/consumer/docs/web-extensions
- Mozilla WebExtensionController API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/WebExtensionController.html
- Mozilla GeckoRuntimeSettings API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoRuntimeSettings.html
- Google Code Scanner guide: https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner
- Android permission minimization guidance: https://developer.android.com/privacy-and-security/minimize-permission-requests

## Verification boundary

This document records API research and intended product boundaries. The Account A branch still requires fresh Android CI. Runtime Reader Mode extraction, QR scanning on a Play-services device, Transfer Studio file deletion and device UI/accessibility behavior require emulator/physical-device QA. No extension installation or remote debugging session is claimed as tested.
