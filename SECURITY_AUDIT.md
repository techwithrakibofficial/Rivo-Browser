# Rivo Browser — OWASP MASVS / MASTG Static Security Audit

**Audit date:** 2026-11-17  
**Repository:** `techwithrakibofficial/Rivo-Browser`  
**Baseline reviewed:** `d139aa9c4a4da29a2dc1956dec8b007908ce0c01`  
**Audit scope:** Android application source, manifests, GeckoView integration, WebView benchmark/probe, local storage, network configuration, FileProvider and notification IPC.  
**Method:** Static source/configuration review mapped to current OWASP MASVS control groups and relevant MASTG knowledge/test procedures. This is not a penetration test, runtime test, independent certification, or claim of full MASVS compliance.

## Executive summary

The production browser engine is **GeckoView**. Android System WebView is present only in the engine benchmark and comparison probe modules; it is not the shipping browser engine.

No confirmed high-severity or medium-severity exploitable vulnerability was identified in the reviewed source paths. One medium-risk privacy-hardening issue was identified and fixed: GeckoView diagnostic logging was not explicitly disabled even though web-console output was disabled. GeckoView documents diagnostic logging as enabled by default. The runtime now explicitly disables both debug logging and `about:config`, in addition to existing remote debugging and console-output restrictions.

Additional defense-in-depth was added through an explicit Android Network Security Configuration that denies cleartext traffic and trusts only system CAs for Android platform networking. The existing manifest-level cleartext prohibition remains enabled.

**Important engine boundary:** Android Network Security Configuration governs Android platform networking; it must not be represented as controlling GeckoView's independent networking stack. GeckoView's own certificate validation and mixed-content policy must be verified through engine-specific tests. No custom certificate bypass or JavaScript-to-native bridge was found in shipping source.

## Changes made in this audit

1. **Gecko diagnostics hardening** — `GeckoRuntimeSettings.Builder` now explicitly calls `.debugLogging(false)` and `.aboutConfigEnabled(false)`; existing `.remoteDebuggingEnabled(false)` and `.consoleOutput(false)` remain.
2. **Android network policy** — added `app/src/main/res/xml/network_security_config.xml` with `base-config cleartextTrafficPermitted="false"`, system-only trust anchors, and no debug trust overrides.
3. **Manifest enforcement** — `app/src/main/AndroidManifest.xml` now references the network security configuration. `android:usesCleartextTraffic="false"` and `android:allowBackup="false"` remain.
4. **Regression guard** — added `tools/verify_security_static.py` and wired it into Android CI. It checks manifest/network policy, exported component constraints, JavaScript bridge absence in shipping source, Gecko diagnostic settings, and the WebView benchmark/probe security settings.

## Detailed findings and evidence

### 1. WebView / browser engine boundary — MASVS-PLATFORM-2

**Status: Source review passed for identified controls; dynamic MASTG verification pending.**

- GeckoView is the production renderer, confirmed by the app dependency on `:engine-gecko` and runtime/session construction in `GeckoBrowserTab.kt`.
- `:engine-webview` is a comparison probe, not the selected production engine.
- The WebView benchmark and probe explicitly set `MIXED_CONTENT_NEVER_ALLOW`, disable file access and content access, and do not install a JavaScript interface.
- The production GeckoView source does not instantiate Android `WebView` or use `addJavascriptInterface`.
- JavaScript is enabled in Gecko sessions as expected for a general-purpose browser. No JavaScript-to-native bridge is exposed by the reviewed shipping source.
- Gecko remote debugging, web-console output, and Gecko diagnostic logging are explicitly disabled. `about:config` is explicitly disabled.

**Residual verification:** Use a test page containing HTTPS top-level content with HTTP scripts, stylesheets, images, frames, and redirects. Verify active mixed content is blocked, record passive mixed-content behavior, inspect `GeckoSession.ProgressDelegate.SecurityInformation.mixedModeActive` and `mixedModePassive` where available, and verify no certificate warning can be bypassed silently. Test current GeckoView 159 on supported Android versions.

