# Feature Matrix

| Capability | Status | Notes |
|---|---|---|
| Android foundation | Implemented | Phase 0 |
| GeckoView runtime / tab session adapter | Implemented in source | Build verification pending |
| WebView comparison adapter | Retained | Phase 0 evaluation only |
| Final engine decision | Pending | Physical-device benchmark not complete |
| Navigation and omnibox | Implemented in source | HTTP/HTTPS and search query resolver |
| Back / forward / reload / stop | Implemented in source | GeckoSession APIs |
| Multi-tab management | Implemented in source | 8-tab MVP cap; inactive sessions set inactive |
| Private tabs | Implemented in source | Native Gecko private session; local history/bookmarks excluded |
| Tracking protection | Enabled in source | Per GeckoSession |
| Site permissions | Deny-by-default | Permission UI not implemented |
| Connection security indicator | Implemented in source | GeckoView security callbacks |
| Bookmarks | Implemented in source | Local DataStore; add/remove |
| Browsing history | Implemented in source | Local DataStore, capped at 300; clear available |
| Downloads | Partial | Stream to app-specific Downloads; public export/share UI pending |
| Dark / Light / Premium Glass | Implemented in source | Centralized semantic color schemes; build/test pending |
| Quick Appearance Picker | Implemented in source | Secondary overflow menu |
| Accessibility / reduced motion audit | Pending | Manual and automated review required |
| Process-death tab restoration | Not implemented | Future Phase 1 task |
| Privacy Intelligence Center | Not started | Phase 2 |
| Transfer Studio | Not started | Phase 2 |
| QR / reader mode | Not started | Phase 2 |
| WebExtensions | Research only | No compatibility claim |
| Developer Mode | Research only | Future secure opt-in |
| CI | Updated | Latest successful run pending |
