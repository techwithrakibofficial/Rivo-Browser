# Feature Matrix

| Capability | Status | Notes |
|---|---|---|
| Android foundation | Implemented | Phase 0 |
| GeckoView runtime / session adapter | Implemented in source | Process runtime, per-tab session; activation policy has JVM coverage; Activity/engine lifecycle needs device QA |
| WebView comparison adapter | Retained | Benchmark probe only |
| Final engine decision | Pending | Physical-device WebView/GeckoView benchmark |
| Navigation and omnibox | Implemented in source | HTTP(S), bare host, search and draft preservation have JVM coverage; live site loading remains device QA |
| Internationalized domain names | Implemented in source | IDN host normalized to ASCII; encoded path/query/fragment and port behavior covered by JVM tests |
| Back / forward / reload / stop | Implemented | Delegate-driven navigation state |
| Multi-tab management | Implemented in source | Inactive session handling; cap 8; duplicate-URL snapshot semantics have JVM coverage |
| Selected-tab restoration | Implemented in source | Safe normal-tab snapshots retain duplicate URLs and selected tab identity; force-stop/relaunch remains device QA |
| Private tabs | Implemented in source | Snapshot policy excludes private tabs; Gecko private session is per-tab; cookie isolation and process-relaunch remain device QA |
| Tracking protection | Enabled | Per GeckoSession |
| Site permissions | Partial mediation | HTTPS notifications and persistent-storage prompts; unsupported/device/media requests remain denied |
| Connection security indicator | Implemented | GeckoView security callback |
| Bookmarks | Implemented in source | Local DataStore; max 500; Android Keystore AES-GCM at rest; user actions need device smoke test |
| Browsing history | Implemented in source | Consecutive callbacks coalesce; a revisit after another URL remains a separate row; UI and DataStore share the same policy |
| Downloads | Implemented in source | Confirmation/cancel, sanitized names, unique reserved target, atomic temp-file rename, 250 MB cap and app-scoped open/share; writer JVM tests |
| Dark / Light / Premium Glass | Implemented in source | Centralized palettes; overlay theme-scope defect remains |
| Quick Appearance Picker | Implemented | Secondary menu |
| System bar contrast | Implemented | Status/navigation icon colors follow appearance |
| UI accessibility audit | Completed (source review) | Device/TalkBack/Scanner verification and listed fixes pending |
| Responsive toolbar | Pending | Five actions may crowd at narrow widths/large font scales |
| ABI-specific APKs | Build configured | ARM64 and ARMv7 QA artifacts |
| Privacy Intelligence Center | Not started | Phase 2 |
| Transfer Studio | Not started | Phase 2 |
| QR / reader mode | Not started | Phase 2 |
| WebExtensions | Research only | No compatibility claim |
| Developer Mode | Research only | Future secure opt-in |
| CI | RIVO-05 branch validation pending | Main baseline run #246 passed; update this row after the RIVO-05 head CI concludes |
| Production release signing | Pending | QA AAB is unsigned |
