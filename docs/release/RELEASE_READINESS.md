# Rivo Browser — Release Readiness

**Decision: NO-GO for production distribution.**  
Audit date: 2026-11-09  
Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser  
Main baseline used by Account A/B worker branches: `0d77826e655267c671498334c11ffad8d1dd5b14`.

## Current integration state

- PR #33 storage hardening / API 35 instrumentation / benchmark and static 16 KB checks is merged into Main. Its PR-head Android CI run #42563889346 completed successfully. This is not physical-device or 16 KB runtime evidence.
- PR #34 OSV Scanner and SPDX source SBOM workflows is merged into Main. Its PR-head Android CI, OSV and source SBOM workflows completed successfully. The source SBOM is not a full resolved Gradle dependency inventory.
- PR #35 Account A Reader Mode, QR Scanner and Transfer Studio work is open on `worker/account-a-core-features-2026-11-09`; not yet Main-integrated.
- PR #36 Account B site permissions, Privacy Intelligence Center, private-download metadata and security QA work is open on `worker/account-b-security-qa-2026-11-09`; not yet Main-integrated.
- Historical PR #10 and #21 remain open on stale/stacked branches. Their relevant work is being integrated through PR #36; do not merge #10/#21 directly.
- Main branch remains unchanged by the Account A/B worker changes.

## Security and QA evidence

- Main source uses Android Keystore AES-256-GCM for browser metadata, randomized 12-byte IV and 128-bit GCM tag; decryption fails closed when the key is missing or ciphertext is invalid.
- Main disables Android backup and cleartext traffic, uses a non-exported scoped FileProvider, and does not override GeckoView TLS certificate validation.
- PR #36 adds HTTPS-only site permission mediation for supported notifications/autoplay, default-deny unsupported and Android/media permission requests, notification runtime consent, and private/insecure-origin notification suppression.
- PR #36 adds private download metadata exclusion, canonical app-root restore validation, URL user-info rejection, legacy ambiguous download-index migration and tests. Downloaded file bytes remain user files and are not erased or encrypted by private mode.
- PR #36 CI run #42570822775 is the current Android CI candidate and must complete on the exact latest PR head before merge. Earlier cancelled runs do not count.
- PR #36 OSV scan run #42570823051 completed successfully but logs explicitly state “No package sources found” / “No lockfiles found”. This is not evidence that dependencies are vulnerability-free.
- PR #36 source SBOM run #42570822827 succeeded and uploaded `rivo-source-sbom` (2,940 bytes). It is source-level, not a complete resolved dependency SBOM.
- New Gradle dependency graph workflow generates resolved dependency graph artifacts for PRs and Main pushes using read-only permissions. GitHub Dependency Graph is disabled in repository settings, so automated GitHub advisory review/submission is unavailable until the owner enables it.

## Release blockers

1. PR #35 and #36 are not merged; review and exact-head CI must pass first.
2. Current PR #36 Android CI, including app lint/JVM tests, Gecko policy unit tests, API 35 Keystore/DataStore instrumentation, minified bundle, benchmarks and static APK/native ELF 16 KB checks, is not yet confirmed green on its final head.
3. OSV currently finds no package sources because Gradle lockfiles are absent. Resolved dependency graph generation improves visibility but does not replace OSV scanning against resolved SBOM/lockfiles or dependency verification.
4. Physical-device site permission grant/deny/revoke, Android notification permission/channel/delivery/click, private tab process-death and download migration tests remain outstanding.
5. No physical 3–4 GB RAM performance benchmark or real 16 KB page-size runtime test is claimed.
6. TalkBack, 320 dp layout, large font scale, contrast and all three appearance modes require device acceptance.
7. No production upload keystore/secrets are available. CI AABs are unsigned QA artifacts.
8. No independent penetration test or formal OWASP MASVS verification/certification has been completed.
9. Signed AAB verification, artifact provenance, Play Console upload validation and production release authorization remain outstanding.

## Android configuration observed in source

- applicationId: `com.rivo.browser`
- versionName: `0.2.0-phase1`; versionCode: `2`
- minSdk: 26; targetSdk: 36; compileSdk: 37.2
- intended ABIs: `arm64-v8a`, `armeabi-v7a`
- app INTERNET permission; PR #36 adds POST_NOTIFICATIONS only. Backup disabled, cleartext disabled.

This is source inspection, not a complete merged-manifest or runtime permission test.

## Secure production signing

The Gradle release build supports opt-in upload signing from environment-only values:

