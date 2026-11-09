# Feature Matrix

Status is classified by evidence level. "Implemented in Main source" does not mean device-verified.

| Capability | Main status | Evidence / remaining work |
|---|---|---|
| Android foundation | Implemented | Modular Android project |
| GeckoView runtime/session | Implemented in Main | Process runtime, per-tab session; engine choice provisional |
| WebView comparison | Not integrated in shipping app | Benchmark work is in open PR #20; it is four commits behind Main and has conflicts to resolve |
| Final engine decision | Pending | Physical 3–4 GB device comparison |
| Navigation / omnibox | Implemented in Main | JVM regression coverage; manual navigation regression pending |
| Back / forward / reload / stop | Implemented in Main | Device regression pending |
| Multi-tab management | Implemented in Main | Eight-tab cap; Advanced Tab Universe merged via PR #23: case-insensitive title/URL search, All/Normal/Private/Active filters and Active-first/Title/Type sorting; PR-head CI #293 passed; PR #24 merged as `350d4f3...`; CI #300 passed assemble, lint, JVM tests, minified bundle and artifact reporting; manual device QA pending |
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
| Accessibility | Source improvements integrated in Main via PR #24 | Persistent tab-search label, selected filter semantics, active/inactive tab state and tab-specific close labels; CI #300 passed on PR #24 head; TalkBack/device verification incomplete |
| Responsive panels | Partial | Main adapts panel height; PR #24 integrates horizontally scrollable tab filters; verify 320 dp width, large font and actual device behavior |
| Reader Mode | Not implemented/verified in Main | Phase 2 |
| QR scanner | Not implemented/verified in Main | Phase 2 |
| Developer Mode | Research only | Implement only verified GeckoView capabilities |
| WebExtensions | Research only | No compatibility claim |
| GeckoView/WebView benchmark | Not integrated/verified | PR #16 closed unmerged; PR #20 contains benchmark integration work |
| Android Keystore instrumentation | Pending integration/verification | PR #20/#21; fresh CI and device evidence required |
| 16 KB page-size validation | Pending fresh CI/runtime | PR #20 includes corrected static checks; runtime test remains |
| Production signing | Not complete | Secure signing configuration and release authorization required |
| Production release | Not released | Release gates remain NO-GO |


## Account B security/privacy integration candidate — 2026-11-09 (PR #36, not Main-integrated)

- Site permissions: HTTPS notifications/autoplay mediation, unsupported/media/Android permission default-deny, normal-mode Gecko permission inventory and revocation read-back; private records excluded; persistent-storage grant marked non-revocable.
- Privacy Intelligence Center: tracking-protection state from actual Gecko session, stored permission inventory/reset, AES-GCM metadata and private-mode boundaries disclosed; no tracker count claim.
- Website notifications: Android 13+ runtime notification permission requested after site-level user consent; private-mode and non-HTTPS/malformed-origin notifications dismissed; notification receiver is non-exported.
- Private downloads: new private-tab download metadata excluded from encrypted browser download index; in-memory record removed when originating private tab closes; downloaded bytes remain app-specific files and are not automatically erased/encrypted.
- Download hardening: only HTTP(S) URLs with host/no user-info; restored files must be direct children of canonical app-specific roots and remain under size cap.
- Legacy download metadata: one-time migration clears ambiguous historical index while preserving files; new-format write marks migration complete atomically.
- CI: API 35 Android Keystore/DataStore instrumentation and Gecko policy unit tests; current PR #36 exact-head CI pending.


## PR #36 follow-up implementation — 2026-11-09 (not Main-integrated)

- Downloads: HTTPS-only URL policy; host/user-info validation; bounded MIME syntax normalization with octet-stream fallback; existing atomic staging, canonical restore-root checks and 250 MB cap retained.
- Privacy Intelligence Center: confirmation-gated Clear Browsing Data closes active Gecko sessions, clears Gecko site data/site settings and local history/download metadata, while retaining bookmarks and downloaded files.
- Supply chain: Gradle dependency locking enabled across project configurations; PR-specific OSV job generates and scans resolved module lockfiles and commits them to the worker branch.
- These are source-level implementation changes. Exact-head CI/OSV, emulator behavior and physical-device validation are still required before marking the features fully verified.
