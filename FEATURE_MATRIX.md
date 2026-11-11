# Rivo Browser — Feature Matrix

Updated: 2026-11-10

Status: **Integrated** means source is merged into Main; it does not imply physical-device acceptance.

| Feature | Status | Notes / limits |
|---|---|---|
| GeckoView browser engine | Integrated | Process-scoped runtime, per-tab GeckoSession |
| WebView comparison engine | Available for benchmark | Not selected as shipping engine; physical benchmark comparison remains |
| Navigation / omnibox | Integrated | HTTP(S) navigation; unsupported schemes become search input |
| Back / forward / reload / stop | Integrated | Gecko session controls |
| Connection security | Integrated | Gecko-reported security callbacks; no TLS override |
| Multi-tab browsing | Integrated | Eight-tab cap pending device memory measurements |
| Private browsing | Integrated | Native private sessions; excludes app-side history/bookmarks/restoration |
| Advanced Tab Universe | Integrated | Search, filters, sorting and semantic accessibility labels |
| Bookmarks / history | Integrated | Local DataStore; encrypted metadata |
| Tab restoration | Integrated | Normal HTTP(S) tabs only; private tabs excluded; background restored pages load on first selection |
| Safe downloads | Integrated | Confirmation, HTTPS policy, app-specific storage, atomic writes, 250 MB cap |
| Transfer Studio | Integrated | Search/filter, recent-100, progress, cancel (best-effort), Open/Share/Remove |
| Reader Mode | Integrated | Gecko readerability and gated-content checks; bounded plain text; not universal |
| QR scanner | Integrated | Google Code Scanner; compatible Play services required; user confirmation |
| Site permissions | Integrated | HTTPS-supported capability prompts; unsupported capabilities default-deny |
| Privacy Intelligence Center | Integrated | Actual engine state/permission records; no invented tracker counts |
| Website notifications | Integrated | HTTPS and normal-mode only; Android notification permission required |
| Clear Browsing Data | Integrated | Clears site data/settings and local history/download metadata; bookmarks and normal files retained |
| Developer Mode | Integrated | Read-only diagnostics; no remote debugging or console |
| WebExtensions | Not user-supported | Gecko engine APIs exist; no Rivo add-on manager/marketplace |
| Dark / Light / Premium Glass | Integrated | Centralized semantic tokens; manual contrast/device validation remains |
| Quick Appearance Picker | Integrated | Secondary control; no permanent main-screen theme icon |
| Accessibility | Partial | Source semantics improved; TalkBack/device validation pending |
| 3–4 GB optimization | Pending validation | No physical-device benchmark evidence |
| 16 KB page-size support | Static checks integrated | Runtime hardware validation pending |
| Production signing / release | Pending | CI artifacts unsigned; signing secrets not available in source session |

## Verification boundary
Build success, JVM tests, emulator instrumentation, physical-device testing and release signing are separate evidence levels. Do not mark device-only features as accepted without recorded hardware results.


| Website notification lock-screen privacy | Integrated in release-hardening branch; pending fresh CI/merge | Private visibility and generic public notification version; OEM/device validation remains pending |
| OWASP MASVS assessment | Preliminary register added in release-hardening branch | Source-based gap assessment only; not certification or independent penetration test |
