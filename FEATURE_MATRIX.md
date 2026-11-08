# Feature Matrix

| Capability | Status | Notes |
|---|---|---|
| Android foundation | Implemented | Phase 0 |
| GeckoView runtime / session adapter | Implemented | Process runtime, per-tab session; provisional engine |
| WebView comparison adapter | Retained | Benchmark probe only |
| Final engine decision | Pending | Physical-device WebView/GeckoView benchmark |
| Navigation and omnibox | Implemented | HTTP/HTTPS and search resolver; draft preservation fix integrated |
| Internationalized domain names | Implemented in source | IDN host normalized to ASCII; JVM regression coverage |
| Back / forward / reload / stop | Implemented | Delegate-driven navigation state |
| Multi-tab management | Implemented | Inactive session handling; cap 8 |
| Selected-tab restoration | Implemented in source | Uses saved record index; duplicate URLs supported; force-stop test pending |
| Private tabs | Implemented | Native private mode; no app history/bookmark writes or persisted private tabs |
| Tracking protection | Enabled | Per GeckoSession |
| Site permissions | Partial mediation | HTTPS notifications and persistent-storage prompts; unsupported/device/media requests remain denied |
| Connection security indicator | Implemented | GeckoView security callback |
| Bookmarks | Implemented | Local DataStore; max 500; Android Keystore AES-GCM at rest |
| Browsing history | Implemented | Local DataStore; max 300; clear; Android Keystore AES-GCM at rest; repeated URLs are separate rows |
| Downloads | Implemented in source | Confirmation, atomic temp-file finalization, 250 MB cap, persisted metadata, scoped open/share |
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
| CI | Verified on current checkpoint | Main run #246 passed assembleDebug, lintDebug, testDebugUnitTest, bundleRelease and configured QA artifact steps on commit `84303588c2560db55bfb7ae2081d8f714be532e7` |
| Production release signing | Pending | QA AAB is unsigned |


## Advanced Tab Universe
- Open-tab panel supports case-insensitive title/URL search, All/Normal/Private/Active filters, and Active-first/Title/Normal-vs-Private organization.
- Shows active state, tab type and URL; preserves the existing eight-tab cap and Gecko session lifecycle.
- Does not claim extra memory savings or persistent private-tab restoration; device QA remains required.
