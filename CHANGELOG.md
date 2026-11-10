# Changelog

Notable changes to Rivo Browser are recorded here.

## Unreleased — integration PR #37 (2026-11-09)
- Added GeckoView page-extraction Reader Mode with gated-page/readability checks, bounded in-memory article content and adjustable text size.
- Added QR-only Google Code Scanner flow with payload sanitation and explicit confirmation before navigation.
- Added Transfer Studio search, filters, bounded recent list, progress reporting, best-effort cancellation and managed file actions.
- Added read-only Developer Mode diagnostics without exposing remote debugging, console output or about:config.
- Added secure-origin site permission mediation, default-deny unsupported capabilities and Privacy Intelligence Center.
- Added HTTPS-only website notifications with private-mode denial and Android notification permission mediation.
- Added encrypted Android permission-denial state, private download metadata exclusion and private download cleanup.
- Added Clear Browsing Data for Gecko site data/settings and local history/download metadata.
- Added Gradle dependency lockfiles and resolved dependency graph workflow support.
- Fixed GeckoView builder API usage, nullable Reader Mode extraction results and ML Kit manifest metadata placement.
- Integrated progress/cancellation and private-download lifecycle handling without overwriting either feature branch.

## 2026-11-09 — Main baseline
- Integrated Advanced Tab Universe search/filter regression coverage.
- Preserved GeckoView session architecture, local storage, encrypted metadata, safe downloads and three appearance modes.
