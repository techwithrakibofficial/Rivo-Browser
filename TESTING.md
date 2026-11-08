# Testing Plan

## Automated CI
- Compile platform API 37.2; targetSdk 36.
- Shipping app assemble, lint, JVM tests and minified release AAB.
- GeckoView and WebView benchmark APK flavors and release AABs.
- Inspect native ELF LOAD alignment in APK/AAB archives and APK ZIP alignment for 16 KB page sizes.
- Report artifact sizes and upload unsigned QA artifacts for ARM64/ARMv7.

## Latest integrated Main validation before RIVO-07
- Main HEAD: 84303588c2560db55bfb7ae2081d8f714be532e7.
- GitHub Actions run #246: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810
- Result: success (2026-11-08). It predates this PR's harness and alignment audit.

## Prior worker PR validation
- Run #237 security branch: build, lint, JVM tests, minified bundle and QA artifacts passed.
- Run #240 engine branch: build, lint, JVM tests, minified bundle and QA artifacts passed.
- Run #241 UI documentation branch: Android CI passed.
- These do not validate later Main commits or this branch.

## Functional matrix
1. HTTPS/HTTP URL, bare domain, internationalized domain and search term.
2. Unsupported schemes (javascript:, file:, content:) resolve safely as search queries.
3. Back, forward, reload and stop.
4. Create/switch/close tabs; verify inactive sessions deactivate.
5. Duplicate-URL restoration selects by saved record index.
6. Omnibox edit survives asynchronous navigation callbacks.
7. Private tabs do not enter app-side history/bookmarks/restoration.
8. Bookmark/history persistence, duplicate visits and clear.
9. Dark/Light/Glass appearance and overlay contrast.
10. Download confirmation/cancel, stream closure, filename sanitization, collision handling, atomic finalization, 250 MB cap, metadata restoration and open/share.
11. Permission prompts, certificate errors and security label.
12. Activity background/foreground and selected-only activation.
13. Force-stop/relaunch restores regular tabs but not private tabs.
14. Release minification runtime smoke test.
15. Keystore encryption, tampering, key loss, migration and DataStore corruption/relaunch.
16. TalkBack, 48 dp targets, font scale, 320 dp width and panel scrolling.

## Engine benchmark
See docs/research/BENCHMARK_PROTOCOL.md. Build:
- :engine-benchmark:assembleGeckoDebug
- :engine-benchmark:assembleWebviewDebug
- :engine-benchmark:bundleGeckoRelease
- :engine-benchmark:bundleWebviewRelease

Run tools/run-engine-benchmark.sh for both APKs on the same physical device. Its final argument selects 1, 5 or 10 tabs. Preserve cold/warm timings, workload-ready markers, CPU samples, PSS/process snapshots, frame stats, crash logs and ANR/exit-info. Use harness buttons for reload/switch/close stability scenarios. Never invent missing measurements.

This is engine-level comparison, not full browser feature parity. Any production engine change additionally requires privacy, security, permission, download and restoration regression tests.

## 16 KB compatibility
CI runs tools/verify_16kb_alignment.py over shipping app and benchmark APK/AAB artifacts. It checks ELF LOAD alignment and APK ZIP alignment. For AAB metadata, run bundletool dump config --bundle=<artifact.aab> and confirm PAGE_ALIGNMENT_16K. Static checks do not replace launching on a 16 KB Android 15+ image.

## Current limitations
- Main CI #246 passed on the pre-PR HEAD; this branch needs its own CI result.
- No physical-device benchmark, 16 KB runtime launch, TalkBack audit or full feature-equivalent WebView browser test is claimed.
- QA AABs are unsigned and are not production artifacts.
