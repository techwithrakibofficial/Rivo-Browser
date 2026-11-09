# Rivo Browser — Release Candidate Checklist

A release candidate is not a public release. Complete this checklist against one exact Main commit and attach evidence.

## Integration and CI
- [ ] Account A PR #35 reviewed, merged, and exact-head CI green.
- [ ] Account B PR #36 reviewed, merged, and exact-head CI green.
- [ ] Current Main Android CI passes assemble, lint, app JVM tests, Gecko engine policy unit tests, API 35 instrumentation, benchmark packaging, minified release AAB and static 16 KB APK/ELF alignment.
- [ ] No unresolved critical/high vulnerability without documented security-owner acceptance.

## Security and privacy
- [ ] Keystore AES-256-GCM round-trip, tampering, missing-key/key-loss, DataStore corruption and migration tests pass on CI/device.
- [ ] HTTPS site permission grant/deny/revoke and Android 13+ notification runtime permission/channel/delivery/click tested on device.
- [ ] Private notifications are denied; private history/bookmark/tab restore/permission/download metadata do not leak across process death.
- [ ] Download URL, filename, path traversal, size cap, temp cleanup, restore, FileProvider open/share and private metadata behavior verified.
- [ ] TLS certificate failures are not bypassed; cleartext remains disabled; redirects and download URL validation tested.
- [ ] Resolved Gradle dependency graph and complete SBOM reviewed; OSV scan covers resolved dependencies; dependency lock/verification strategy recorded.
- [ ] OWASP MASVS control map has evidence and residual risks reviewed. Do not claim certification without independent verification.

## Device and accessibility QA
- [ ] ARM64 and ARMv7 install/launch tested where supported.
- [ ] Fresh install, upgrade, data migration, force-stop/relaunch and process-death tests pass.
- [ ] Navigation, private mode, downloads, site permissions and all three themes pass.
- [ ] TalkBack, focus order, touch targets, contrast, large font and 320 dp layouts pass.
- [ ] Performance, stability, crash/ANR and memory profiling completed on representative 3–4 GB devices.
- [ ] 16 KB page-size environment confirmed with `adb shell getconf PAGE_SIZE` returning `16384`; APK ZIP and ELF alignment verified; app launched and exercised with 16 KB compatibility mode disabled.

## Signing and distribution
- [ ] Production upload signing secrets configured in protected environment; no key/password in repository.
- [ ] Signed AAB/APKs verified; artifact SHA-256 and provenance recorded.
- [ ] Play Console pre-launch report and target API/permission disclosures reviewed.
- [ ] Known issues and rollback/recovery plan approved.
- [ ] Owner explicitly authorizes creating/tagging the release candidate.

Never publish or announce a production release without explicit authorization. CI-generated unsigned QA AABs are not production release artifacts.
