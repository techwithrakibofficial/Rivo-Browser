# Project State

Updated: 2026-11-09 (current integration divergence audit)

## Canonical repository state
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: `main`
- Main HEAD after PR #31 documentation merge: `159643bb358c5bf9e92d537acfe9ba5ee1c39f00` (latest application code merge: PR #27 `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`)
- Latest commit: `docs: refresh current Main integration audit`
- The earlier checkpoint `84303588c2560db55bfb7ae2081d8f714be532e7` is no longer current.
- PR #23 head passed CI #293; PR #24 head `4e1a404b50b96d17388fd6839068f9622fb02804` passed CI run [#300](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42561554513), including assembleDebug, lintDebug, JVM unit tests, minified bundleRelease, size reports and QA artifact uploads. PR #24 merged as `350d4f3...`; PR #27 adds two Tab Universe search/filter regression tests and merged as `ad607a7...` after CI #306 passed all configured steps. No push-triggered workflow result for the #27 merge SHA has been returned.

## Latest integration audit (2026-11-09)
- PR #10: current comparison is diverged, 10 commits behind Main, mergeable=false. Its permission changes overlap current `BrowserController.kt`, `BrowserScreen.kt` and Gecko permission code; owner was asked to rebase and rerun CI.
- PR #20: current comparison is diverged, 8 commits behind Main, mergeable=false. It overlaps controller, storage/cipher and build/CI configuration; owner was asked to coordinate conflict resolution and rerun API 35/16 KB checks.
- PR #21 remains stacked on PR #10; rebase only after #10 integration and rerun instrumentation.

## Open pull requests observed
- PR #24 — Tab Universe accessibility/narrow-layout follow-up; merged to Main as `350d4f3...` after CI #300 passed.
- PR #10 — RIVO-06 privacy and per-site permission controls. Open, based on old Main checkpoint `84303588...`; 11 commits behind current Main, connector reports mergeable=false. CI #268 passed all configured steps on its recorded head: assembleDebug, lintDebug, JVM tests, minified release bundle, size reports and QA APK/AAB uploads. No instrumentation or physical-device permission validation was included. Device validation is explicitly outstanding.
- PR #20 — RIVO-04/05/07/09 hardening, instrumentation, engine benchmark and 16 KB validation. Open, based on older Main `72700fa6...`; 9 commits behind current Main and connector reports mergeable=false. PR head `71af60e...` passed all configured CI #288 steps, including shipping assemble/lint/JVM tests, GeckoView and WebView benchmark APK/AAB builds, minified release bundle, static 16 KB alignment checks, artifact uploads and API 35 Android security instrumentation. It does not test integration with current Main or prove physical-device/16 KB runtime behavior.
- PR #21 — RIVO-11 privacy center and private-download hardening. Open and stacked on PR #10; no workflow run returned for its current head. Rebase after #10 integration, then run fresh CI.
- PR #17's preserved Advanced Tab Universe work was superseded by PR #23 and is now integrated in Main at `d192baf0...`. PR #16 benchmark remains closed/unmerged and is not integrated.
- Branch/PR status is a snapshot from this audit and may change.

## Implemented in Main source (not equivalent to device-verified)
- Tab Universe policy regression tests: trimmed search combined with Normal/Private filter and empty search-result behavior; PR #27 CI #306 passed all configured build, lint, JVM test, minified release bundle, size-report and QA artifact steps before merge.
- Process-scoped GeckoRuntime and per-tab GeckoSession architecture.
- Navigation, URL/search omnibox, back/forward/reload/stop and Gecko-reported connection security.
- Up to eight tabs; native Gecko private sessions; private tabs excluded from app-side history/bookmarks/restoration.
- Advanced Tab Universe: case-insensitive title/URL search, All/Normal/Private/Active filters, Active-first/Title/Type sorting, semantic Material 3 filter chips, horizontally scrollable filter row, persistent search label, active/inactive TalkBack state and title-specific close action; the eight-tab cap is unchanged.
- Per-session tracking protection.
- Local DataStore bookmarks (max 500), history (max 300), restorable tabs and download metadata; sensitive metadata encrypted with Android Keystore AES-256-GCM.
- Confirmation-gated HTTP(S) downloads, app-specific storage, temporary-file streaming, 250 MB cap, atomic finalization and scoped FileProvider open/share.
- Dark, Light and Premium Glass palettes; secondary-menu Quick Appearance Picker.
- Regular HTTP(S) tab persistence/restoration and selected-tab index fix.
- Security fail-closed metadata write handling, malformed-record rejection, IDN normalization and relevant JVM regression tests.
- UI panel adaptive-height source change appears in latest Main commit; visual/device acceptance remains unverified.

