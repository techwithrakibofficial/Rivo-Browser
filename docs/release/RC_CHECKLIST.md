# Release Candidate Checklist

A release candidate is not a public release. This checklist must be completed against one exact Main commit.

- [ ] Phase 1 acceptance criteria closed and prerequisite PRs merged.
- [ ] Current Main CI passes assembleDebug, lint, unit/instrumentation tests and minified bundleRelease.
- [ ] Dependency vulnerability scan and SBOM reviewed; no unresolved critical/high issue without documented security-owner acceptance.
- [ ] OWASP MASVS checklist completed with evidence and residual risks.
- [ ] Merged manifests and target SDK reviewed.
- [ ] Production upload signing secrets configured in protected environment; no key/password in repository.
- [ ] Signed AAB/APKs verified; artifact hashes and provenance recorded.
- [ ] ARM64 and ARMv7 install/launch verified on physical devices.
- [ ] Fresh install, upgrade, migration and relaunch tests pass.
- [ ] Navigation, private mode, downloads and all three themes pass.
- [ ] TalkBack, touch targets, contrast, large font and small screen QA pass.
- [ ] Performance, stability, crash/ANR and 16 KB page-size checks completed where applicable.
- [ ] Known issues and rollback artifact/recovery plan approved.
- [ ] Owner explicitly authorizes creating/tagging the release candidate.

Never publish or announce a production release without explicit authorization.
