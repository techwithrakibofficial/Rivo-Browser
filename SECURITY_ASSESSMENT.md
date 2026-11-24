# Rivo Browser — OWASP MASVS Gap Assessment

Assessment date: 2026-11-10  
Repository baseline: main at 45611c3a42ae14f82089f8ba49c5840a9548a0b6  
Assessment type: preliminary source/configuration review; **not** a penetration test, independent audit, or certification.

## Standard and method

This assessment uses the current OWASP Mobile Application Security Verification Standard (MASVS) control groups and IDs. MASVS defines the security requirements; the OWASP Mobile Application Security Testing Guide (MASTG) provides test procedures and MASWE describes common weaknesses. See:

- [OWASP MASVS](https://mas.owasp.org/MASVS/)
- [OWASP MAS Testing Guide](https://mas.owasp.org/MASTG/)
- [OWASP MASWE](https://mas.owasp.org/MASWE/)

Status meanings:

- **Source evidence** — relevant implementation/configuration was found in source; runtime evidence is still required where noted.
- **Partial** — some requirements are implemented, but material gaps or verification remain.
- **Not implemented / gap** — no implementation was found in the reviewed baseline.
- **N/A (scope)** — the control does not currently apply to the product's architecture; reassess if the architecture changes.
- **Not assessed** — insufficient evidence for a defensible conclusion.

Do not interpret this document as a MASVS pass. A source review cannot establish device behavior, privacy disclosure completeness, resistance to a compromised OS, or independent security assurance.

## Control register

| Control | Preliminary status | Source evidence / reason | Required closure evidence |
|---|---|---|---|
| MASVS-STORAGE-1 | Partial | Browser metadata uses DataStore and Android Keystore AES-GCM (LocalDataCipher, BrowserStorage). Downloaded file bytes are not encrypted by that metadata cipher and reside in app-specific Downloads. | Decide/document download-data sensitivity; test backup, filesystem exposure, key loss and file lifecycle on devices. |
| MASVS-STORAGE-2 | Partial | Manifest disables Android backup; private tabs are excluded from app-side history/bookmarks/restoration and private download metadata is not persisted. | Inspect logs, crash reports, clipboard, screenshots, notifications, recent-apps snapshots and OS backup/restore behavior on supported Android versions. |
| MASVS-CRYPTO-1 | Source evidence | Metadata cipher uses AES/GCM/NoPadding, 256-bit AES keys, 96-bit IV from the provider and 128-bit GCM tag. | Validate provider behavior, corruption/tamper handling, nonce uniqueness assumptions and supported-device behavior. |
| MASVS-CRYPTO-2 | Partial | Key is generated/stored in Android Keystore and missing-key/decryption errors fail closed. Hardware-backed storage is not guaranteed; explicit key rotation/recovery lifecycle is not evidenced. | Test key invalidation, backup/restore, OS upgrade and app-data migration; document key lifecycle and recovery limitations. |
| MASVS-AUTH-1 | N/A (scope) | Rivo has no Rivo-operated account/backend authentication protocol. Website authentication is handled by the browser engine and the visited origin. | Reassess if Rivo adds account sync, cloud services or a remote API. |
| MASVS-AUTH-2 | N/A (scope) | Rivo does not currently expose app-local PIN/biometric authentication. Device lock is an OS-level user responsibility. | Reassess if app lock, protected profiles or local credentials are introduced. |
| MASVS-AUTH-3 | N/A (scope) | No app-owned sensitive operation requiring a second authentication factor was found in the reviewed product scope. | Reassess for export/sync, password vault, payment or account-management features. |
| MASVS-NETWORK-1 | Partial | GeckoView is the shipping engine; project security baseline says certificate validation is not overridden and cleartext traffic is disabled. A full traffic audit was not performed. | Test HTTP/HTTPS, redirects, mixed content, proxy/VPN, certificate failures, WebSocket and download flows on devices; inspect all native networking paths. |
| MASVS-NETWORK-2 | N/A (scope) | No Rivo-controlled remote endpoint was found. Pinning arbitrary websites would be incompatible with a general-purpose browser. | Reassess if any Rivo-operated endpoint is introduced; use pinning only for endpoints under Rivo control and with a rotation plan. |
| MASVS-PLATFORM-1 | Partial | MainActivity is the launcher; FileProvider and notification receiver are non-exported; FileProvider grants URI access explicitly. Full IPC/PendingIntent review is not complete. | Verify every PendingIntent flag/target, URI grant lifetime, intent validation, exported component and cross-app sharing behavior. |
| MASVS-PLATFORM-2 | N/A (shipping engine) | GeckoView is the shipping engine. Android WebView exists as a benchmark adapter, not the selected production browser engine. | Reassess WebView configuration if it becomes a shipping engine; continue GeckoView-specific hardening and regression tests. |
| MASVS-PLATFORM-3 | Not assessed | Source review did not establish screenshot/recents redaction, secure keyboard behavior or sensitive UI disclosure policy. | Test app-switcher snapshots, screen recording, autofill/password fields, clipboard and accessibility output; decide whether any screens require secure-window treatment. |
| MASVS-CODE-1 | Not assessed | CI configures Android API 35 instrumentation and build SDK 37.2; this alone does not prove the minimum supported OS is sufficiently current. | Record minSdk/targetSdk policy and a supported-OS security patch policy; verify at release time. |
| MASVS-CODE-2 | Not implemented / gap | No forced-update mechanism was found. The app has a signed-release workflow, but no update enforcement channel. | Decide whether forced updates are appropriate for this free, non-account browser; document risk acceptance or implement a trustworthy update mechanism. |
| MASVS-CODE-3 | Partial | Gradle lockfiles, OSV scanning, SBOM and dependency graph workflows exist. A clean scanner run is not a full component security review. | Preserve current scan evidence, triage advisories, review GeckoView/Android SDK supply chain and validate update cadence. |
| MASVS-CODE-4 | Partial | Navigation, QR, download URL/MIME/filename/path and restored-tab policies have dedicated validators and JVM tests. | Add fuzz/property tests for URI parsing, path traversal, malformed saved data and boundary conditions; test all external intents and engine callbacks. |
| MASVS-RESILIENCE-1 | Not implemented / gap | No platform-integrity/root detection control was found. This is defense-in-depth and must be threat-model justified. | Decide whether compromised-device detection is in scope; document residual risk rather than claiming protection. |
| MASVS-RESILIENCE-2 | Not assessed | No dedicated anti-tampering or runtime integrity verification was evidenced. | Review release signing provenance and threat model; implement only if justified, and test bypass resistance. |
| MASVS-RESILIENCE-3 | Partial | CI builds a minified shipping release bundle; this is not equivalent to a complete anti-static-analysis control. | Verify release R8/minification configuration and inspect shipped artifact; document that obfuscation is defense-in-depth. |
| MASVS-RESILIENCE-4 | Not implemented / gap | No anti-debugging or anti-dynamic-instrumentation controls were found. | Threat-model whether these controls are needed; avoid brittle controls that undermine user trust or accessibility. |
| MASVS-PRIVACY-1 | Partial | Site permissions are mediated and unsupported capabilities default-deny; permissions are requested at runtime. Manifest still declares location, camera and microphone for browser site capabilities. | Review each SDK's data access, permission timing, disclosure and consent; test permission denial/revocation and SDK behavior. |
| MASVS-PRIVACY-2 | Not met / limitation disclosed | A browser cannot hide the user's IP from visited sites or network operators by default. No anonymity or fingerprint-resistance guarantee is claimed. | Keep user-facing privacy limitations explicit; do not claim anonymity. Consider anti-fingerprinting only with measured compatibility/security trade-offs. |
| MASVS-PRIVACY-3 | Partial | Privacy UI documents private-mode, downloads, permissions and tracker-protection limitations. A complete data-flow/privacy-disclosure review was not performed. | Publish and validate a complete privacy notice, SDK/data inventory and network traffic disclosure before release. |
| MASVS-PRIVACY-4 | Partial | Clear Browsing Data, permission reset, private-session isolation and appearance/privacy controls exist. Normal downloaded files and bookmarks are retained by design. | Device-test deletion semantics and explain exactly which records/files are retained, removed or not erasable. |

## Priority remediation and verification plan

### P0 — required before production release

1. Complete a control-by-control manual MASTG test pass for storage, cryptography, network, platform IPC, untrusted inputs and privacy.
2. Verify private-mode isolation, download cleanup, notification/permission denial and Clear Browsing Data on physical Android devices.
3. Verify backup/restore, Keystore key loss/invalidation and metadata corruption behavior.
4. Complete privacy disclosure and third-party SDK/data-flow inventory.
5. Confirm signed-release provenance and verify signatures/checksums of actual published artifacts.
6. Close high/critical dependency advisories with documented evidence or mitigation.

### P1 — risk-based hardening

1. Decide and document forced-update policy (MASVS-CODE-2).
2. Decide whether platform integrity, anti-tampering and anti-dynamic-analysis controls are warranted for Rivo's threat model.
3. Complete UI leakage review: screenshots, recents, clipboard, autofill, TalkBack and screen recording.
4. Add fuzz/property tests for URL parsing, persisted JSON, download names and file paths.
5. Validate network security behavior across redirects, certificate errors and all engine-mediated paths.

### Device/performance release gates tracked separately

- Physical-device functional QA and TalkBack/narrow-screen/large-font checks.
- 3–4 GB RAM startup, memory and page-load profiling.
- Runtime 16 KB page-size verification on compatible hardware.

## Evidence boundary and reassessment

This register is a preliminary source-based gap analysis against the current OWASP MASVS structure. It is not an independent penetration test, legal privacy assessment, MASVS certification, or proof that controls pass. Each row must be updated only when a reproducible test, artifact, device result or approved risk acceptance is recorded. Recheck the upstream OWASP control set before each major release because MASVS is a living standard.


## Reassessment — 2026-11-23 Main revision

Reviewed Main revision `0df6760287b0de5c07d2468b247145dae9c0ea4b`. The updated source review covered GeckoView as the shipping engine, manifest/IPC, FileProvider, notification PendingIntent, navigation and download input boundaries, permission mediation, encrypted metadata, private browsing persistence, Gradle dependency controls and CI security guardrails.

No confirmed fixable high- or medium-severity vulnerability was identified in the reviewed source/configuration paths. This is a source-review result only, not evidence of runtime resistance or MASVS compliance.

New deterministic property-style regression coverage is being added for malformed omnibox values and download filenames. CI execution must be verified against the resulting PR head before marking these tests passed.

### Reassessment status by control group

| Group | Source evidence at this revision | Outstanding verification |
|---|---|---|
| MASVS-PLATFORM | Launcher-only exported activity; private FileProvider/receiver; explicit immutable notification PendingIntent; no website-callable JS bridge found | Merged release manifest, external intent/URI grant tests, notification lifecycle |
| MASVS-NETWORK | Android platform cleartext denied and system CA trust only; no custom trust-all hook found; GeckoView is independently configured | Gecko TLS errors, mixed content, redirects, proxy/VPN and HTTP download behavior |
| MASVS-STORAGE / CRYPTO | Keystore AES-GCM metadata, fail-closed corruption handling, backup disabled, private metadata excluded | Physical device key invalidation, backup/restore, filesystem/log/clipboard/recents and deletion verification |
| MASVS-CODE | Lockfiles, strict Gradle verification workflow, OSV, SBOM and dependency graph are present | Review each advisory and dependency change; verify release binary and provenance |
| MASVS-PRIVACY | Private-mode persistence policy, permission mediation, generic notification public preview | Full SDK/data-flow/privacy notice review, screenshot/recents and OEM behavior |
| MASVS-RESILIENCE | No claim of anti-tamper/root detection or enforced update | Decide only if required by threat model; avoid adding disruptive controls without a product requirement |

### Risk treatment

- Download file contents are not encrypted by the metadata cipher; app-specific storage and private-download lifecycle are the current mitigations. Treat this as a documented limitation, not a cryptographic pass.
- StrongBox/hardware-backed Keystore use is not guaranteed; hardware capability and invalidation must be tested per device.
- Android Network Security Config does not govern GeckoView NSS networking.
- No certificate pinning is recommended for arbitrary websites. Reassess only for future Rivo-owned service endpoints.
- Screenshots/recents behavior is not established by static source inspection; preserve browser usability and test actual background snapshots before choosing a blocking policy.

The exact signed release candidate, supported Android versions, and physical test devices are not available in this audit session. The dynamic MASTG test matrix remains a production release gate.


### Medium finding remediation — mixed-content status was omitted from security UI

A follow-up review of the GeckoView `SecurityInformation` callback found that the app forwarded only `isSecure` and `isException`, while GeckoView separately reports `mixedModeActive` and `mixedModePassive`. The UI could consequently use a generic secure-connection label without disclosing loaded mixed content.

**Fix in current PR:** mixed-content loaded/unknown states are now propagated to tab state and rendered as explicit warnings/qualified transport status. `BrowserSecurityStatusPolicyTest` covers certificate-exception precedence, active/passive loaded content, unknown state, ordinary secure state and insecure state. Treat this finding as remediated in source, pending PR CI and runtime GeckoView fixture verification.


## 2026-11-24 — PR #67 verification closeout (authoritative latest checkpoint)

- Audited source branch: `security/comprehensive-android-audit-2026-11-23`; verified PR head: `1cde602ae030b44c260770734205b0186deb4e28`; base Main: `0df6760287b0de5c07d2468b247145dae9c0ea4b`.
- GitHub Actions Android CI run [45242372450](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372450) completed successfully. Its build job completed all steps successfully, including strict Gradle dependency verification, static security configuration audit, shipping and benchmark builds, Android lint, JVM tests, Gecko policy tests, minified shipping release bundle, static 16 KB alignment checks, and API 35 emulator Android security instrumentation tests.
- PR-head supply-chain workflows also completed successfully: [Gradle Dependency Verification Bootstrap](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372194), [Source SBOM](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372086), [OSV Scanner](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242372048), and [Gradle Dependency Graph](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45242371982).
- The Medium mixed-content security-indicator finding is remediated in source and covered by JVM policy tests. CI success verifies the automated test path; GeckoView mixed-content fixture behavior on physical devices remains unverified.
- Correction to earlier preliminary audit wording: a follow-up review DID identify the Medium mixed-content status presentation issue; it is fixed in this PR. Do not interpret earlier “no confirmed Medium issue” wording as the final finding.
- PR #67 remains open and unmerged. The trusted reviewer `techsomadhanmedia` has write permission and is already requested as reviewer, but the PR currently has no submitted review/approval. Do not self-approve, bypass required review, or merge before independent approval and required checks.
- Scope boundary: successful emulator instrumentation is verified; physical-device MASTG/runtime tests, merged release manifest/IPC inspection, exact signed APK/AAB inspection, production signing/release, and independent penetration testing are NOT completed. This is not MASVS certification.
- Manual next gate: `techsomadhanmedia` must open PR #67, inspect the changes, and submit **Approve**. After approval, recheck all required checks and mergeability before merging. Physical-device checks must then be executed using the documented matrix before production security acceptance.


## 2026-11-24 — Authoritative post-PR #67 checkpoint
- PR #67 was independently approved by `techsomadhanmedia` and squash-merged to Main as `ecd9c45c5db53b0f9f0203c82402fe067b3e622d` on 2026-11-23 19:23:45 UTC.
- The Medium mixed-content security-indicator presentation finding is remediated in source: GeckoView active/passive mixed-mode state is propagated into tab/UI security status, unknown state is qualified, navigation resets state, and JVM regression/property tests were added. This does not prove runtime GeckoView behavior; physical fixture/device tests remain required.
- Active Main ruleset `Protect main branch` requires PR review, one approval, CODEOWNERS review, resolved threads and strict check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- Post-merge OSV Scanner [#45248503306](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503306), Dependency Graph [#45248502790](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248502790), and Source SBOM [#45248501246](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501246) succeeded. Android CI [#45248503483](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248503483) and Dependency Verification Bootstrap [#45248501576](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45248501576) were still running at the last captured check; final conclusions must be verified before updating their status.
- This is a source/configuration assessment and CI checkpoint, not a penetration test, runtime MASTG completion, independent audit or MASVS certification. Production signed artifact verification, physical-device TLS/mixed-content/private-mode/storage tests, accessibility, low-RAM profiling, runtime 16 KB verification and full control-by-control assessment remain open.


## Authoritative readiness checkpoint — 2026-11-24

- Main includes PR #68, squash-merged after CODEOWNER approval by `techsomadhanmedia`: https://github.com/techwithrakibofficial/Rivo-Browser/pull/68. Merge commit: `e44cae6fb2e9a34bcc40d6769e78626df1ea40c5`.
- Main ruleset `Protect main branch` is active. It requires pull requests, at least one approval, CODEOWNERS review, resolved review threads and strict required check `build`; deletion and non-fast-forward updates are blocked; no bypass actors are configured.
- No open pull requests were returned by the live repository query at this checkpoint.
- Post-merge Source SBOM [run 45251818989](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251818989) and OSV Scanner Scheduled [run 45251819550](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819550) completed successfully.
- Post-merge Android CI [run 45251819071](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/45251819071) was still running at this checkpoint. Security static audit, shipping debug assemble, lint, app JVM tests, Gecko policy JVM tests and engine benchmark debug APK builds had succeeded; engine benchmark release bundle build was in progress. Do not claim the full Android CI, minified bundle, static 16 KB artifact verification, QA artifact uploads or Android instrumentation passed until the run concludes.
- Owner-reported device QA: the project owner states they installed and exercised the app on their Android phone and that the phone-based checks were successful. This is owner-reported acceptance, not independently reproduced evidence. It does not by itself establish runtime 16 KB compatibility on a dedicated 16 KB device or independent penetration-test completion.
- Production signing: the owner previously reported configuring four production signing secrets. This integration cannot read secret values or verify Environment protection. No signed production APK/AAB, signer fingerprint verification, checksum publication or GitHub Release is evidenced at this checkpoint. The signed-release workflow validates signer consistency before publishing, but no authorized version/tag execution has occurred.
- Remaining gates: final post-merge Android CI conclusion; authorized version/tag and production signing/release execution; independent penetration testing and any required control-by-control MASVS/MASTG assessment. No certification is claimed.
