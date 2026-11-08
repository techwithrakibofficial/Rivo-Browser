# Extensions and Developer Tools Feasibility

## WebView
No native full WebExtensions runtime/extension manager. Do not label injected scripts or ad-hoc content scripts as full extensions. Chrome DevTools remote debugging can be used during development, but production exposure requires strict controls.

## GeckoView
GeckoView exposes WebExtension controller APIs; exact install sources, signing requirements, supported APIs and permission UX must be tested against the pinned stable artifact. Compatibility with all Firefox/Chrome extensions is not guaranteed.

GeckoView remote debugging is a supported developer capability. Keep disabled by default, require explicit opt-in, and prevent production network exposure.

## Acceptance
- Install/remove/enable/disable one test extension.
- Permission prompt and denied-permission tests.
- Unsupported API handling.
- Verify no silent installs.
- Verify DevTools disabled by default and no exposed debug endpoint in release builds.