## Work in open PRs / not yet integrated

- Per-site GeckoView permission mediation, permission inventory/revocation and notification handling: PR #10.
- Consolidated Keystore/DataStore hardening, Android API 35 instrumentation, GeckoView/WebView benchmark harness, corrected 16 KB validation and opt-in signing: PR #20.
- Privacy Intelligence Center, fail-closed missing-key decryption, private-download metadata exclusion, download validation and Keystore instrumentation: PR #21 (stacked on #10).

## Known release blockers
1. Verify push-triggered Main CI for application merge `ad607a7...` when available; PR-head CI #306 passed all configured jobs. The latest Main HEAD is docs-only merge `693ec61...`.
2. PR #10 is 11 commits behind current Main and reports mergeable=false; resolve/rebase before integration.
3. PR #20 is 9 commits behind current Main and reports mergeable=false; reconcile conflicts, rerun CI on the rebased head and review API 35 instrumentation/16 KB static/runtime evidence.
4. PR #21 depends on PR #10 and needs rebase plus fresh CI.
5. Recover/review PR #16 benchmark work only if it remains useful; Advanced Tab Universe is now integrated through PR #23.
6. Keystore round-trip/tampering/key-loss, DataStore corruption/relaunch and process-death restoration require executed instrumentation/device evidence.
7. No physical 3–4 GB device benchmark or 16 KB Android runtime test is claimed.
8. Dependency/SBOM review and OWASP MASVS assessment are incomplete; no MASVS certification is claimed.
9. TalkBack/manual accessibility and narrow-screen/large-font regression evidence is incomplete despite source semantics improvements; no physical-device evidence is available.
10. Production signing and release are not complete. Unsigned CI AABs are QA artifacts only.
11. Transfer Studio, Reader Mode, QR scanning and verified extension workflows are not confirmed implemented in Main.

## Current engineering phase
Phase 1 — Functional Browser MVP stabilization and verification. Do not declare Phase 1 accepted until the above automated and manual/device gates have evidence. Phase 2 features should proceed only after integration dependencies and release blockers are tracked.

## Exact next steps
1. Verify post-merge Main CI for `350d4f3...` if/when triggered; PR #24 CI #300 passed on the PR head.
2. Review PR #20 files and fresh CI; resolve all failures before considering merge.
3. Rebase PR #10 onto current Main, resolve conflicts, run full CI and validate permission flows.
4. Rebase PR #21 onto the integrated permission implementation; run CI including API 35 instrumentation.
5. Recover PR #16 benchmark branch changes if still useful; PR #17 has been superseded by merged PR #23.
6. Complete device QA: process death, private isolation, permission grant/revoke, notifications, download safety, TalkBack, narrow widths, large font, 3–4 GB memory/performance and 16 KB runtime.
7. Complete dependency/SBOM and MASVS source-level assessment.
8. Implement remaining Phase 2 features (Transfer Studio, Reader Mode, QR scanning) only with verified engine capability and tests.
9. Prepare production signing/release only after all gates pass and release credentials are securely configured.

## Verification honesty
- A merged PR, passing JVM test, successful build, emulator instrumentation run and physical-device test are different evidence levels.
- Never claim unexecuted tests, hardware-backed Keystore, full tracker blocking, anonymity, extension support or production readiness.
- PR #23 and PR #24 were merged after their respective PR-head CI runs #293 and #300 passed. Post-merge Main CI and all physical-device/UI validation remain separate evidence gates.

## Account B security integration candidate — 2026-11-09

- Main baseline at branch creation: `0d77826e655267c671498334c11ffad8d1dd5b14`.
- Worker branch: `worker/account-b-security-qa-2026-11-09`.
- PR #36 is open as a draft; do not merge until current-head CI and review pass.
- Integrated candidate changes: GeckoView HTTPS site permission mediation, default-deny policy, normal permission inventory/reset, notification delegate/receiver, private download metadata suppression, URL and restored-file checks, one-time migration of legacy ambiguous download metadata, and regression tests.
- Main's existing Keystore/DataStore hardening, API 35 Keystore instrumentation, atomic download writer, Account A Tab Universe work and static 16 KB checks are preserved.
- The previous candidate CI run was cancelled due to rapid commits; current final SHA requires a fresh complete run.
- Device validation, 16 KB runtime validation, full transitive dependency inventory, MASVS independent assessment and production signing remain open.
