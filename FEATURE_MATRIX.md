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
| Tracking protection | Enabled in source | Per GeckoSession; Privacy Intelligence Center reports active session setting and Gecko-reported site exception; no fabricated tracker count |
| Site permissions | Implemented in source (limited capabilities) | HTTPS notifications/autoplay prompts; Gecko StorageController review/reset with read-back; persistent storage, location, camera, microphone and unsupported requests default-deny; private records excluded |
| Connection security indicator | Implemented | GeckoView security callback |
| Secure browser metadata | Implemented in source | Bookmarks, history, regular-tab restoration and download metadata use AES-256-GCM with an Android Keystore key; missing-key decrypt fails closed; instrumentation added, runtime verification pending |
| Bookmarks | Implemented | Local DataStore; max 500; Android Keystore AES-GCM at rest |
| Browsing history | Implemented | Local DataStore; max 300; clear; Android Keystore AES-GCM at rest; repeated URLs are separate rows |
| Downloads | Implemented in source | HTTP(S)-only response URLs, filename sanitization, atomic temp-file finalization, 250 MB streamed cap, canonical app-root restore checks, scoped read-only open/share; private-tab file bytes remain but new private download metadata is not persisted |
| Dark / Light / Premium Glass | Implemented in source | Centralized palettes; overlay theme-scope defect remains |
| Quick Appearance Picker | Implemented | Secondary menu |
| System bar contrast | Implemented | Status/navigation icon colors follow appearance |
| UI accessibility audit | Completed (source review) | Device/TalkBack/Scanner verification and listed fixes pending |
| Responsive toolbar | Pending | Five actions may crowd at narrow widths/large font scales |
| ABI-specific APKs | Build configured | ARM64 and ARMv7 QA artifacts |
| Privacy Intelligence Center | Implemented in source | Reports actual Gecko tracking setting/site exception, lists and resets supported stored permissions, discloses AES-GCM metadata encryption, private-session boundaries and unencrypted downloaded file contents; device validation pending |
| Transfer Studio | Not started | Phase 2 |
| QR / reader mode | Not started | Phase 2 |
| WebExtensions | Research only | No compatibility claim |
| Developer Mode | Research only | Future secure opt-in |
| CI | RIVO-11 branch verification pending | Android Keystore instrumentation and new download-policy JVM tests added; require a fresh complete run on this branch |
| Production release signing | Pending | QA AAB is unsigned |
