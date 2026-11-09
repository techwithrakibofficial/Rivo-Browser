# Rivo Browser — Feature Matrix

Updated: 2026-11-09

Status meanings: **Main** = merged to canonical Main; **Integration candidate** = included in PR #37 but not yet merged; **Pending** = not implemented or not verified.

| Feature | Status | Notes / limits |
|---|---|---|
| GeckoView browser engine | Main | Process-scoped runtime, per-tab GeckoSession |
| WebView comparison engine | Main | Benchmark variant; final engine decision still needs physical measurements |
| Navigation and omnibox | Main | HTTP(S) navigation and search resolution |
| Back / forward / reload / stop | Main | Engine session controls |
| Connection security status | Main | Based on GeckoView security callbacks |
| Multi-tab browsing | Main | Eight-tab cap for memory stability |
| Private browsing | Main | Native private sessions; excludes app-side history/bookmarks/restoration |
| Advanced Tab Universe | Main | Search, filters, sorting, accessible state and close labels |
| Bookmarks and history | Main | Local storage; private tabs excluded |
| Tab restoration | Main | Normal HTTP(S) tabs only |
| Encrypted browser metadata | Main | Android Keystore AES-256-GCM; device-level verification remains |
| Safe downloads | Main | Confirmation, app-specific storage, filename/path checks, 250 MB cap |
| Dark / Light / Premium Glass | Main | Centralized appearance palettes |
| Quick Appearance Picker | Main | Secondary menu control; no permanent theme icon on main display |
| Reader Mode | Integration candidate | GeckoView extraction, readability/gated checks, bounded in-memory text |
| QR scanning | Integration candidate | Google Code Scanner; QR-only, no camera permission for scanner flow, explicit confirmation |
| Transfer Studio | Integration candidate | Search, filters, progress, best-effort cancel, open/share/remove; no pause/resume or resumable queue |
| Developer Mode | Integration candidate | Read-only diagnostics; remote debugging/console/about:config disabled |
| Privacy Intelligence Center | Integration candidate | Actual Gecko permission inventory/state; no fabricated tracker counts |
| Site permissions | Integration candidate | Secure-origin mediation, supported permissions only, unsupported requests default-deny |
| Website notifications | Integration candidate | HTTPS-only, Android notification permission, private-mode notifications denied |
| Private download cleanup | Integration candidate | Private metadata excluded; private files removed on tab close |
| Clear Browsing Data | Integration candidate | Gecko site data/settings and local history/download metadata; normal downloaded file bytes retained |
| End-user WebExtensions | Pending | GeckoView engine support does not provide an add-on manager or marketplace |
| Cloud sync / accounts | Pending | No account or mandatory backend |
| Physical 3–4 GB device benchmark | Pending | No physical-device measurements claimed |
| 16 KB runtime verification | Pending | Static CI alignment is not runtime evidence |
| OWASP MASVS assessment | Pending | Source-level control review and residual-risk record required |
| Production signing / store release | Pending | Unsigned CI AABs are QA artifacts only |

## Integration source
- PR #37 combines Account A PR #35 and Account B PR #36.
- Do not merge #35 or #36 independently because shared files overlap.
- Exact-head PR #37 validation is required before merge.
