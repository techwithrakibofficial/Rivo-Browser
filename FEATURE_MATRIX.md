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
| Private tabs | Implemented in Main; hardened in PR #36 candidate | Native Gecko private mode; no app history/bookmark/restoration; private download metadata is not persisted and private files are cleaned on close/disposal/startup |
| Tracking protection | Enabled in Main source | Per GeckoSession; no tracker count/full-blocking/anonymity claim |
| Site permissions | Partial in Main; implemented in PR #36 candidate | HTTPS-origin geolocation/camera/microphone with Android runtime coordination and explicit site consent; notification flow; background/unsupported requests denied; encrypted app-level denials; emulator/device validation pending |
| Privacy Intelligence Center | Not integrated in Main; implemented in PR #36 candidate | Gecko tracking-protection status, normal site-permission inventory/reset, private-session disclosure and confirmation-gated Clear Browsing Data |
| Bookmarks / history | Implemented in Main | Local DataStore, bounded lists, encrypted metadata; Keystore/DataStore instrumentation pending |
| Downloads | Implemented in Main; hardened in PR #36 candidate | HTTPS-only, bounded MIME normalization, filename/path validation, 250 MB cap, atomic staging; private downloads use a separate directory and are deleted on tab close/controller disposal/startup |
| Transfer Studio | First increment implemented on Account A worker branch; not yet in Main | Search/filter All/Active/Completed/Failed, file size, live byte progress, best-effort Cancel, Open/Share/Remove; UI materializes latest 100 only; pause/resume/retry are not claimed because current Gecko external-response stream does not expose a resumable contract |
| Dark / Light / Premium Glass | Implemented in Main source | Overlay and responsive UI regression evidence required |
| Quick Appearance Picker | Implemented in Main | Secondary menu |
| Accessibility | Source improvements integrated in Main via PR #24 | Persistent tab-search label, selected filter semantics, active/inactive tab state and tab-specific close labels; CI #300 passed on PR #24 head; TalkBack/device verification incomplete |
| Responsive panels | Partial | Main adapts panel height; PR #24 integrates horizontally scrollable tab filters; verify 320 dp width, large font and actual device behavior |
| Reader Mode | Native article reader implemented on Account A worker branch; not yet in Main | Uses GeckoView PageExtractionController metadata/readability and plain-text extraction; only eligible articles; 120,000-character cap; runtime/device validation pending |
| QR scanner | Google Code Scanner integration implemented on Account A worker branch; not yet in Main | QR-only, no CAMERA permission, bounded/sanitized result with user confirmation; requires compatible Google Play services; device test pending |
| Developer Mode | Read-only diagnostics panel implemented on Account A worker branch; not yet in Main | Reports engine/API/ABI/session settings; remote debugging and console logging explicitly disabled; no about:config or arbitrary preference editor |
| WebExtensions | GeckoView engine capability verified; no Rivo extension manager in Main or Account A branch | GeckoView supports APK-bundled built-ins and Mozilla-signed external XPI installs; no store/permission manager/update UX yet; do not claim end-user extension support |
| GeckoView/WebView benchmark | Not integrated/verified | PR #16 closed unmerged; PR #20 contains benchmark integration work |
| Android Keystore instrumentation | Pending integration/verification | PR #20/#21; fresh CI and device evidence required |
| 16 KB page-size validation | Pending fresh CI/runtime | PR #20 includes corrected static checks; runtime test remains |
| Production signing | Not complete | Secure signing configuration and release authorization required |
| Production release | Not released | Release gates remain NO-GO |


## Account A feature branch checkpoint — 2026-11-09

Branch: `worker/account-a-core-features-2026-11-09` (based on Main `0d77826e655267c671498334c11ffad8d1dd5b14`).

- Reader Mode uses the pinned GeckoView page-extraction API and native Compose article presentation.
- QR scanning uses Google Code Scanner with no camera permission; result opening is user-confirmed and goes through the HTTP(S)-only navigation resolver.
- Transfer Studio adds local transfer search/filter and managed-file actions; it does not claim pause/resume or retry.
- Developer Mode is read-only diagnostics; remote debugging/console output stay disabled.
- The branch is not yet merged. Fresh CI and device validation are required before marking these features integrated.
- Extension research is recorded in `docs/research/GECKOVIEW_CAPABILITIES.md`; no extension manager or marketplace is implemented.


## Account B security/privacy integration candidate — 2026-11-09 (PR #36, not Main-integrated)

- Site permissions: HTTPS notifications/autoplay mediation, unsupported/media/Android permission default-deny, normal-mode Gecko permission inventory and revocation read-back; private records excluded; persistent-storage grant marked non-revocable.
- Privacy Intelligence Center: tracking-protection state from actual Gecko session, stored permission inventory/reset, AES-GCM metadata and private-mode boundaries disclosed; no tracker count claim.
- Website notifications: Android 13+ runtime notification permission requested after site-level user consent; private-mode and non-HTTPS/malformed-origin notifications dismissed; notification receiver is non-exported.
- Private downloads: metadata excluded from persistent index; files isolated in `.rivo-private` and deleted on tab close, controller disposal and next-process startup.
- Download hardening: HTTPS-only URLs with host/no user-info; MIME normalization; restored files must be direct children of canonical app-specific roots and remain under size cap.
- Legacy download metadata: one-time migration clears ambiguous historical index while preserving files; new-format write marks migration complete atomically.
- CI: API 35 Android Keystore/DataStore instrumentation and Gecko policy unit tests; current PR #36 exact-head CI pending.


## PR #36 follow-up implementation — 2026-11-09 (not Main-integrated)

- Downloads: HTTPS-only URL policy; host/user-info validation; bounded MIME syntax normalization with octet-stream fallback; existing atomic staging, canonical restore-root checks and 250 MB cap retained.
- Privacy Intelligence Center: confirmation-gated Clear Browsing Data closes active Gecko sessions, clears Gecko site data/site settings and local history/download metadata, while retaining bookmarks and downloaded files.
- Supply chain: Gradle dependency locking enabled across project configurations; PR-specific OSV job generates and scans resolved module lockfiles and commits them to the worker branch.
- These are source-level implementation changes. Exact-head CI/OSV, emulator behavior and physical-device validation are still required before marking the features fully verified.


## PR #36 candidate additions — 2026-11-09 (not Main-integrated)

- Clear Browsing Data closes sessions before GeckoView SITE_DATA/SITE_SETTINGS clearing, then clears local history/download metadata; bookmarks and normal download files are retained.
- Android camera/microphone/location denials are encrypted in local storage; the UI discloses denied app-level permissions and points users to Android Settings to re-enable them.
- Supply-chain validation: five Gradle lockfiles, OSV scan of 788 package instances with no issues, SPDX source SBOM with 809 packages and successful Gradle dependency graph artifact generation.
- Static 16 KB APK/native-library alignment and minified QA AAB build passed on the code-equivalent CI run. Emulator instrumentation is being rerun with KVM setup; physical-device validation remains outstanding.
