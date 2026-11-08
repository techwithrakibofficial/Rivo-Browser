# Engine Benchmark Protocol

Last reviewed: 2026-11-09

## Comparison boundary

Compare Android System WebView with Rivo's pinned GeckoView using one benchmark Activity, package ID, UI controls, deterministic offline HTML/JavaScript workload and the same physical device. Install one flavor at a time.

The new engine-benchmark module is an isolated engine harness. It measures relative engine/host overhead and session behavior; it is **not** a feature-equivalent port of the shipping browser. The shipping app remains GeckoView-only. Full browser feature, privacy and security parity is a separate decision gate.

The previous protocol referenced app flavor tasks that were not configured. The WebView module on main was only a library probe, not an integrated alternate browser.

## Build

~~~bash
gradle --no-daemon :engine-benchmark:assembleGeckoDebug :engine-benchmark:assembleWebviewDebug
gradle --no-daemon :engine-benchmark:bundleGeckoRelease :engine-benchmark:bundleWebviewRelease
find engine-benchmark/build/outputs/apk -name '*.apk' -type f
find engine-benchmark/build/outputs/bundle -name '*.aab' -type f
~~~

Both flavors share package com.rivo.browser.enginebenchmark and must be installed sequentially.

## Device and run controls

Use a physical Android device. Record manufacturer/model/chipset, physical RAM, Android build, ABI, page size, WebView provider package/version, GeckoView artifact version, battery, thermal state, free storage, display refresh/brightness and power mode. Keep device, settings and conditions the same for both engines. Reboot before the first block, cool between blocks, close unrelated apps and alternate order (WebView, GeckoView, GeckoView, WebView).

Run at least five repetitions per engine; 20+ samples are preferred before reporting p95. Use separate output directories for each engine and tab count.

## Capture

The workload is a local data: URI. It creates 400 DOM rows using JavaScript and changes the document title to a per-tab ready marker. It uses no network, DNS, TLS or external assets. The title callback is a workload-ready marker, **not** a first-pixel or first-contentful-paint metric.

~~~bash
tools/run-engine-benchmark.sh gecko path/to/gecko.apk build/bench/gecko-1tab 5 1
tools/run-engine-benchmark.sh webview path/to/webview.apk build/bench/webview-1tab 5 1
tools/run-engine-benchmark.sh gecko path/to/gecko.apk build/bench/gecko-5tabs 5 5
tools/run-engine-benchmark.sh webview path/to/webview.apk build/bench/webview-5tabs 5 5
~~~

Repeat with ten tabs. The script records device metadata, exact APK size/hash, force-stop + am start -W cold process startup, ten top CPU samples, each DOM-ready marker, a fixed 10-second settle followed by PSS/process lists, gfxinfo framestats, exit info, last ANR and crash output. It then backgrounds the app and measures warm task return only when pidof confirms the process remains resident. A killed OS process is marked as not measured, not a warm result.

## Inactive tabs and stability

Use 1-, 5- and 10-tab blocks. Ten tabs is a stress workload; Rivo production currently caps at eight. The harness activates only the selected tab; inactive Gecko sessions receive setActive(false)/setFocused(false), while inactive WebViews receive onPause(). Background/foreground the Activity, switch through tabs repeatedly, reload each page at least 25 times, close/reopen tabs and record failures, stale pages, hangs, renderer exits, crashes and recovery.

Memory must include the host and relevant renderer/content child processes. WebView commonly uses Chromium renderer processes; GeckoView may also use child processes. Preserve full ps and dumpsys captures and state exactly which process PSS entries are included in any total. Do not compare host-only figures when child processes differ.

## Metric definitions

| Metric | Capture | Report |
|---|---|---|
| Cold startup | force-stop + am start -W | TotalTime, each run, median |
| Warm startup | Home + task return while PID resident | TotalTime, missing/evicted runs excluded and disclosed |
| Workload ready | Per-tab title callback after DOM/JS work | elapsed ms from Activity creation; not paint time |
| Memory | dumpsys meminfo + ps -A after fixed settle | host and child PSS in KB, membership disclosed |
| CPU | top -b -n 10 -d 1 | raw process samples, child processes included |
| Rendering | dumpsys gfxinfo framestats | raw frame data and comparability limits |
| Stability | exit-info, lastanr, crash log and manual reload tests | counts, affected process, repro and recovery |
| APK/AAB | exact artifacts + SHA-256 | per-ABI APK and release AAB, same build type |

This shell runner is a baseline, not an instrumentation benchmark. Android Macrobenchmark supports COLD/WARM startup, JSON output and Perfetto traces; add an instrumentation target for more rigorous startup profiling.

## 16 KB page-size compatibility

CI inspects every packaged .so in shipping app and benchmark APK/AAB artifacts for ELF PT_LOAD alignment of at least 16,384 bytes. It also runs zipalign -c -P 16 on APKs. For AAB delivery metadata, run:

~~~bash
bundletool dump config --bundle=path/to/app-release.aab | grep alignment
~~~

Expect PAGE_ALIGNMENT_16K. Then install and launch on an Android 15+ 16 KB page-size emulator/device. Static ELF/ZIP checks do not prove runtime compatibility. The AAB bundle config and 16 KB runtime launch are separate acceptance checks.

## Results and decision

Copy docs/research/benchmark-results/TEMPLATE.md for each device/test block. Preserve raw outputs. Report median for five or more runs and p95 only with an adequate sample. Disclose missing warm runs, process exclusions, thermal events and limitations.

**Status (2026-11-09):** benchmark host, flavors and capture scripts are added in RIVO-07. Main run #246 passed on the pre-PR HEAD; this PR's CI must validate these files. No physical-device measurements were collected in this task. Do not publish synthetic values or nominate an engine winner.
