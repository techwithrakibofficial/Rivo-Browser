# Security Baseline

Use OWASP MASVS as the baseline for storage, cryptography, authentication, network, platform, code, resilience and privacy.

## Phase 0 controls
- No JavaScript-to-native bridge in the WebView probe.
- Explicit WebView lifecycle disposal.
- No certificate-validation override.
- No secrets or personal data in repository.
- CI token permissions limited to contents: read.
- Dependencies pinned; no dynamic version selectors.

## Later requirements
HTTPS-first/HTTPS-only policy, safe permission mediation, private-session separation, secure downloads, Keystore-backed secrets, log redaction, dependency scanning and release signing through GitHub Secrets.

This file is a baseline plan, not a completed security audit.
