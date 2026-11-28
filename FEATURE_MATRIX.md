## 2026-11-28 — v0.2.5 signed release published and verified

- PR #104 merged; documentation baseline Main commit: `3a284aa9b11dd4e43d8dde6f56baaabea276c84d`.
- Immutable annotated tag `v0.2.5-phase1` resolves to source commit `4d344128fcecac3b64476a71606b0e05d92051f9` (PR #103 crash-remediation source).
- Signed Android Release workflow run #46111644854 completed successfully: https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/46111644854.
- Both release jobs passed. Build job verified production signing configuration, signed/minified ARM64 and ARMv7 APKs, signed AAB, signer identity/signatures, retained R8 mapping, and removed temporary keystore. Publish job uploaded verified assets.
- Retained workflow artifacts: signed release bundle (artifact ID `15250660159`) and R8 mapping (artifact ID `15249902905`); both reported unexpired at verification time.
- Public release: https://github.com/techwithrakibofficial/Rivo-Browser/releases/tag/v0.2.5-phase1. Assets: `app-arm64-v8a-release.apk`, `app-armeabi-v7a-release.apk`, `app-release.aab`, and `SHA256SUMS`.
- PR #104 documentation merge and subsequent Main workflows #46112116262 (Android CI), #46112116297 (dependency verification bootstrap), #46112116318 (Gradle Dependency Graph), #46112116254 (Source SBOM), and #46112116518 (OSV Scanner) completed successfully. Main SHA at this checkpoint is `3a284aa9b11dd4e43d8dde6f56baaabea276c84d`.
- Physical TECNO KM9 (Android SDK 36) cold-start/repeated-launch/QR-scan acceptance remains **not yet evidenced**. Do not state the device-specific startup crash is confirmed fixed until this test passes.
- Remaining release-quality gates: physical-device feature QA, 3–4 GB memory/performance profiling, TalkBack and large-font/accessibility checks, runtime 16 KB validation on target hardware, and a documented MASVS/MASTG assessment. No independent penetration test or MASVS certification is claimed.

---

**Acceptance distinction:** successful CI and signed publication prove the build/release pipeline, not physical-device runtime acceptance. QR scanner lazy initialization is source-integrated; device confirmation remains pending.

## 2026-11-28 — QR scanner startup regression update

- QR scanner remains **Integrated**, but its Google ML Kit client is now created only after the user explicitly selects Scan QR code; it is no longer initialized during initial browser composition.
- Synchronous scanner initialization errors are handled with a user-facing notice. Compatible Google Play services remain required.
- PR #103 merged to Main; Android CI, emulator instrumentation and supply-chain checks passed.
- TECNO KM9 physical-device launch and QR-scan acceptance remain **Pending**. Automated CI does not establish device acceptance.

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


## 2026-11-24 Android CI evidence update
- ARM64 and ARMv7 shipping debug APK variants: **built and uploaded** by successful Main CI run #45326960699; QA/debug only.
- API 35 x86_64 emulator instrumentation: **passed** for the currently checked-in instrumented regression tests; this does not cover all feature journeys.
- Static 16 KB package/native alignment: **passed** for the CI APK/AAB outputs; runtime 16 KB hardware verification remains pending.
- Physical device, low-RAM performance and accessibility acceptance: **pending**.


## 2026-11-24 Main verification update

- Universal no-ABI-split debug APK: Integrated and CI-verified on Main via PR #72; static 16 KB alignment check passed. This is not runtime 16 KB hardware validation.
- Main Android CI #45334566292, Gradle dependency verification bootstrap #45334566241, Dependency Graph #45334566254, OSV Scanner #45334566666 and Source SBOM #45334566273: success on 39c35e64ab4260adb7787fed919c29cd098ac40a.
- CI artifacts include ARM64/ARMv7 debug APKs, universal debug APK, unsigned QA AAB, test reports and benchmark outputs. Production-signed release remains pending.
- PR #66 governance hardening is merged; CODEOWNERS maps the repository to @techsomadhanmedia; active ruleset requires one approval and code-owner review.
- Physical-device, accessibility, low-RAM, runtime 16 KB and full MASVS/MASTG/independent penetration assessment remain pending; do not treat emulator CI as device acceptance.


## 2026-11-24 — Current release verification checkpoint
- Features marked Integrated remain source-integrated; this status is not equivalent to device acceptance.
- PR #85's signed-release APK-after-clean correction is merged to Main at `4821c7af1255486e25b98305fd2b74cde33f488d` and passed all five PR workflows plus CODEOWNER review.
- Production signing/publication remains Pending until a protected signed-release run and artifact signature/checksum verification complete.
- Physical-device QA, 3–4 GB profiling, TalkBack/accessibility, runtime 16 KB and full MASVS/MASTG review remain Pending.

## 2026-11-25 — Desktop Site candidate
| Feature | Status | Notes |
|---|---|---|
| Desktop Site / Request desktop website | Implemented on review branch; CI pending | Per-tab GeckoView user-agent and viewport modes; toggle back to mobile; setting not persisted across process restart |

## 2026-11-26 — Main feature status update

| Feature | Current status | Evidence / limitation |
|---|---|---|
| Native Rivo start page | Integrated in Main | PR #100 merged at `3cca911`; native Compose page, internal home marker not sent to GeckoView/network |
| Private-mode history separation on start page | Integrated in source | Private mode omits history; physical-device privacy lifecycle still needs QA |
| Dark / Light / Premium Glass start-page surfaces | Integrated in source | Visual contrast, TalkBack and low-end rendering acceptance pending |
| Desktop Site | Not integrated in Main | Candidate branch diverged 15 commits ahead/behind; port and validate on fresh Main branch before claiming availability |
| Production signed APK/AAB | Not verified | No current-Main signed release evidence in this checkpoint |

## Correction — Desktop Site (2026-11-26)

| Feature | Main status | Remaining validation |
|---|---|---|
| Desktop Site | Integrated by PR #93 | Per-tab GeckoView user-agent + viewport modes and mobile reset are present; physical-site compatibility, tab isolation, private-tab and restore QA pending |
