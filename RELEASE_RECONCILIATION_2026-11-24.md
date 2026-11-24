# Rivo Browser — Release Reconciliation (2026-11-24)

## Verified repository state
- Repository: https://github.com/techwithrakibofficial/Rivo-Browser
- Main HEAD: `50af17782686bf8b3376cee8a5fed2bc6d466779`
- Latest commit: `fix(release): normalize Base64 keystore input`
- PR #74 is merged; no open pull requests were returned by the repository PR search.
- Existing tag `v0.2.0-phase1` resolves to commit `e528d16a951fc6d2a153fcb7519fb0b0e2cd8255`.
- Comparing that tag to Main shows Main is exactly one commit ahead; the only changed file is `.github/workflows/signed-release.yml` (26 additions, 3 deletions). The existing tag was not moved or rewritten.
- GitHub Releases API returned an empty list at the time of this check.

## Latest Main CI evidence
All five workflow runs associated with the latest Main push completed successfully:
- Android CI: run 45347938960
- Gradle Dependency Verification Bootstrap: run 45347938867
- Gradle Dependency Graph: run 45347938963
- OSV Scanner Scheduled: run 45347939269
- Source SBOM: run 45347938853

Android CI job details for run 45347938960: all reported steps succeeded, including checked-in dependency verification metadata, Android security configuration static audit, shipping assemble, lint, unit tests, Gecko engine policy tests, minified release bundle, static 16 KB alignment checks, and Android security instrumentation tests. QA AAB/APK outputs are not production-signed release artifacts.

## Release workflow source validation
The merged signed-release workflow now normalizes Base64 whitespace and URL-safe characters, validates the alphabet strictly, rejects empty/invalid/truncated payloads, decodes PKCS#12, and checks the configured alias using keytool without printing signing material.

This verifies the workflow source and its CI checks only. It does **not** prove that the live production secret contains a complete, valid keystore or that the configured password and alias match it.

A follow-up source review found that the cleanup step targeted an obsolete `.jks` filename while the workflow actually creates a `.p12` PKCS#12 file. PR #75 corrects the cleanup path to remove `$RUNNER_TEMP/rivo-upload.p12` even when a prior signing step fails. The updated workflow must pass the fresh PR CI before integration.

## Release status
No GitHub Release is published. No successful production-signed APK/AAB run is evidenced. Do not distribute unsigned CI artifacts as production builds.

The signed-release workflow requires a new reviewed tag at a Main-ancestral commit or a manual workflow dispatch, plus the protected production environment secrets:
- `RIVO_UPLOAD_KEYSTORE_BASE64`
- `RIVO_UPLOAD_STORE_PASSWORD`
- `RIVO_UPLOAD_KEY_ALIAS`
- `RIVO_UPLOAD_KEY_PASSWORD`

The available GitHub integration in this session supports read-only workflow-run inspection and repository file/branch/commit operations, but exposes no create-tag or workflow-dispatch operation. Secret values are intentionally inaccessible. No workaround was attempted that would rewrite the existing release tag, expose credentials, or weaken workflow protections.

## Outstanding non-CI acceptance evidence
- Physical-device QA on supported Android hardware, including 3–4 GB RAM devices.
- Accessibility checks (TalkBack, narrow screens, large text, contrast, reduced motion).
- Runtime 16 KB page-size verification on compatible hardware.
- Repeatable memory/startup/page-load/battery profiling.
- Full OWASP MASVS/MASTG assessment and dependency advisory triage.

## Next execution point
After the release tag is created through the repository's authorized release process, run the Signed Android Release workflow against that tag. Review the job result and verify APK/AAB signatures, signer identity, checksums, and published assets before marking production release complete.
