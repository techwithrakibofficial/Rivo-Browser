# Engine Research — 2026-11-08

## Official sources
- Android WebView guide: https://developer.android.com/develop/ui/views/layout/webapps/webview
- Mozilla GeckoView overview: https://mozilla.github.io/geckoview/
- GeckoView quick start: https://mozilla.github.io/geckoview/consumer/docs/geckoview-quick-start
- GeckoView API changelog: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/doc-files/CHANGELOG

## Verified findings
- WebView is a View component for displaying web pages; browser navigation controls/address bar are app responsibilities.
- Android documents that WebView is Chromium-based and that instances retain memory unless properly removed and destroyed.
- Mozilla describes GeckoView as a self-contained engine designed for apps and browsers.
- GeckoView API surface includes GeckoSession navigation, delegates, history, content blocking and extension controller APIs (subject to exact stable artifact/version validation).

## Important caveats
- WebView does not provide full Chrome-style WebExtensions.
- GeckoView does not guarantee compatibility with every Firefox or Chrome extension.
- Bundled GeckoView may increase APK/download size and requires app-managed engine updates.
- No measured size, RAM, startup or battery result exists yet.

## Decision
GeckoView is the provisional prototype candidate; WebView remains the comparison baseline.
