# Release Readiness & Android QA — RIVO-09

**Decision: NO-GO for production distribution.**
Audit date: 2026-11-09
Canonical repository: https://github.com/techwithrakibofficial/Rivo-Browser
Main HEAD after the latest confirmed merges: `9633351069a1c59d8bd184d4ad6f2e2603e0addb`.

## Integrated and branch-level evidence

- PR #5 documentation checkpoint synchronization: merged.
- PR #6 centralized theme overlays, contrast and UI accessibility improvements: merged as `9633351069a1c59d8bd184d4ad6f2e2603e0addb`; its latest branch CI run #249 passed.
- PR #7 adaptive browser panel list sizing: CI run #250 passed; branch is based on the earlier Main checkpoint and currently has a merge conflict.
- PR #8 Android Keystore/DataStore instrumentation: CI run #252 has passed assemble, lint, JVM tests and unsigned AAB/artifact steps; API 35 emulator instrumentation was still running at this audit checkpoint. Do not claim instrumentation passed until the run concludes successfully.
- PR #9 core navigation/history/tab/download hardening: CI run #254 passed all configured build, lint, JVM test, release bundle and QA artifact steps; branch is based on the earlier Main checkpoint and requires conflict resolution before integration.
- PR #10 privacy and per-site permission controls: CI run #268 passed configured build, lint, JVM test, release bundle and QA artifact steps. Runtime permission prompts/notifications/site permission persistence still need device verification.
- PR #11 matched GeckoView/WebView benchmark harness: initial CI run #272 failed at benchmark release AAB packaging because ABI splits produced multiple shrunk-resource files. A code/workflow correction is being prepared; no benchmark result or engine winner is claimed.
- PR #12 Advanced Tab Universe first increment: CI run #273 passed configured checks. This covers open-tab search/filter/sort only, not the complete Phase 2 feature set.
- PR #13 release readiness/signing: earlier CI run #275 passed, but follow-up signing/changelog corrections require a fresh run before merge.

CI is commit-specific. A passing run on an earlier branch SHA does not validate later commits or the integrated Main HEAD.

## What CI does and does not prove

The Android workflow covers debug assembly, Android lint, JVM unit tests, minified release AAB packaging and unsigned QA artifacts. PR #8 adds API 35 emulator instrumentation. These checks do not replace physical-device install, upgrade, force-stop/relaunch, TalkBack, notification UX, hardware-backed Keystore validation or 3–4 GB RAM profiling.

The CI AAB is unsigned QA output unless the explicit production signing property and protected signing environment are configured. No production signing credentials are available in this project environment.

## Release blockers

1. Phase 1 work remains partly unmerged: #7, #8, #9, #10, #11, #12 and #13 are open; #7 and #9 require conflict resolution.
2. PR #8 emulator instrumentation must finish green; representative hardware-backed Keystore and secure-lock-screen key invalidation remain physical-device checks.
3. PR #11 benchmark bundle workflow must pass, followed by actual GeckoView/WebView runs on representative 3–4 GB devices. Do not choose an engine based only on CI build success.
4. No physical-device install/upgrade/migration/relaunch, browsing, private-mode, download, notification, permission, accessibility or performance QA evidence has been recorded.
5. No production upload keystore/secrets have been supplied. The Gradle signing configuration is opt-in and fails closed when required secrets are incomplete.
6. Dependency vulnerability review, SBOM generation, GeckoView license/transitive dependency review and documented OWASP MASVS assessment remain incomplete.
7. Merged release manifest, APK/AAB signature, raw artifact sizes, 16 KB runtime compatibility and Play upload validation remain outstanding.
8. No production GitHub Release has been published.

## Android configuration observed in the Phase 1 source

- applicationId: `com.rivo.browser`
- versionName: `0.2.0-phase1`; versionCode: `2`
- minSdk: 26; targetSdk: 36; compileSdk: 37.2
- intended ABIs: `arm64-v8a`, `armeabi-v7a`
- app manifest declares INTERNET only; backup disabled and cleartext traffic disabled.

This is source-level inspection, not a complete merged-manifest or runtime permission test.

## Secure production signing

The Gradle release build supports opt-in upload signing from environment-only values:

- `RIVO_UPLOAD_STORE_FILE`
- `RIVO_UPLOAD_STORE_PASSWORD`
- `RIVO_UPLOAD_KEY_ALIAS`
- `RIVO_UPLOAD_KEY_PASSWORD`

Only when explicitly requested with `-Privo.releaseSigningRequired=true` does the release variant use the upload signing config. If any required value is missing, Gradle fails. Ordinary CI remains unsigned QA packaging. Never commit a keystore/password or expose secrets in logs or command-line arguments. Use protected GitHub Actions environment secrets and least-privilege access.

## Required release-candidate verification matrix

| Area | Required evidence | Status |
|---|---|---|
| CI | Current Main build, lint, JVM and instrumentation suites, minified AAB | Partly complete; PR validation ongoing |
| Fresh install | Physical ARM64 and ARMv7 install/launch | Not run |
| Upgrade/migration | Upgrade without uninstall; preserve and migrate user data | Not run |
| Relaunch | Force-stop/reboot; normal tabs restore and private tabs do not | Not run |
| Navigation | HTTPS/HTTP, search, redirects, back/forward/reload, certificate errors | Not run on device |
| Private mode | App-side persistence isolation and clear network privacy disclosure | Source-level only |
| Downloads | Confirm/cancel, path traversal, collisions, failure cleanup, open/share, cap | JVM/instrumentation coverage partial; device not run |
| Site permissions | Allow/deny, Android notification permission, revocation and private-mode behavior | JVM policy/CI coverage; device not run |
| Themes/accessibility | Dark, Light, Glass overlays; TalkBack, focus, 48dp targets, font scale | Source/previews and CI; device not run |
| Performance | Cold/warm startup, PSS, CPU, thermal, ANR/crash on 3–4 GB devices | Harness under repair; no measurements |
| Security | Keystore device behavior, dependency scan/SBOM, MASVS evidence, merged manifest | Incomplete |
| ABI/16 KB | Physical install and runtime validation on supported ABIs/page sizes | Static workflow pending |

## Dependency and MASVS review

A complete vulnerability assessment has not been performed. Before release, generate resolved Gradle dependency reports, run an advisory scanner such as OSV-Scanner, produce a CycloneDX/SPDX SBOM, review GeckoView native/transitive dependencies and licenses, and document evidence/residual risks against OWASP MASVS STORAGE, CRYPTO, NETWORK, PLATFORM, CODE, RESILIENCE and PRIVACY. Do not claim MASVS certification without an actual assessment.

## Known limitations and rollback

- GeckoView remains provisional until measured against WebView on target devices.
- Site-permission UX intentionally defaults unsupported capabilities to deny.
- Downloaded file contents are not encrypted by the metadata cipher; private mode does not erase user-saved downloads.
- Private browsing does not make network activity anonymous.
- If a candidate fails, stop promotion, retain diagnostics and restore the previous known-good signed artifact. Do not uninstall or wipe user data as a rollback shortcut. Validate backward-compatible data migration before downgrade.

## Release decision

**NO-GO.** Do not tag, publish, announce or distribute a production release until all blockers close, current Main CI passes, physical-device/security evidence is reviewed, signing is configured and the owner explicitly authorizes the release.
