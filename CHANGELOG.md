# Changelog

## 2026-11-09 — Tab Universe accessibility merge
- Merged PR #24 as `350d4f3cabcaeea63f5860cffb5669c4e0cd3bc5` after PR-head CI run [#300](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42561554513) passed assembleDebug, lintDebug, JVM unit tests, minified release bundle, size reporting and QA artifact uploads.
- Integrated semantic Material 3 FilterChips, horizontally scrollable filters, persistent search label, active/inactive state announcement and title-specific close action labels.
- Physical 320 dp, large-font and TalkBack validation remain outstanding.

## 2026-11-09 — Advanced Tab Universe integration
- Recovered the preserved Advanced Tab Universe implementation into PR #23 against the updated Main checkpoint and squash-merged it as `d192baf0f38db835904aa76a901d7e7cb2a24e31`.
- Added case-insensitive title/URL search, All/Normal/Private/Active filters, Active-first/Title/Normal-vs-private sorting, active-state indication and tab close/switch while retaining the eight-tab cap and GeckoSession lifecycle.
- PR-head CI run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026) passed assembleDebug, lintDebug, JVM unit tests and minified release bundle; manual device, TalkBack and narrow-screen QA remain open.

## 2026-11-09 — GitHub recovery audit synchronization
- Audited Main HEAD `72700fa6e599af94317d5e7d76eaa211e992281d`; previous `84303588...` checkpoint is historical.
- Recorded open PR dependencies: #10 permissions, #20 security/benchmark integration, #21 privacy center (stacked on #10).
- Recorded PR #17 as superseded by merged PR #23; PR #16 benchmark remains closed/unmerged and is not counted as integrated.
- Corrected feature, testing and handoff status to distinguish Main source, open PR implementation, CI evidence and device verification.
- Advanced Tab Universe source was merged through PR #23; no production release or physical-device QA was performed.

## 2026-11-08 — CI and documentation synchronization
- Verified integrated Main commit `84303588c2560db55bfb7ae2081d8f714be532e7` with GitHub Actions run #246.
- Historical CI success applies only to that exact commit, not later Main commits.
- Security, engine and design-system documentation was aligned with that checkpoint.

## Unreleased — Phase 1 Functional Browser MVP
- GeckoView runtime/session architecture, navigation, private sessions, tracking protection, local bookmarks/history, encrypted metadata and confirmation-gated downloads.
- Security fail-closed metadata handling and Android engine regression fixes.
- Dark, Light and Premium Glass design foundations.
- Device validation, permission mediation, advanced features and production signing remain open.


## 2026-11-09 — CI evidence correction
- Inspected completed job steps for PR #10 CI #268 and PR #20 CI #288 rather than relying on stale PR descriptions.
- PR #10 #268 passed all configured build/lint/JVM/release bundle and QA artifact steps; no instrumentation step was present.
- PR #20 #288 passed benchmark builds, static 16 KB checks, API 35 emulator security instrumentation and configured artifacts on its recorded PR head. Rebase and fresh CI are still required because the branch is five commits behind Main.
- Neither CI run establishes physical-device behavior or 16 KB runtime support.
