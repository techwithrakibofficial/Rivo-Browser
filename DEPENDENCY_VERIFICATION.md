# Gradle Dependency Verification

## Policy
- Android CI runs Gradle in strict dependency-verification mode.
- The committed metadata records SHA-256 checksums for resolved artifacts and metadata.
- PGP signature verification is intentionally disabled in this baseline; no unverified keys are trusted.
- No missing-key exceptions, ignored artifacts or verification exceptions are allowed.

## Bootstrap and provenance
Metadata is generated using the repository's configured Google Maven, Maven Central and Mozilla repositories. The bootstrap resolves the shipping debug build, lint, JVM tests, Gecko policy tests, GeckoView/WebView benchmark builds and minified shipping release bundle, then repeats the task set in strict mode.

The initial checksum set is a trust-on-first-use record of artifacts served by those configured repositories at bootstrap time. It is not independent upstream signature attestation. Review every new or changed checksum when dependencies or repositories change. Keep Gradle lockfiles, OSV scanning and the source SBOM enabled as separate controls.

## Updating metadata
1. Update dependency declarations and lockfiles intentionally.
2. Run the Gradle Dependency Verification Bootstrap workflow on Main.
3. Review the generated metadata and component/artifact changes.
4. Merge the metadata PR only after dependency changes and repository provenance are understood.
5. Confirm Android CI passes with strict verification enabled.

Do not add PGP keys, ignored keys or verification exceptions without independently verifying fingerprints and documenting the reason.
