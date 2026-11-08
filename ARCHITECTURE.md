# Architecture

## Layers
- app: Android entry point, Phase 0 comparison screen and composition root.
- engine-api: engine-neutral evaluation contract.
- engine-webview: Android System WebView probe adapter.
- engine-gecko: Mozilla GeckoView probe adapter.
- domain: browser use cases (later phase).
- data: persistence (later phase).
- core/designsystem: centralized tokens/components (later phase).

## Engine lifecycle
- Compose must not create/destroy engine objects on every recomposition.
- The Phase 0 screen creates one probe with `remember` and disposes it with `DisposableEffect`.
- WebView probe owns and destroys its WebView.
- GeckoView runtime is process-scoped; the flavor factory reuses one GeckoRuntime. Each probe owns one GeckoSession and closes it on disposal. Disposing a session must not shut down the shared runtime.
- Future tab architecture must measure active/inactive session memory before selecting retention policies.

## Phase 0 comparison
The app has separate `webview` and `gecko` product flavors. Each flavor packages only its matching engine adapter and loads the same deterministic local HTML data URL. This is a comparison harness, not a user-ready browser and not a final engine decision.

## Future architecture constraints
- Keep engine-specific APIs behind adapters.
- Keep private-session storage and permissions separate from normal browsing.
- Centralize theme tokens and components across Dark, Light and Premium Glass modes.
- Avoid mandatory backend services and unnecessary background processes.
