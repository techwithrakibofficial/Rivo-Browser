# Rivo Browser — Master Specification

## Product
Free, privacy-focused Android browser for low- and mid-range smartphones (3–4 GB RAM target).

## Priorities
Security, privacy, performance, accessibility, usability, maintainability and zero mandatory paid infrastructure.

## Authorized phase
Phase 0 foundation and engine research completed as a source-code checkpoint; physical-device benchmark remains outstanding.
**Phase 1 is now authorized (2026-11-08):** implement a functional browser MVP. Phase 2 and Phase 3 remain out of scope until separately approved.

## Phase 1 MVP scope
- Browser navigation, omnibox with URL/search interpretation, back/forward/reload/stop.
- Multiple tabs with create, switch and close; inactive sessions should be set inactive to reduce resource use.
- Private tabs using GeckoView's native private-session setting; do not persist private history/bookmarks or imply protection against network/ISP observation.
- Local bookmarks and browsing history for regular tabs.
- Safe downloads using Android DownloadManager with safe filename handling and explicit user-visible download status.
- Dark, Light and Premium Glass browser chrome themes using centralized semantic tokens; theme preference stored locally.
- Basic site security indicator based on engine-provided security information, not URL-string guesses.
- Conservative site permission handling; no permission is granted silently.

## Engine strategy
GeckoView Stable is the Phase 1 implementation candidate because GeckoSession supports private sessions, browser navigation delegates, tracking protection and per-session lifecycle. This is a provisional implementation choice, not a final benchmark verdict. Keep the engine boundary modular and retain the WebView comparison adapter until physical-device evidence is reviewed. Do not ship claims about unsupported extensions or devtools.

## Appearance
One centralized design system with Dark, Light and Premium Glass modes. Glass effects have low-cost fallbacks and respect accessibility/reduced-motion. Quick Appearance Picker lives in a secondary control, not a permanent main-toolbar icon.

## Product integrity
No simulated features, fabricated privacy counters, unsupported extension claims, or claims of anonymity/absolute protection.

## Release
GitHub Releases first. Google Play and F-Droid readiness assessed separately.
