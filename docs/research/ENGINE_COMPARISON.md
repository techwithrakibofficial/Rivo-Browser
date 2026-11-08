# Engine Research — 2026-11-08

## Official documentation
- Android WebView guide: https://developer.android.com/develop/ui/views/layout/webapps/webview
- Mozilla GeckoView overview: https://mozilla.github.io/geckoview/
- Mozilla GeckoView quick start: https://mozilla.github.io/geckoview/consumer/docs/geckoview-quick-start
- GeckoSession API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSession.html
- GeckoView release index: https://mvnrepository.com/artifact/org.mozilla.geckoview/geckoview/versions

## Verified product differences
- Android WebView is an embeddable View; navigation UI and browser-level behavior are application responsibilities.
- Android documents WebView as Chromium-based. The installed WebView provider can vary by device and is updated separately from the app.
- Mozilla describes GeckoView as a self-contained engine intended for apps and browsers; the engine is bundled with the application.
- GeckoView uses GeckoRuntime and GeckoSession. Mozilla documents GeckoRuntime creation as process-scoped and GeckoSession as the per-content/session object.
- GeckoView exposes browser-oriented APIs, including WebExtension controller and content-blocking APIs. These do not imply universal Firefox/Chrome extension compatibility.

## Phase 0 dependency candidate
- Channel: Stable
- Candidate artifact: `org.mozilla.geckoview:geckoview-stable:159.0.20261022150525`
- The public Maven version index lists 159.0.20261022150525 as the 159.0.x stable line. Mozilla's quick start documents the stable channel naming and Mozilla Maven repository.
- Dependency resolution and compilation still require a successful GitHub Actions build; the pin is not considered validated until CI confirms it.

## Implemented comparison setup
- `:engine-webview` and `:engine-gecko` are isolated adapter modules behind `:engine-api`.
- `:app` has `webview` and `gecko` product flavors so each APK packages one engine adapter.
- Both variants load the same deterministic local HTML data URL from the Phase 0 probe screen.
- WebView enables JavaScript and DOM storage for parity with GeckoView defaults, enables Safe Browsing, disallows mixed content and installs no JavaScript-native bridge.
- GeckoView uses one process-scoped GeckoRuntime and one GeckoSession for the probe; disposing the probe closes the session but does not shut down the shared runtime.

## Caveats
- The deterministic local page isolates HTML/CSS engine rendering from network variability. A separate network workload is still required before choosing an engine for real browsing.
- Debug APK size is not a substitute for Play-delivered download size or ABI-split measurements.
- No measured APK size, PSS, startup, navigation, battery or crash result is available yet.
- GeckoView may increase package size and requires the app to manage engine update cadence and compatibility testing.
- WebView does not provide a full WebExtensions runtime. GeckoView does not guarantee every Firefox or Chrome extension will work.

## Current recommendation
Keep both adapters buildable and compare them on physical 3–4 GB devices. GeckoView remains a candidate, not a final engine decision. ADR-0001 stays Proposed until reproducible build and device evidence is reviewed.
