# Engine Benchmark Protocol

## Goal
Compare WebView and GeckoView on target low-/mid-range Android hardware without claiming results before measurements.

## Device matrix
At minimum: one 3 GB RAM device and one 4 GB RAM device if available. Record model, chipset, Android build, available storage and WebView package version.

## Controlled conditions
- Same device and OS.
- Same network and test pages.
- Same screen brightness and power mode.
- Similar thermal/battery state.
- Clear/cold cache and warm-cache runs separately.
- Alternate engine test order to reduce order bias.

## Measurements
- Installed APK size and download size.
- Cold/warm app startup.
- First contentful render proxy (navigation start to first visual completion callback).
- DOM/page completion timing where comparable.
- PSS, Java heap and native heap.
- CPU time and battery delta for fixed browsing workload.
- Crash/ANR and memory-pressure behavior.
- 1, 5 and 10 session/tab workload.

## Reporting
Run at least 5 repetitions per scenario where practical. Report median and p95, raw observations, device/OS/engine versions, and limitations. Do not compare incomparable callback definitions as if they were identical.

## Gate
No final engine decision until both probes build and run on physical hardware.