### 2. Mixed content — MASVS-NETWORK-1 / MASVS-PLATFORM-2

**Status: No application override found; engine runtime test pending.**

- No production code sets a permissive mixed-content mode.
- The WebView benchmark/probe use `WebSettings.MIXED_CONTENT_NEVER_ALLOW`.
- GeckoView is the shipping engine. The reviewed GeckoView public `GeckoSessionSettings` API does not expose a WebView-style mixed-content mode property; do not add an unsupported WebView setting to Gecko code.
- GeckoView security callbacks expose active/passive mixed-mode status. Rivo currently receives secure/exception booleans but does not persist the mixed-mode detail as an independent UI state.
- Android Network Security Configuration is not a substitute for Gecko mixed-content enforcement.

**Required MASTG test:** Verify active mixed content is blocked by the Gecko engine's shipped defaults and confirm passive mixed content behavior explicitly. Treat unknown mixed-mode status as unknown, not as proof that the page is fully secure.

### 3. JavaScript interfaces / native bridge — MASVS-PLATFORM-2

**Status: Static source check passed.**

- No `addJavascriptInterface` call was found in the shipping `app/src/main` or `engine-gecko/src/main` Kotlin source reviewed.
- No custom Gecko JavaScript bridge or website-callable native capability was identified in the reviewed browser session implementation.
- WebView benchmark/probe also contain no JavaScript bridge.
- Page extraction uses GeckoView's page-extraction API; the code comments and implementation indicate it does not inject JavaScript into page content.

**Residual verification:** Repeat a whole-repository source/binary scan on release artifacts and verify no transitive/custom extension or debug-only bridge is added in future builds. Test hostile pages attempting common bridge names and confirm no native capability is reachable.

### 4. Data storage and private browsing — MASVS-STORAGE-1/2, MASVS-CRYPTO-1/2

**Status: Source evidence passed for metadata encryption and backup controls; device verification pending.**

- Browser bookmarks, history, restorable tabs, download metadata, and saved Android permission-denial metadata use `LocalDataCipher`.
- The cipher uses Android Keystore AES-256-GCM with a 96-bit IV and 128-bit authentication tag. Encrypted-data integrity/decryption failures are propagated; callers do not silently replace unreadable records with empty collections.
- The app manifest sets `android:allowBackup="false"`.
- Private tabs are excluded from normal history and restorable-tab persistence by the controller/storage policies.
- Private download metadata is excluded from persistence. Private download files are isolated in a dedicated app-specific directory and are deleted when the private tab closes or the controller is disposed.
- Normal downloaded file bytes are **not encrypted by LocalDataCipher**. They are written to app-specific external-files Downloads (or internal app files fallback). This is a disclosed limitation, not a claim of encrypted file contents.
- Android Keystore hardware-backed/StrongBox storage is not guaranteed by source configuration.

**Residual verification:** On physical devices, test backup/restore and device transfer behavior, Keystore key invalidation and app-data restore, ciphertext tampering, process death during private downloads, tab-close cleanup, app termination, and file deletion. Inspect logs, crash output, clipboard, notifications, recents snapshots and screenshots for browsing data leakage.

### 5. Network security configuration and TLS — MASVS-NETWORK-1/2

**Status: Android platform configuration source check passed; GeckoView runtime network verification pending.**

- Manifest-level `android:usesCleartextTraffic="false"` is present.
- A referenced Network Security Configuration now explicitly denies cleartext for Android platform networking, limits trust anchors to preinstalled system certificates, and contains no debug trust overrides.
- No custom trust-all `X509TrustManager`, permissive `HostnameVerifier`, or `onReceivedSslError` override was found in shipping source.
- No certificate pinning is configured. Rivo is a general-purpose browser and does not have a fixed set of visited-site endpoints to pin. Do not pin arbitrary websites.
- Android Network Security Configuration does not guarantee enforcement inside GeckoView's NSS-based networking stack.

