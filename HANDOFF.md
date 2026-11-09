# Handoff

Updated: 2026-11-09 — Tab Universe policy regression tests merged

## Repository checkpoint
- Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Main HEAD after PR #27 merge: `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`
- Latest Main commit: `test(tabs): expand Tab Universe search regression coverage`
- The previous `84303588...` checkpoint and CI run #246 are historical, not validation of the latest Main HEAD.
- Audit branch: `docs/recovery-audit-sync-2026-11-09`

## Open PRs and dependencies
- #24 Tab Universe accessibility/narrow-layout follow-up: merged to Main as `350d4f3...`; PR-head CI #300 passed all configured jobs.
- #10 RIVO-06 permissions: open; based on old Main `84303588...`; 7 commits behind current Main, connector reports mergeable=false. CI #268 passed all configured assemble/lint/JVM/release bundle/QA artifact steps on its old head; rebase and fresh CI required.
- #20 RIVO-04/05/07/09 integration: open; based on old Main `72700fa6...`, 5 commits behind current Main, connector reports mergeable=false. CI #288 passed all configured steps on PR head, including benchmark builds, minified bundle, static 16 KB checks, artifact uploads and API 35 security instrumentation. Rebase and fresh validation are still required.
- #21 RIVO-11 privacy center/download hardening: open; stacked on #10. Rebase after #10 is integrated, then fresh CI.
- #17 Advanced Tab Universe was recovered as PR #23, CI #293 passed on its head, and PR #23 is now merged to Main. PR #16 engine benchmark remains closed/unmerged.
 
- PR #27 adds JVM regression coverage for trimmed search combined with Normal/Private filters and empty results. CI #306 passed all configured jobs before squash merge as `ad607a7...`; post-merge Main CI not yet returned.

## Main source baseline
- GeckoView runtime/session architecture; navigation, omnibox and security status.
- Eight-tab cap; native private sessions; local bookmarks/history/restorable tabs.
- Advanced Tab Universe is integrated: case-insensitive title/URL search, All/Normal/Private/Active filters, active-first/title/type sorting, semantic FilterChips, horizontal filter scrolling, persistent search label, active/inactive TalkBack state and tab-specific close labels.
- Android Keystore AES-256-GCM encrypted browser metadata.
- Confirmation-gated HTTP(S) downloads with app-specific storage and 250 MB cap.
- Dark/Light/Premium Glass themes and secondary Quick Appearance Picker.
- Security/IDN/tab-restoration JVM regressions.
- Latest responsive panel source change is on Main, but device/UI verification is not established.

## Highest-priority next actions
1. Verify push-triggered Main CI for `350d4f3...` when available; PR #24 CI #300 passed all configured checks on its PR head.
2. Review and validate PR #20, then integrate only after successful required checks and code review.
3. Rebase PR #10 to current Main, resolve conflicts and run fresh CI; manually test Gecko permission/notification flows.
4. Rebase PR #21 on the integrated PR #10 work and run fresh CI plus API 35 instrumentation.
5. Recover/review PR #16 benchmark work if still useful; PR #17 has already been superseded by merged PR #23.
6. Run Android Keystore/DataStore, process-death/private-mode, accessibility, download and real-device performance tests.
7. Complete dependency/SBOM and OWASP MASVS review.
8. Finish Transfer Studio, Reader Mode and QR only after Phase 1 acceptance.
9. Prepare production signing and distribution after all release gates pass.

## Evidence limitations
- PR #23 head passed CI #293. PR #24 head `4e1a404...` passed CI #300 (assembleDebug, lintDebug, JVM tests, minified release bundle, size reporting and QA artifact uploads), then merged as `350d4f3...`. No separate Main push-triggered CI run has been returned.
- PR #10 CI #268 completed all configured assemble/lint/JVM/minified release bundle/size-report/QA artifact steps on its recorded head; it did not include Android instrumentation or physical-device permission validation.
- PR #20 CI #288 passed all configured tasks on its recorded head, including API 35 Android security instrumentation and static 16 KB checks. Current Main integration still requires conflict resolution and fresh CI; no physical-device or 16 KB runtime result is claimed.
- PR #21 says fresh CI is required; its instrumentation has not been reported as passing.
- No physical-device tests, 3–4 GB benchmarks, 16 KB runtime test, production signing or release are claimed.

## Session status
PR #23 and PR #24 are integrated. PR #27 (`test(tabs): expand Tab Universe search regression coverage`) passed CI #306 on its PR head and merged as `ad607a7a97f9e9bce8cd57a206b2a438e6003f20`. The two new JVM policy regressions are now in Main. Post-merge Main CI and physical-device verification remain open.
