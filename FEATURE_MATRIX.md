# Feature Matrix

| Capability | Status | Notes |
|---|---|---|
| Android foundation | Implemented | Phase 0 |
| GeckoView runtime / session adapter | Implemented | Process runtime, per-tab session |
| WebView comparison adapter | Retained | Phase 0 only |
| Final engine decision | Pending | Physical-device benchmark not complete |
| Navigation and omnibox | Implemented | HTTP/HTTPS and search resolver |
| Back / forward / reload / stop | Implemented | Delegate-driven navigation state |
| Multi-tab management | Implemented | Inactive session handling; cap 8 |
| Private tabs | Implemented | Native private mode; no app history/bookmark writes |
| Tracking protection | Enabled | Per GeckoSession |
| Site permissions | Deny-by-default | User-facing permission controls pending |
| Connection security indicator | Implemented | GeckoView security callback |
| Bookmarks | Implemented | Local DataStore; add/remove |
| Browsing history | Implemented | Local DataStore, max 300; clear |
| Downloads | Implemented | App-specific storage, 250 MB cap, open/share |
| Dark / Light / Premium Glass | Implemented in source | Centralized colors/tokens; CI passed |
| Quick Appearance Picker | Implemented | Secondary menu |
| System bar contrast | Implemented | Status/navigation icon colors follow appearance |
| ABI-specific APKs | Build configured | ARM64 and ARMv7 debug APK artifacts |
| Accessibility audit | Pending | Manual TalkBack/contrast review required |
| Process-death tab restoration | Implemented in source; CI pending | Restores up to eight regular HTTP(S) tabs; private tabs are never serialized; needs force-stop/relaunch validation |
| Privacy Intelligence Center | Not started | Phase 2 |
| Transfer Studio | Not started | Phase 2 |
| QR / reader mode | Not started | Phase 2 |
| WebExtensions | Research only | No compatibility claim |
| Developer Mode | Research only | Future secure opt-in |
| CI | Prior checkpoint passed; latest changes pending | New lifecycle, tab restore, R8 release bundle build to be validated |
