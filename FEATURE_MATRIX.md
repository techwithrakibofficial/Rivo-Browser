# Feature Matrix

Status is classified by evidence level. "Implemented in Main source" does not mean device-verified.

| Capability | Main status | Evidence / remaining work |
|---|---|---|
| Android foundation | Implemented | Modular Android project |
| GeckoView runtime/session | Implemented in Main | Process runtime, per-tab session; engine choice provisional |
| WebView comparison | Not integrated in shipping app | Benchmark work is in closed PR #16 / open PR #20; verify branch state |
| Final engine decision | Pending | Physical 3–4 GB device comparison |
| Navigation / omnibox | Implemented in Main | JVM regression coverage; manual navigation regression pending |
| Back / forward / reload / stop | Implemented in Main | Device regression pending |
| Multi-tab management | Implemented in Main | Eight-tab cap; Advanced Tab Universe merged via PR #23: case-insensitive title/URL search, All/Normal/Private/Active filters and Active-first/Title/Type sorting; PR-head CI #293 passed; PR #24 adds semantic filter chips, horizontal filter scrolling and clearer state/actions, CI pending; manual device QA pending |
| Selected-tab restoration | Implemented in Main source | Duplicate URL index fix; force-stop/relaunch verification pending |
| Private tabs | Implemented in Main | Native Gecko private mode; app-side history/bookmark/restoration excluded; device test pending |
| Tracking protection | Enabled in Main source | Per GeckoSession; no tracker count/full-blocking/anonymity claim |
| Site permissions | Partial in Main | Expanded mediation and revoke UI in open PR #10; device validation pending |
| Privacy Intelligence Center | Not integrated in Main | Open PR #21 stacked on #10 |
| Bookmarks / history | Implemented in Main | Local DataStore, bounded lists, encrypted metadata; Keystore/DataStore instrumentation pending |
| Downloads | Implemented in Main | Confirmation, HTTP(S), 250 MB cap, app-specific storage; hardening/private metadata policy in PR #21 |
| Transfer Studio | Not implemented/verified in Main | Phase 2 |
| Dark / Light / Premium Glass | Implemented in Main source | Overlay and responsive UI regression evidence required |
| Quick Appearance Picker | Implemented in Main | Secondary menu |
| Accessibility | Source improvements in Main and PR #24 | Persistent tab-search label, selected filter semantics, active/inactive tab state and tab-specific close labels; PR #24 CI pending; TalkBack/device verification incomplete |
| Responsive panels | Partial | Main adapts panel height; PR #24 adds horizontally scrollable tab filters; verify 320 dp width, large font and actual device behavior |
| Reader Mode | Not implemented/verified in Main | Phase 2 |
| QR scanner | Not implemented/verified in Main | Phase 2 |
| Developer Mode | Research only | Implement only verified GeckoView capabilities |
| WebExtensions | Research only | No compatibility claim |
| GeckoView/WebView benchmark | Not integrated/verified | PR #16 closed unmerged; PR #20 contains benchmark integration work |
| Android Keystore instrumentation | Pending integration/verification | PR #20/#21; fresh CI and device evidence required |
| 16 KB page-size validation | Pending fresh CI/runtime | PR #20 includes corrected static checks; runtime test remains |
| Production signing | Not complete | Secure signing configuration and release authorization required |
| Production release | Not released | Release gates remain NO-GO |
