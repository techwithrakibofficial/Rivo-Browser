# Project State

Updated: 2026-11-09 (Core browser integration)

## Canonical repository state
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Default branch: `main`
- Main HEAD after Advanced Tab Universe merge: `d192baf0f38db835904aa76a901d7e7cb2a24e31`
- Latest commit: `feat(tabs): add advanced tab universe`
- The earlier checkpoint `84303588c2560db55bfb7ae2081d8f714be532e7` is no longer current.
- PR #23 head `2f4027ac5dd5f8b60241485527c290b8b27916fa` passed Android CI run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026). Its squash merge is `d192baf0...`; a separate push-triggered CI result for that merge commit has not been confirmed.

## Open pull requests observed
- PR #24 — Core follow-up for Tab Universe accessibility and narrow layouts. Open against Main `d192baf0...` from branch `core/tab-universe-accessibility-2026-11-09`; CI result has not yet been returned.
- PR #10 — RIVO-06 privacy and per-site permission controls. Open, based on old Main checkpoint `84303588...`; connector reports mergeable=false. CI #268 passed assembleDebug, lintDebug and JVM unit tests for its recorded head; release bundle was still in progress in its description. Device validation is explicitly outstanding.
- PR #20 — RIVO-04/05/07/09 hardening, instrumentation, engine benchmark and 16 KB validation. Open, connector reports mergeable=true. Previous run #280 failed only due to an over-broad 32-bit ELF alignment check; run #283 identified/fixed an injected DataStore reference. Fresh CI on current PR head, including API 35 instrumentation, is required.
- PR #21 — RIVO-11 privacy center and private-download hardening. Open and stacked on PR #10; must be rebased after its dependency is integrated. Fresh CI is required.
- PR #17's preserved Advanced Tab Universe work was superseded by PR #23 and is now integrated in Main at `d192baf0...`. PR #16 benchmark remains closed/unmerged and is not integrated.
- Branch/PR status is a snapshot from this audit and may change.

## Implemented in Main source (not equivalent to device-verified)
- Process-scoped GeckoRuntime and per-tab GeckoSession architecture.
- Navigation, URL/search omnibox, back/forward/reload/stop and Gecko-reported connection security.
- Up to eight tabs; native Gecko private sessions; private tabs excluded from app-side history/bookmarks/restoration.
- Advanced Tab Universe: case-insensitive title/URL search, All/Normal/Private/Active filters, Active-first/Title/Type sorting, active-tab indication and tab close/switch; the eight-tab cap is unchanged.
- Per-session tracking protection.
- Local DataStore bookmarks (max 500), history (max 300), restorable tabs and download metadata; sensitive metadata encrypted with Android Keystore AES-256-GCM.
- Confirmation-gated HTTP(S) downloads, app-specific storage, temporary-file streaming, 250 MB cap, atomic finalization and scoped FileProvider open/share.
- Dark, Light and Premium Glass palettes; secondary-menu Quick Appearance Picker.
- Regular HTTP(S) tab persistence/restoration and selected-tab index fix.
- Security fail-closed metadata write handling, malformed-record rejection, IDN normalization and relevant JVM regression tests.
- UI panel adaptive-height source change appears in latest Main commit; visual/device acceptance remains unverified.

## Work in open PRs / not yet integrated
- Tab Universe accessibility polish (persistent search label, semantic FilterChip selection, horizontal filter scrolling, active/inactive and tab-specific close announcements): PR #24, awaiting CI and merge.
- Per-site GeckoView permission mediation, permission inventory/revocation and notification handling: PR #10.
- Consolidated Keystore/DataStore hardening, Android API 35 instrumentation, GeckoView/WebView benchmark harness, corrected 16 KB validation and opt-in signing: PR #20.
- Privacy Intelligence Center, fail-closed missing-key decryption, private-download metadata exclusion, download validation and Keystore instrumentation: PR #21 (stacked on #10).

## Known release blockers
1. Obtain successful CI on PR #24 before merge, and separately verify push-triggered CI for Main merge commit `d192baf0...`; PR-head CI #293 is green but is not a substitute for Main CI.
2. PR #10 is based on an older Main checkpoint and reports mergeable=false; resolve/rebase before integration.
3. PR #20 needs fresh CI on its current head and review of API 35 instrumentation/16 KB checks.
4. PR #21 depends on PR #10 and needs rebase plus fresh CI.
5. Recover/review PR #16 benchmark work only if it remains useful; Advanced Tab Universe is now integrated through PR #23.
6. Keystore round-trip/tampering/key-loss, DataStore corruption/relaunch and process-death restoration require executed instrumentation/device evidence.
7. No physical 3–4 GB device benchmark or 16 KB Android runtime test is claimed.
8. Dependency/SBOM review and OWASP MASVS assessment are incomplete; no MASVS certification is claimed.
9. TalkBack/manual accessibility and narrow-screen/large-font regression evidence is incomplete.
10. Production signing and release are not complete. Unsigned CI AABs are QA artifacts only.
11. Transfer Studio, Reader Mode, QR scanning and verified extension workflows are not confirmed implemented in Main.

## Current engineering phase
Phase 1 — Functional Browser MVP stabilization and verification. Do not declare Phase 1 accepted until the above automated and manual/device gates have evidence. Phase 2 features should proceed only after integration dependencies and release blockers are tracked.

## Exact next steps
1. Verify Main CI for merge commit `d192baf0...` and keep future Main commits green.
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
- PR #23 was merged after CI #293 passed on its head. Post-merge Main CI and all physical-device/UI validation remain separate evidence gates.