**Residual verification:** Test GeckoView with invalid, expired, self-signed, hostname-mismatched and revoked certificates where practical; test redirects from HTTPS to HTTP, HTTP downloads, HSTS, WebSocket, proxy/VPN, and captive portal scenarios. Verify that users see a clear security state and that no invalid certificate is silently accepted.

### 6. Exported components, IPC and FileProvider — MASVS-PLATFORM-1

**Status: Manifest source check passed.**

- `MainActivity` is exported only as the Android launcher entry point. It has MAIN/LAUNCHER intent filters and no BROWSABLE/deep-link filter.
- `FileProvider` is `exported=false` and uses explicit URI grant permissions. Configured roots are limited to app-specific Downloads and internal downloads.
- `RivoWebNotificationReceiver` is `exported=false`.
- Website notification PendingIntent uses an explicit receiver class and `FLAG_IMMUTABLE`; notification visibility is private with a generic public version.
- No exported service or additional exported provider/receiver was identified in the shipping app manifest.
- The benchmark module has a separate launcher activity for the benchmark application; it is not part of the shipping browser manifest.

**Residual verification:** Inspect the merged release manifest, not only source manifests. Exercise FileProvider URI grants, verify read-only access and revocation/lifetime, test malformed incoming intents, and review all future PendingIntent creation sites.

## Automated static regression checks

The new `tools/verify_security_static.py` is executed by Android CI. It fails if:
- backup or cleartext protections are removed;
- the explicit network security configuration is missing or becomes permissive;
- non-launcher components become exported;
- shipping source introduces a JavaScript interface or custom TLS override;
- Gecko debug/remote-debug/console/about:config restrictions are removed;
- WebView benchmark/probe mixed-content or local-file protections are weakened.

This is a targeted regression guard. It does not replace CodeQL, dependency vulnerability scanning, binary analysis, runtime testing, or a penetration test.

## MASVS / MASTG coverage summary

| Area | Static result | Runtime / manual status |
|---|---|---|
| MASVS-PLATFORM-1 — IPC/exported components | Source/manifest checks passed | Merged-manifest and URI-grant tests pending |
| MASVS-PLATFORM-2 — WebView/browser renderer | No shipping WebView or JS bridge; benchmark hardened | Hostile-page and mixed-content tests pending |
| MASVS-NETWORK-1 — TLS/cleartext | Android NSC hardened; no custom trust bypass found | GeckoView TLS/certificate tests pending |
| MASVS-NETWORK-2 — identity pinning | N/A for arbitrary visited sites; no Rivo-owned remote API identified | Reassess if Rivo-owned endpoints are added |
| MASVS-STORAGE-1/2 | Metadata encrypted; backup disabled; private data excluded from metadata | Device/forensic leakage tests pending |
| MASVS-CRYPTO-1/2 | Keystore AES-GCM source evidence | Key invalidation, tamper and hardware-backed tests pending |
| MASVS-CODE-3 | Existing lockfiles, OSV, SBOM and dependency-graph workflows | Advisory triage remains continuous |
| MASVS-PRIVACY | Private mode and permission policies present | Full data-flow, disclosure and OEM behavior review pending |

## Manual MASTG verification plan

Run against the exact signed release candidate and record build SHA, device model, Android API, GeckoView version, test steps and evidence.

1. **WebView/renderer:** hostile HTML/JS, bridge-name probing, local-file access attempts, open redirects, content URI access, popup/window creation.
2. **Mixed content:** HTTPS page with HTTP script, stylesheet, iframe, image, audio/video, redirect and fetch/XHR. Verify active content is blocked; document passive behavior.
3. **TLS:** expired, untrusted, hostname mismatch and user-installed CA scenarios; verify correct browser security indicator and no silent bypass.
4. **Storage:** inspect app sandbox, DataStore ciphertext, logs, crash output, backups, recents snapshots, screenshots, clipboard and notification surfaces.
5. **Private mode:** verify no history/bookmark/tab metadata persistence; terminate process and close tab during a private download; verify file cleanup.
6. **IPC:** inspect merged manifest; test explicit external launch, malformed intents, FileProvider URI grants, share/open flows and PendingIntent mutability.
7. **Permissions:** deny/revoke camera, microphone, location and notification permissions; verify all requests are origin-scoped and denied by default when unsupported.
8. **Supply chain:** inspect final APK/AAB, dependency advisories, SBOM, signatures and provenance.

