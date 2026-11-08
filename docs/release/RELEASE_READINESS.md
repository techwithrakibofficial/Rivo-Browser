# Release Readiness & Android QA — RIVO-09

**Status: BLOCKED — not a production release candidate.**
Audit date: 2026-11-09
Audited branch: `main`
Audited commit: `84303588c2560db55bfb7ae2081d8f714be532e7`

## Verified CI evidence

GitHub Actions run #246 on the audited Main commit completed successfully:
https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42481870810

Completed successfully:
- `:app:assembleDebug`
- `:app:lintDebug`
- `:app:testDebugUnitTest`
- `:app:bundleRelease` with R8/resource shrinking
- QA artifact upload for unsigned AAB, ARM64 debug APK and ARMv7 debug APK

Artifact archive sizes (GitHub Actions ZIP artifacts, **not** raw APK/AAB file sizes):
- ARM64 debug artifact: 109,569,398 bytes
- ARMv7 debug artifact: 105,594,221 bytes
- unsigned release AAB artifact: 248,829,993 bytes

Do not report these ZIP archive sizes as installed APK or raw AAB sizes. Download and inspect the individual files before release. CI proves build packaging for both ABI outputs, not installation or runtime support on physical devices.

Run #246 is the current Main validation evidence at audit time. Earlier Main run #244 was cancelled after the main build steps had passed; it is superseded by #246.

## Release blockers

1. Phase 1 acceptance is not complete. PR #8 (Android Keystore/storage tests), #9 (core browser hardening/downloads), #10 (privacy/per-site controls) and #6 (theme overlay integration) remain open at audit time. Do not assume their code is present in Main.
2. PR #11 engine benchmark failed CI and no physical-device benchmark result exists. Engine choice remains provisional.
3. PR #12 Advanced Tab Universe remains open and its CI validation was still running at audit time.
4. Android Keystore AES-GCM, corruption, key loss and legacy migration instrumentation tests are still outstanding.
5. No physical Android device installation, upgrade, force-stop/relaunch, migration, navigation, private-mode, download, accessibility or performance QA evidence is available.
6. No production signing key has been supplied/configured. CI AAB is explicitly unsigned QA output.
7. No dependency vulnerability scan/SBOM or completed OWASP MASVS assessment has been recorded. `SECURITY.md` explicitly says it is a baseline, not certification.
8. No raw APK/AAB size inspection, APK Signature Scheme verification, AAB signing verification, 16 KB runtime test or install-time compatibility test has been recorded.
9. No GitHub Release exists at audit time.

## Android configuration observed

- applicationId: `com.rivo.browser`
- versionName: `0.2.0-phase1`; versionCode: `2`
- minSdk: 26
- targetSdk: 36
- compileSdk: 37.2
- configured ABI splits: `arm64-v8a`, `armeabi-v7a`
- manifest permission: `android.permission.INTERNET` only
- `allowBackup=false`; `usesCleartextTraffic=false`
- no broad storage, camera, microphone or location permission declared in the main app manifest.

This is a source inspection, not a full merged-manifest or runtime permission test. Review merged manifests for every release variant and transitive SDK before publishing.

## Secure production signing

The app Gradle configuration supports environment-only upload signing. No keystore or password is stored in source control.

Configure protected GitHub Actions Environment secrets (or secure local environment variables):
- `RIVO_UPLOAD_STORE_FILE`: path to the upload keystore available to the runner
- `RIVO_UPLOAD_STORE_PASSWORD`
- `RIVO_UPLOAD_KEY_ALIAS`
- `RIVO_UPLOAD_KEY_PASSWORD`

Production signing must be explicitly required:
```bash
./gradlew -Privo.releaseSigningRequired=true :app:bundleRelease
```

If signing is required but any environment value is missing, Gradle fails configuration. Without the explicit property, the existing CI release task remains unsigned for QA. Do not put secret values in command-line arguments, Gradle files, logs, repository variables, or committed `local.properties`. Use a protected environment, least-privilege access, limited reviewers, secret masking and a backup/recovery process. Keep the upload key separate from the Play app-signing key if Play App Signing is used. Validate the resulting signature with `jarsigner -verify -verbose -certs` for APKs and `bundletool validate` plus the Play upload flow for AABs.

## Required QA matrix before RC

| Test | Required evidence | Status |
|---|---|---|
| Fresh install | Install, launch, first-run and permissions on physical ARM64 and ARMv7 devices | Not run |
| Upgrade | Install prior signed build, upgrade without uninstall, preserve expected data | Not run |
| Migration | Legacy plaintext encrypted metadata migration and rollback-safe behavior | Not run |
| Relaunch | Force-stop/reboot; normal tabs restore, private tabs do not | Not run |
| Navigation | HTTPS/HTTP, search, back/forward/reload, redirects, certificate errors | Not run |
| Private mode | No app-side history/bookmark/tab persistence; explain network visibility | Not run |
| Downloads | Confirm/cancel, failures, path traversal, duplicate names, open/share, size limit | Not run |
| Themes | Dark, Light, Premium Glass across every dialog/panel/system bar | Not run |
| Accessibility | TalkBack labels/states, focus order, 48dp targets, font scale, contrast | Not run |
| Performance | Cold/warm startup, PSS, CPU, thermal, ANR/crash, low-memory behavior | Not run |
| Security | Keystore instrumentation, dependency scan, MASVS evidence, merged manifest | Not run |
| ABI | Install and launch each supported ABI artifact on matching devices | Not run |

## Dependency and MASVS review

A complete vulnerability assessment has **not** been performed. Pinning dependencies is not proof that dependencies are vulnerability-free. Before release:
- Generate Gradle dependency reports for every runtime configuration.
- Run dependency vulnerability scanning (for example, OSV-Scanner or OWASP Dependency-Check) and review advisories against exact resolved versions.
- Produce an SBOM (CycloneDX or SPDX) and retain it with the release evidence.
- Review GeckoView native components, transitive dependencies, licenses and update policy.
- Complete a documented OWASP MASVS checklist covering STORAGE, CRYPTO, NETWORK, PLATFORM, CODE, RESILIENCE and PRIVACY. Record evidence and residual risks; do not claim certification.

## Known issues / rollback

- Site permission grant/revoke management remains incomplete; unsupported permission requests should remain denied.
- GeckoView versus WebView selection is not supported by physical-device benchmark data.
- Theme overlay and narrow-screen/accessibility issues remain documented in the development plan.
- Downloaded files are user data in app-specific storage and are not encrypted by the metadata cipher.
- Private mode does not make network activity anonymous.
- If a release candidate fails: stop promotion, revoke/discontinue the candidate track, retain crash/log evidence, restore the previous known-good signed artifact, and avoid uninstall/data wipe as a rollback method. Database/DataStore schema changes require a tested backward-compatible migration or a documented data-preserving recovery plan. Never downgrade over user data without validating migration compatibility.

## Release decision

**NO-GO.** Do not tag, publish, announce, or distribute a production release. A release candidate may be prepared only after blockers close, secure signing is configured, all required physical-device tests pass, security/dependency evidence is reviewed, and the owner explicitly authorizes RC creation.
