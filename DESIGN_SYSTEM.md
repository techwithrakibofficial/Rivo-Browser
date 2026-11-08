# Design System

## Appearance modes
1. Dark — graphite/near-black semantic surfaces.
2. Light — clean neutral surfaces with accessible contrast.
3. Premium Glass — translucent layered surfaces with restrained gradients and blur.

## Central tokens
Color roles, backgrounds, surfaces, typography, iconography, borders, elevation, shadows, blur tiers, spacing, corner radii, motion and animation durations.

## Rules
- All app surfaces consume semantic tokens; no per-screen hardcoded theme palettes.
- Theme choice persists locally and applies immediately.
- Quick Appearance Picker lives in a secondary control, not as a permanent main-toolbar icon.
- Glass has solid/low-blur fallback for low-end devices and reduced-effects preferences.
- Website content styling remains separate from browser chrome.
- Validate contrast, font scaling, touch targets, TalkBack semantics and reduced motion.