## Known limitations and release blockers

- No physical-device MASTG execution or independent penetration test was performed in this static audit.
- GeckoView mixed-content and certificate behavior requires runtime verification against the exact shipped GeckoView build.
- Normal download bytes are not application-encrypted; app-specific storage and private-download lifecycle controls are used instead.
- Android Keystore hardware backing is device-dependent.
- Android Network Security Configuration does not control GeckoView's independent network stack.
- This report does not certify MASVS compliance and does not claim every MASVS control is applicable or passed.
- Production signing, merged-manifest inspection, device forensic checks, and full privacy/data-flow review remain release gates.

## References

- OWASP MASVS: https://mas.owasp.org/MASVS/
- OWASP MASTG: https://mas.owasp.org/MASTG/
- MASTG WebViews knowledge: https://mas.owasp.org/MASTG/knowledge/android/MASVS-PLATFORM/MASTG-KNOW-0018/
- Android Network Security Configuration: https://developer.android.com/privacy-and-security/security-config
- Android security checklist (WebView/JavaScript interface guidance): https://developer.android.com/privacy-and-security/security-tips
- Android unsafe WebView file inclusion: https://developer.android.com/privacy-and-security/risks/webview-unsafe-file-inclusion
- GeckoSessionSettings API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoSessionSettings.html
- GeckoRuntimeSettings.Builder API: https://mozilla.github.io/geckoview/javadoc/mozilla-central/org/mozilla/geckoview/GeckoRuntimeSettings.Builder.html


## Comprehensive source audit update — 2026-11-23

**Audited revision:** `0df6760287b0de5c07d2468b247145dae9c0ea4b` (Main at audit start)  
**Audit type:** repository source/configuration review plus automated CI checks. This is not a device penetration test.

### Additional review performed

The complete tracked Android source/test/configuration path inventory was enumerated. Targeted review covered the app manifest and FileProvider roots, GeckoView runtime/session delegates, permission mediation, notification IPC, navigation input normalization, download URL/MIME/path handling, encrypted DataStore records, private-session persistence, browsing-data clearing, Gradle configuration, dependency lock/verification files, CI security guardrails, and existing JVM/instrumentation tests.

Observed source controls:
- GeckoView is the shipping engine; Android WebView is limited to benchmark/probe modules. No production JavaScript bridge API or custom TLS trust override was found in shipping Kotlin source.
- Omnibox navigation accepts only HTTP(S) URLs with a host and no user-info; unsupported schemes and malformed inputs become an encoded DuckDuckGo search.
- Download acceptance requires HTTPS, a host and no URL user-info. MIME types are normalized, filenames are reduced to a bounded safe basename, downloads are size-limited and staged before finalization, and restored files are checked against canonical app-managed roots and a size cap.
- Website permission requests are mediated by Gecko delegates and the app UI; unsupported permission classes are denied. Camera/microphone/location require secure-origin checks and Android runtime permission where applicable.
- Private sessions use Gecko private mode; app-side history/restorable-tab and download-metadata persistence excludes private sessions. Private download files are isolated and cleanup is attempted on tab close/controller disposal/startup.
- DataStore browser metadata is encrypted with an Android Keystore AES-GCM key. Authentication/decryption errors fail closed rather than being silently converted to empty records.
- The app manifest disables backup and cleartext Android platform networking; Network Security Config restricts Android platform trust anchors to system certificates. This does not configure GeckoView's independent network stack.
- FileProvider and the notification receiver are non-exported; the notification PendingIntent is explicit and immutable. Notification public preview uses generic text.

