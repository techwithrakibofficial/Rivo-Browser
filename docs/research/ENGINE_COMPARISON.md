# Engine Research — 2026-11-09

## Official documentation reviewed

- Android WebView: https://developer.android.com/develop/ui/views/layout/webapps/webview
- Android WebView memory: https://developer.android.com/develop/ui/views/layout/webapps/manage-webview-memory
- Android Macrobenchmark: https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview
- Android 16 KB pages: https://developer.android.com/guide/practices/page-sizes
- GeckoView: https://mozilla.github.io/geckoview/
- GeckoView quick start: https://mozilla.github.io/geckoview/consumer/docs/geckoview-quick-start
- GeckoSession Javadoc: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSession.html
- GeckoView changelog: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/doc-files/CHANGELOG

The live Mozilla Javadoc describes API 162.0.20261107162351. Rivo remains pinned to 159.0.20261022150525; this audit does not update the dependency because release-note review, CI and runtime regression testing are required.

## Shipping app architecture on main

- app depends on engine-api and engine-gecko; it does not depend on engine-webview.
- engine-gecko packages org.mozilla.geckoview:geckoview:159.0.20261022150525.
- GeckoBrowserRuntime is one synchronized process-scoped GeckoRuntime created with the application context.
- Each browser tab owns one GeckoSession with content, progress, navigation and permission delegates.
- Compose creates a GeckoView for the selected tab. DisposableEffect releases that view on tab change; releaseView removes it from its parent and calls GeckoView.releaseSession().
- BrowserController deactivates the previous session on tab selection, activates/focuses only the selected session while the host is started, and deactivates/unfocuses all sessions on Activity stop.
- Closing a tab detaches its view and closes its session. The runtime is retained for process lifetime. Production tab cap is eight.

Mozilla documents open() as asynchronous (session operations queue until open completes), close() as releasing Gecko objects/unloading the page, and setActive(false) as flushing state and significantly reducing memory use. Inactive sessions should not be visible. releaseSession() detaches a session from a GeckoView; it is not a substitute for closing a session when the tab is disposed.

**Finding:** ownership and lifecycle are coherent at source level. This does not prove absence of leaks or memory regressions; physical-device profiling is needed.

## Existing WebView comparator

engine-webview is a small probe module, not a second browser implementation and is not referenced by the shipping app. It enables JavaScript and DOM storage, disables mixed content and file/content access, enables Safe Browsing, and detaches/stops/destroys the WebView on disposal. Android documentation explains that WebView may use separate Chromium renderer processes and Java heap alone does not capture all content memory.

## Controlled harness

The new engine-benchmark module supplies gecko and webview product flavors from one host Activity, package ID, UI controls, 400-row offline DOM/JS workload and 1/5/10-tab test sizes. It exercises activation, inactive sessions, Activity background/foreground, switching, reload and close.

- Debug builds: :engine-benchmark:assembleGeckoDebug and :engine-benchmark:assembleWebviewDebug.
- Release AABs: :engine-benchmark:bundleGeckoRelease and :engine-benchmark:bundleWebviewRelease.
- Capture: tools/run-engine-benchmark.sh.
- Static ELF/APK alignment: tools/verify_16kb_alignment.py.
- Procedure and limitations: docs/research/BENCHMARK_PROTOCOL.md.

This is engine-level measurement, not full product parity. A production engine change additionally requires browser-level testing of navigation, private mode, tracking protection, permissions, downloads, restoration, security and accessibility.

## Native packaging and 16 KB pages

GeckoView packages native binaries; WebView uses the device's system provider. CI checks native ELF PT_LOAD alignment in APK/AAB archives and APK ZIP alignment. Also inspect AAB metadata for PAGE_ALIGNMENT_16K with bundletool, then install and launch on a 16 KB Android image. Static inspection does not prove runtime compatibility.

Mozilla's public issue #262 described a startup crash on an Android 16 16 KB x86_64 emulator with GeckoView 143; a Mozilla maintainer noted fixes in version 144. That old report is not proof about Rivo's pinned version 159. Inspect the actual packaged binaries and test the selected artifact.

## Package-size evidence

Historical CI run #143 reported debug APKs of 208,688,414 bytes (ARM64) and 169,493,492 bytes (ARMv7). They are not current release sizes or a WebView comparison. The benchmark release AABs and per-ABI APKs provide comparable engine-host artifact measurements.

## Recommendation

Keep GeckoView as the **provisional** shipping engine. There is no measured physical-device performance or full feature-parity evidence to justify a switch. Do not remove required native libraries or sacrifice browser functionality for package size. Revisit only after repeated 3–4 GB device measurements, release artifact comparison, runtime 16 KB tests and browser-level regression coverage.
