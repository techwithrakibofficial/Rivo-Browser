# Architecture

## Proposed layers
- app: Android entry point and composition root.
- engine-api: engine-neutral contracts.
- engine-webview: WebView evaluation adapter.
- engine-gecko: planned after exact stable artifact/API verification.
- domain: browser use cases (later phase).
- data: persistence (later phase).
- core/designsystem: centralized tokens/components (later phase).

## Lifecycle rules
Engine/session lifecycle must not be owned by Compose recomposition. Engine objects are created and disposed by lifecycle-aware owners. A future GeckoView implementation should use one shared GeckoRuntime and a GeckoSession per tab, subject to measured memory behavior.

## Phase 0 skeleton
The current app is only a foundation status screen. It is not a usable browser and does not expose navigation controls.
