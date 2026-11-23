# GitHub Governance & Release Security Audit

Updated: 2026-11-23  
Audited revision: `cda27e5bf019fbf50be47678c53bf1dcc2e54d0e`  
Hardening branch: `security/governance-hardening-2026-11-23`

## Executive result

The repository now has an active main-branch ruleset. The previous documentation claiming that no active protection existed is superseded by the live ruleset inspection below. The ruleset is a useful baseline, but it does **not** currently require a human review. Production release remains blocked until the production Environment is independently verified and protected and a real signed release is tested.

## Main branch ruleset — live API evidence

Ruleset: [Protect main branch](https://github.com/techwithrakibofficial/Rivo-Browser/rules/27894079) (ID `27894079`)  
Target: `refs/heads/main`  
Enforcement: `active`  
Bypass actors: none; current integration reports bypass as `never`.

Enabled rules:
- Require a pull request before merging.
- Require the status check `build` and require the branch to be up to date.
- Block branch deletion.
- Block non-fast-forward updates (force pushes).
- Require review-thread conversation resolution.

Important missing/weak settings:
- Required approving reviews: **0**.
- Dismiss stale reviews on new pushes: **false**.
- Code-owner review: **false**.
- Required reviewer list: empty.

The `build` required context matches the Android CI job/check name observed on Main. The classic branch-protection endpoint reports protection disabled, while the active repository ruleset independently applies to Main; do not confuse those two mechanisms.

## Actions and workflow review

### Fixed on this hardening branch

1. `.github/workflows/gradle-verification-bootstrap.yml`: removed the privileged `commit_pull_request` job. The PR-triggered Gradle build executes PR-controlled build logic and must remain read-only. It may upload generated verification metadata/lockfiles for review, but must not hand artifacts to a second job with `contents: write` and push credentials. The Main push proposal flow remains separate.
2. `.github/workflows/signed-release.yml`: publishing now fails if the release tag already has a GitHub Release. The prior `gh release upload --clobber` path could mutate an existing published release; this is replaced with create-once behavior. The workflow still verifies downloaded assets against SHA-256 after publication.

### Remaining workflow policy work

- Current workflow references use version tags (for example `actions/checkout@v7`, `actions/setup-java@v6`, `gradle/actions/setup-gradle@v6`, `google/osv-scanner-action@v2.7.1`, `anchore/sbom-action@v0.24.3`). Pin each third-party action to a verified full-length commit SHA, or adopt GitHub Actions dependency lockfiles after validating support for the repository's action set. Do not invent or copy unverified SHAs.
- Enable repository Actions policy to require full-length SHA pinning only after every workflow and reusable action has been migrated and tested. Restrict allowed actions to the minimum trusted set.
- Keep workflow defaults read-only and grant write/security-event permissions only to the exact jobs that need them. Do not introduce `pull_request_target` for executing PR code.
- Protect workflow/security configuration changes with CODEOWNERS plus an enforced code-owner review rule.

## Production Environment — not verified

The workflow references an Environment named `production` and reads signing values from that Environment. The connected GitHub integration cannot read Environment protection settings or secret metadata, so the following are **unverified**, not assumed configured:
- At least one trusted required reviewer; prevent self-review where available.
- Deployment branch/tag policy restricted to reviewed Main-ancestral release tags.
- All four signing values exist as Environment secrets on `production`, not repository-level secrets:
  - `RIVO_UPLOAD_KEYSTORE_BASE64`
  - `RIVO_UPLOAD_STORE_PASSWORD`
  - `RIVO_UPLOAD_KEY_ALIAS`
  - `RIVO_UPLOAD_KEY_PASSWORD`
- No signing credential is stored in source, workflow YAML, artifacts, or logs.

Never paste signing secrets into chat or commit them.

## Release status and evidence

- No published GitHub Release was returned by the repository Releases API at audit time.
- The signed-release workflow exists and includes owner gating, the `production` Environment, read-only build permissions, a separate publishing job, signature/fingerprint checks, checksum generation and post-upload checksum verification.
- No production-signed workflow run or signed APK/AAB was evidenced in this audit.
- CI success is not a substitute for a production signing run, manual device QA, or security certification.

## Integration limitations / owner-only settings

The connected GitHub integration supports repository and workflow reads and repository file/branch writes, but does not expose the administration mutation endpoints needed to change rulesets, classic branch protection, Actions repository policy, or Environment protection. These settings were not changed by API in this session. The owner must complete the explicitly listed GitHub Settings items.

## Acceptance checklist

- [x] Active Main ruleset verified; bypass list empty.
- [x] Required check `build` matches an observed Android CI check.
- [x] Force-push and deletion protections verified in the ruleset.
- [x] PR-controlled dependency bootstrap no longer receives a write-back job.
- [x] Existing release assets cannot be overwritten by this workflow.
- [ ] Require at least one trusted PR approval; dismiss stale approvals.
- [ ] Enable code-owner review and protect `.github/workflows/**`.
- [ ] Verify production Environment reviewers and deployment branch restrictions.
- [ ] Verify four signing secrets are Environment-scoped (names only; never expose values).
- [ ] Migrate workflow actions to verified immutable SHAs or validated dependency lockfile.
- [ ] Run hardening PR CI and inspect all checks.
- [ ] Execute a production-signed release and verify signatures/checksums.
