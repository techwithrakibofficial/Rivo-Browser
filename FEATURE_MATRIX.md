# Rivo Browser — Feature Matrix

Updated: 2026-11-11

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
| Accessibility | Source-audited | Compose labels, radio selection semantics, headings and standard Material touch targets reviewed; TalkBack/device validation pending |
| 3–4 GB optimization | Pending validation | No physical-device benchmark evidence |
| 16 KB page-size support | Static checks integrated | Runtime hardware validation pending |
| Production signing / release | Pending | CI artifacts unsigned; signing secrets not available in source session |

## Verification boundary
Build success, JVM tests, emulator instrumentation, physical-device testing and release signing are separate evidence levels. Do not mark device-only features as accepted without recorded hardware results.


| Website notification lock-screen privacy | Integrated in release-hardening branch; pending fresh CI/merge | Private visibility and generic public notification version; OEM/device validation remains pending |
| OWASP MASVS assessment | Preliminary register added in release-hardening branch | Source-based gap assessment only; not certification or independent penetration test |


## 2026-11-11 integration status correction
- Website notifications: **Integrated** — public notification preview is generic Rivo-owned content and notification visibility is private; JVM regression test is included. OEM/lock-screen behavior remains pending physical-device QA.
- OWASP MASVS: **Preliminary assessment register added** in `SECURITY_ASSESSMENT.md`; this is not a completed independent audit or certification.
- PR #46 merged to Main as `b50350a241908fb1f38d2920b7a358451cb436f7`; PR-head Android CI, OSV Scanner, dependency graph and SBOM all passed. Post-merge Main CI was still running at checkpoint.
- Production signing, physical-device acceptance, 3–4 GB profiling, runtime 16 KB validation and branch protection remain pending.


## 2026-11-17 accessibility and 16 KB source audit
- Browser UI is Jetpack Compose; no XML layout resources are present. The only app XML resources are the manifest and FileProvider path policy. The benchmark UI is built with native Android Views.
- Icon-only remove-bookmark action now exposes a contextual TalkBack label; appearance options expose one selectable radio-group item with selected state; Reader Mode text-size slider has an explicit accessible name; panel and reader titles are marked as headings.
- Material 3 controls supply minimum interactive touch-target sizing; the custom appearance selector explicitly enforces a 48 dp minimum row height. Traversal follows the Compose/View hierarchy order; no custom focus-order override is required by the current linear layout.
- AGP 9.5.1 exceeds the Android 16 KB packaging minimum (8.5.1). App and benchmark application modules now explicitly retain uncompressed JNI libraries for 16 KB ZIP alignment. There is no project-owned NDK/CMake/ndk-build source; GeckoView supplies prebuilt native libraries, which CI inspects in packaged APK/AAB artifacts.
- Static ELF/ZIP alignment checks remain CI evidence, not a runtime 16 KB device test. TalkBack, font scaling, narrow-screen and physical-device checks remain pending.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- Canonical Main SHA: `ecd9c45c5db53b0f9f0203c82402fe067b3e622d`. PR #67 was independently approved by `techsomadhanmedia` and squash-merged on 2026-11-23 19:23:45 UTC.
- Active Main ruleset `Protect main branch` requires PR review, at least one approval, CODEOWNERS review, resolved threads and strict check `build`; branch deletion/non-fast-forward updates are blocked and no bypass actors exist.
- PR #67 fixes the Medium mixed-content security-indicator presentation issue in source and adds JVM regression/property tests. GeckoView runtime mixed-content behavior remains unverified on physical devices/fixtures.
- On this Main SHA, OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still in progress at the last captured check; do not claim success before rechecking final conclusions.
- Remaining release gates: production-signed APK/AAB and signer/checksum verification; physical-device QA, accessibility and 3–4 GB RAM profiling; runtime 16 KB verification; full MASVS/MASTG assessment and independent penetration testing where required. CI does not replace these gates; no certification is claimed.
