#!/usr/bin/env bash
set -euo pipefail
usage() { echo "Usage: $0 <gecko|webview> <apk> <output-dir> [runs=5] [tabs=1..10]" >&2; exit 2; }
[[ $# -ge 3 ]] || usage
ENGINE="$1"; APK="$2"; OUT="$3"; RUNS="${{4:-5}"; TABS="${{5:-1}"
PKG="com.rivo.browser.enginebenchmark"; COMPONENT="$PKG/.BenchmarkActivity"
case "$ENGINE" in gecko|webview) ;; *) usage ;; esac
[[ -f "$APK" ]] || { echo "APK not found: $APK" >&2; exit 1; }
[[ "$RUNS" =~ ^[1-9][0-9]*$ ]] || usage
[[ "$TABS" =~ ^([1-9]|10)$ ]] || usage
command -v adb >/dev/null || { echo "adb is required" >&2; exit 1; }
mkdir -p "$OUT"
sha256sum "$APK" > "$OUT/apk-sha256.txt"
wc -c "$APK" > "$OUT/apk-size.txt"
adb wait-for-device
{
  echo "engine=$ENGINE"; echo "tab_count=$TABS"; echo "captured_utc=$(date -u +%FT%TZ)"
  echo "model=$(adb shell getprop ro.product.model | tr -d '\r')"
  echo "manufacturer=$(adb shell getprop ro.product.manufacturer | tr -d '\r')"
  echo "android=$(adb shell getprop ro.build.version.release | tr -d '\r')"
  echo "sdk=$(adb shell getprop ro.build.version.sdk | tr -d '\r')"
  echo "abi=$(adb shell getprop ro.product.cpu.abilist | tr -d '\r')"
  echo "page_size=$(adb shell getconf PAGE_SIZE 2>/dev/null | tr -d '\r' || true)"
  adb shell cat /proc/meminfo | head -n 1
  adb shell dumpsys battery
  adb shell dumpsys thermalservice 2>&1 || true
  adb shell dumpsys webviewupdate 2>&1 || true
} > "$OUT/device-metadata.txt"
adb uninstall "$PKG" >/dev/null 2>&1 || true
adb install "$APK" > "$OUT/install.txt"
for run in $(seq 1 "$RUNS"); do
  prefix="$OUT/run-$(printf '%02d' "$run")"
  adb logcat -c
  adb shell am force-stop "$PKG"
  cold="$(adb shell am start -W -n "$COMPONENT" --ei benchmark_tab_count "$TABS" 2>&1)"
  printf '%s\n' "$cold" > "$prefix-cold-start.txt"
  printf '%s\n' "$cold" | awk -F': *' '/^TotalTime:/ {print $2; exit}' > "$prefix-cold-total-ms.txt"
  adb shell top -b -n 10 -d 1 > "$prefix-cpu-top.txt" 2>&1 || true
  for tab in $(seq 1 "$TABS"); do
    marker=""
    for attempt in $(seq 1 30); do
      marker="$(adb logcat -d -s RivoEngineBench:I '*:S' 2>/dev/null | grep -F "event=dom_ready tab=$tab " | tail -n 1 || true)"
      [[ -n "$marker" ]] && break
      sleep 1
    done
    printf '%s\n' "${{marker:-DOM_READY_TIMEOUT}" > "$prefix-dom-ready-tab-$tab.txt"
  done
  sleep 10
  adb shell dumpsys meminfo "$PKG" > "$prefix-meminfo.txt" 2>&1 || true
  adb shell ps -A > "$prefix-processes.txt" 2>&1 || true
  adb shell dumpsys gfxinfo "$PKG" framestats > "$prefix-framestats.txt" 2>&1 || true
  adb shell dumpsys activity exit-info "$PKG" > "$prefix-exit-info.txt" 2>&1 || true
  adb shell dumpsys activity lastanr > "$prefix-last-anr.txt" 2>&1 || true
  adb logcat -b crash -d > "$prefix-crash-buffer.txt" 2>&1 || true
  adb shell input keyevent KEYCODE_HOME || true
  sleep 2
  pid="$(adb shell pidof "$PKG" 2>/dev/null | tr -d '\r' || true)"
  if [[ -n "$pid" ]]; then
    warm="$(adb shell am start -W -n "$COMPONENT" 2>&1)"
    printf '%s\n' "$warm" > "$prefix-warm-start.txt"
    printf '%s\n' "$warm" | awk -F': *' '/^TotalTime:/ {print $2; exit}' > "$prefix-warm-total-ms.txt"
    echo "process_resident=true pid=$pid" > "$prefix-warm-state.txt"
  else
    echo "process_resident=false warm_not_measured_os_evicted" > "$prefix-warm-state.txt"
  fi
done
echo "Raw captures saved to $OUT"
