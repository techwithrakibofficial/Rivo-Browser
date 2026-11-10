# Rivo Browser — Design System

Updated: 2026-11-10

## Appearance modes
- Dark, Light and Premium Glass are supported through one centralized semantic color/shape/spacing system.
- All browser panels and dialogs inherit the active MaterialTheme.
- Quick Appearance Picker remains a secondary overflow/menu control; no permanent theme icon is placed on the main browser display.

## Premium Glass constraints
- Use translucent semantic surfaces, restrained borders and layered elevation.
- Do not use expensive real-time blur or GPU-heavy effects on low-/mid-range devices.
- Glass must remain readable when transparency is reduced or disabled.
- Preserve sufficient text/icon contrast in all three modes.

## Interaction and accessibility
- Prefer Material 3 components and semantic labels/state descriptions.
- Maintain clear focus order, large touch targets and TalkBack names for tab close, filters and controls.
- Respect system font scaling and reduced-motion preferences.
- Validate 320 dp widths and large fonts; horizontal filter rows should remain operable.

## Feature surfaces
- Reader Mode, Transfer Studio, Privacy Intelligence Center, permission prompts and dialogs use semantic theme tokens.
- QR results require an explicit confirmation surface.
- Developer Mode remains read-only and visually distinguished from normal browsing controls.

## Acceptance checklist
- [ ] Dark, Light and Glass screenshots on supported Android versions.
- [ ] Contrast and icon legibility in all modes.
- [ ] TalkBack and keyboard/focus order.
- [ ] 320 dp width and large font scaling.
- [ ] Reduced motion and transparency-disabled behavior.
- [ ] 3–4 GB device performance without sustained blur/render overhead.
