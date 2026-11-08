# Engine Research — 2026-11-08

## Official documentation
- Android WebView guide: https://developer.android.com/develop/ui/views/layout/webapps/webview
- Mozilla GeckoView overview: https://mozilla.github.io/geckoview/
- Mozilla GeckoView quick start: https://mozilla.github.io/geckoview/consumer/docs/geckoview-quick-start
- GeckoSession API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSession.html
- GeckoView release index: https://mvnrepository.com/artifact/org.mozilla.geckoview/geckoview/versions

## Verified product differences
- Android WebView is an embeddable View; navigation UI and browser-level behavior are application responsibilities.
- Android documents WebView as Chromium-based; installed provider may vary and updates separately from app.
- Mozilla describes GeckoView as a self-contained engine intended for apps and browsers.
- GeckoView uses GeckoRuntime and GeckoSession; the runtime is process-scoped and sessions represent browser content contexts.
- GeckoView exposes browser-oriented APIs, including WebExtension controller and content-blocking APIs. This does not guarantee universal Firefox/Chrome extension compatibility.

## Phase 0/1 dependency
- Channel: Stable.
- Published artifact coordinate: org.mozilla.geckoview:geckoview:159.0.20261022150525.
- The artifactId is geckoview, not geckoview-stable. CI confirmed resolution and Kotlin compilation.
- Compile SDK baseline is Android API 37.2 due GeckoView AAR metadata; targetSdk remains 36.

## Phase 1 implementation
- Separate engine-gecko adapter module; GeckoView is the provisional MVP engine.
- Process-scoped GeckoRuntime provider.
- One GeckoSession per tab; native private mode and tracking protection.
- Activity-context GeckoView instances are attached/detached separately from ViewModel session state.
- Permission delegate denies content, Android and media permissions until permission UX exists.
- Downloads are streamed to app-specific storage and exposed through scoped FileProvider.

## Benchmark status
- WebView and GeckoView physical-device benchmark has not been performed.
- CI verifies build/lint/unit tests, not runtime performance.
- Previous combined debug artifact ZIP was 215,087,168 bytes; ARM split build is configured to reduce per-device payload. Individual APK sizes remain to be measured.

## Current recommendation
GeckoView is the Phase 1 implementation candidate, not a final benchmark winner. Keep WebView comparison adapter and ADR-0001 Proposed until controlled 3–4 GB device results are available.


## Packaging and binary footprint note (2026-11-08)
- The measured ABI-specific debug APKs are 208,688,414 bytes (ARM64) and 169,493,492 bytes (ARMv7). ABI splits remove the other ABI's native libraries but the GeckoView engine remains the dominant package-size concern.
- Android's official size guidance recommends Android App Bundles for device-targeted delivery, R8 code shrinking plus resource shrinking for release builds, and ABI splits when distributing APKs directly: https://developer.android.com/topic/performance/reduce-apk-size
- GeckoView is a self-contained engine bundled with the app, unlike Android WebView which uses the platform provider: https://mozilla.github.io/geckoview/
- Do not strip or exclude GeckoView native libraries as a size workaround. Measure a minified release build and compare with a WebView build before deciding whether engine footprint justifies changing the provisional engine choice.
- Native-library compatibility, including 16 KB page-size alignment, must be verified for the exact GeckoView artifact: https://developer.android.com/guide/practices/page-sizes
