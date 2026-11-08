# Engine Benchmark Protocol

## Goal
Compare Android System WebView and Mozilla GeckoView on representative 3–4 GB Android devices. Separate engine cost from network variability and do not publish results before collecting them.

## Build variants
Run from the repository root:
```bash
gradle --no-daemon :app:assembleWebviewDebug :app:assembleGeckoDebug
```
Expected outputs:
- `app/build/outputs/apk/webview/debug/app-webview-debug.apk`
- `app/build/outputs/apk/gecko/debug/app-gecko-debug.apk`

Both use the same application ID and signing configuration. Install one variant at a time.

## Device matrix
At minimum, test one 3 GB and one 4 GB Android device where available. Record:
- Manufacturer/model and chipset
- Physical RAM and Android version/build
- WebView provider package/version
- Free storage, battery level and thermal state
- APK file size and ABI

## Controlled workload
- Both variants load the same deterministic local HTML data URL from the Phase 0 screen.
- Keep display brightness, power mode, orientation and background app state constant.
- Reboot or force-stop before cold runs; use a separate warm-start series.
- Alternate engine order (WebView, GeckoView, GeckoView, WebView) to reduce order bias.
- Repeat each scenario at least five times where practical.
- Add a separate network workload only after the local workload; keep URL, network and DNS conditions identical.

## Measurements
1. APK bytes on disk.
2. Cold launch duration from `adb shell am start -W -n com.rivo.browser/.MainActivity`.
3. Process memory after the page is visible and after a fixed 10-second settling interval:
   `adb shell dumpsys meminfo com.rivo.browser`.
4. Frame/render diagnostics:
   `adb shell dumpsys gfxinfo com.rivo.browser framestats`.
5. CPU and system trace where possible using Android Studio Profiler or Perfetto.
6. Stability: crashes, ANRs, page load failures and memory-pressure behavior.

## Repeatable run outline
```bash
adb install -r app-webview-debug.apk
adb shell am force-stop com.rivo.browser
adb shell am start -W -n com.rivo.browser/.MainActivity
# Wait until the local test page is visible, then wait 10 seconds.
adb shell dumpsys meminfo com.rivo.browser
adb shell dumpsys gfxinfo com.rivo.browser framestats
```
Repeat with the GeckoView APK. Record raw outputs; do not compare unlike timing callbacks as if they measure the same event.

## Reporting
Report raw observations plus median and p95, device/OS/engine versions, test order, run count and limitations. Keep WebView provider version explicit. Do not extrapolate one device result to all 3–4 GB phones.

## Current result status
No physical-device run has been performed. The repository now contains build variants and the deterministic probe screen, but actual benchmark data remains pending.

## Decision gate
Do not finalize the engine until both variants resolve dependencies, build, launch and pass the same device workload. Keep ADR-0001 Proposed until then.