### Findings and disposition

| Severity | Finding | Disposition |
|---|---|---|
| High | No confirmed exploitable high-severity source finding in reviewed paths | None confirmed; runtime testing remains necessary |
| Medium | No confirmed exploitable medium-severity source finding in reviewed paths | None confirmed; runtime testing remains necessary |
| Low / residual | Normal download bytes are not encrypted by the metadata cipher | Disclosed design limitation; stored in app-specific storage; decide product encryption requirement separately |
| Low / residual | GeckoView mixed-content/TLS behavior, Android merged manifest, permission UI, recents/screenshots, backup/restore and private-download cleanup need runtime/device evidence | Open manual release gates; no static claim substitutes for runtime proof |
| Low / residual | Keystore hardware backing and key invalidation/recovery behavior are device-dependent | Open device verification; no StrongBox guarantee claimed |
| Low / residual | No full third-party SDK/data-flow/privacy disclosure review or release-binary inspection was executed | Open release gate |

No control was weakened to satisfy tests. No unsupported GeckoView mixed-content override, arbitrary-site certificate pinning, or JavaScript bridge was introduced.

### Regression test addition

Added `SecurityInputPropertyTest.kt`: deterministic property-style hostile-input loops exercise 512 generated plus seeded malformed/hostile omnibox inputs and 512 generated plus seeded download filenames. Assertions require HTTP(S)-only resolved navigation with a host and no credentials; sanitized download names must not contain path separators, exceed the filename bound, become dot path components, or collide with the interrupted-download cleanup namespace. CI execution is pending until this revision's Android CI run completes.

### Evidence and limitations

- The repository's Android CI workflow runs the security static guard, Gradle dependency verification in strict mode, Android lint, app and Gecko JVM tests, shipping/benchmark builds, static 16 KB checks, and API 35 emulator instrumentation tests.
- Current Main Android CI was still in progress when this update was authored; do not describe it as passed until its run concludes successfully.
- OSV Scanner Scheduled, Source SBOM and Gradle Dependency Graph runs on the audited Main SHA completed successfully. Their successful workflow conclusions do not prove absence of every vulnerability or complete advisory triage.
- No physical device, production-signed release artifact, merged-release-manifest inspection, live network interception, rooted-device/forensic environment, or independent penetration test was available in this audit session.
- MASTG dynamic tests for Gecko TLS/certificate failures, active/passive mixed content, redirect/proxy behavior, IPC/URI grants, private-mode lifecycle, screenshots/recents, Keystore invalidation and data leakage remain open.

### Current conclusion

This is a **static source/configuration audit with regression automation**, not a completed end-to-end penetration test. No confirmed fixable high or medium issue was identified in the reviewed source paths. Production security acceptance remains conditional on successful CI plus the listed device/runtime/manual checks.


### Additional finding discovered and remediated — mixed-content security indicator

**Severity: Medium (security-state presentation / user-decision risk). Status: fixed in this PR; CI verification pending.**

The initial source review found that the address-area security message used GeckoView's `isSecure` and certificate-exception booleans only. GeckoView separately exposes `mixedModeActive` and `mixedModePassive`; the UI did not surface those fields. A page with loaded mixed content could therefore be presented with the generic "Secure connection" label even though page subresources weakened its security.

**Remediation:** The Gecko progress delegate now forwards active/passive mixed-content loaded states and an explicit unknown-state flag into per-tab state. The browser security label now:
- prioritizes certificate exceptions;
- warns when active mixed content was loaded;
- discloses loaded insecure passive content;
- says "Secure transport · mixed-content status unknown" rather than overclaiming when either mixed-content field is unknown;
- retains "Secure connection" only when the transport is secure, there is no certificate exception, neither mixed-content field reports loaded content, and status is not unknown.

Added `BrowserSecurityStatusPolicyTest.kt` for each state and precedence. This is a UI/state-policy regression test; it does not establish Gecko's runtime classification of particular mixed-content fixtures. Dynamic mixed-content tests remain required.
