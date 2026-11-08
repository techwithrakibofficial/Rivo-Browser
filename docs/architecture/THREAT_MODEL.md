# Phase 0 Threat Model

| Threat | Control direction |
|---|---|
| Malicious web content | Engine process isolation; no unsafe native bridge |
| TLS interception | Platform certificate validation; no trust-all callbacks |
| Session leakage | Separate normal/private session data and cleanup |
| Malicious extension | Signed source validation, permission disclosure and user consent |
| Dangerous downloads | MIME/path validation, safe destination and explicit open action |
| Local data extraction | Android sandbox, Keystore-backed key protection where needed |
| Debug endpoint abuse | Disabled by default; developer opt-in; no production exposure |
| Supply-chain compromise | Pinned dependencies, CI checks, provenance and review |
| Sensitive logs | Redaction; no URLs with secrets, cookies or credentials |

This is an initial threat model, not a completed penetration test or MASVS verification.