- `RIVO_UPLOAD_STORE_FILE`
- `RIVO_UPLOAD_STORE_PASSWORD`
- `RIVO_UPLOAD_KEY_ALIAS`
- `RIVO_UPLOAD_KEY_PASSWORD`

Only when explicitly requested with `-Privo.releaseSigningRequired=true` does the release variant use the upload signing config. Missing required values fail the build. Never commit a keystore/password or expose secrets in logs or command-line arguments. Use protected GitHub Actions environment secrets and least-privilege access.

## Release-candidate acceptance matrix

| Area | Required evidence | Current status |
|---|---|---|
| Main/PR CI | Exact-head assemble, lint, JVM, instrumentation, minified AAB | PR #33 baseline green; #35/#36 pending exact-head checks |
| Security storage | Keystore round-trip/tamper/key-loss, DataStore corruption/migration, process-death | API 35 tests exist; current PR #36 run pending; physical test not run |
| Permissions | HTTPS site prompt, deny/grant/revoke, Android notification runtime permission | Source and JVM policy tests; device not run |
| Private mode | No history/bookmark/tab/permission/download-metadata leakage; process death | Source and instrumentation coverage partial; device not run |
| Downloads | URL/path/filename/size/temp cleanup/open/share/private metadata | Source/JVM/instrumentation coverage partial; device not run |
| Network | TLS failure not bypassed, cleartext disabled, redirect and download validation | Source review only; device/network test not run |
| Themes/accessibility | Dark, Light, Glass; TalkBack, 48 dp, large font, narrow screen | Source review only; device not run |
| Performance | Cold/warm startup, PSS, CPU, thermal, ANR/crash on 3–4 GB devices | No measurements |
| 16 KB | APK ZIP and native ELF static alignment plus 16 KB runtime launch | Static checks configured; runtime not run |
| Supply chain | OSV over resolved dependencies, full SBOM, dependency verification | Source OSV currently no lockfiles; Gradle graph workflow added; full SBOM/verification incomplete |
| Signing/distribution | Signed AAB, signature/hash/provenance, Play Console validation | No production credentials or signed release artifact |

## Dependency and MASVS review

OWASP MASVS source-level mapping is recorded in `SECURITY.md`. It is a gap-tracking document, not certification. Before release, obtain a complete resolved Gradle dependency inventory/SBOM, scan it with OSV, review GeckoView native/transitive dependencies and licenses, establish dependency verification/locking, and record evidence and residual risks across MASVS-STORAGE, CRYPTO, NETWORK, PLATFORM, CODE, RESILIENCE and PRIVACY.

## Known limitations and rollback

- GeckoView engine choice remains subject to measured performance on target devices.
- Unsupported site/device permissions remain denied.
- Downloaded file contents are not encrypted by the metadata cipher; private mode does not erase user-saved downloads.
- Private browsing does not make network activity anonymous.
- No end-user WebExtensions store is implemented.
- If a candidate fails, stop promotion, retain diagnostics and restore the previous known-good signed artifact. Do not wipe user data as a rollback shortcut; validate backward-compatible migration first.

## Release decision

**NO-GO.** Do not tag, publish, announce or distribute production until all blockers close, current Main CI and device/security evidence are reviewed, production signing is configured, and the owner explicitly authorizes release.


## Latest Account B candidate checkpoint — 2026-11-09

- PR #36 latest head at this checkpoint: `4d42f47ee8ba95f14ad34e366470396bd1106ad3`; 42 commits ahead / 0 behind Main; draft and unmerged.
- OSV PR run #42571406620 completed successfully but logged no package sources and no lockfiles. This is not a clean resolved-dependency vulnerability result.
- Source SBOM run #42571406272 completed successfully and uploaded a 2,941-byte SPDX source artifact; it is not a complete transitive Gradle SBOM.
- Gradle dependency graph run #42571406249 completed successfully and uploaded a 10,829-byte resolved dependency graph artifact. GitHub Dependency Graph is disabled in repository settings, so automated Dependency Review is not available; owner action is required to enable it.
- Android CI run #42571406314 initially failed due HTTP 504 while downloading kotlin-compiler-embeddable 2.4.10 from GitHub release assets. A retry was superseded by subsequent test/documentation changes. Exact-head Android CI remains pending and must pass before merge.
- New Account B JVM regression verifies private tabs and unsafe URLs never enter process-restoration snapshots. API 35 instrumentation includes metadata migration/store-reopen tests.
- Main baseline PR #33 CI #42563889346 passed configured tests, API 35 instrumentation, benchmark packaging, minified bundle and static APK/native ELF 16 KB alignment. No physical 16 KB runtime test is claimed.
