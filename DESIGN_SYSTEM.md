# Rivo Browser — Design System

Updated: 2026-11-09

**Scope:** Native Android/Compose browser chrome. Website-rendered content remains controlled by the visited site.

## Product principles
- Premium, calm and predictable browser chrome.
- Consistent Dark, Light and Premium Glass appearance across the browser, panels, dialogs, permission prompts, Reader Mode and Transfer Studio.
- Accessible by default: semantic controls, TalkBack names, scalable text, visible focus and WCAG AA contrast.
- Low-RAM friendly: no continuous blur, animated backgrounds or unnecessary recomposition.
- Never communicate security/private state by color alone.

## Centralized tokens
- `RivoColorSchemes.forAppearance`: Material 3 semantic palettes for Dark, Light and Glass.
- `RivoSpacing`: 4, 8, 12, 18 and 24 dp spacing scale.
- `RivoShapes`: 18 dp controls, 20 dp cards and 24 dp panels.
- Browser chrome consumes semantic Material 3 colors; website content remains site-controlled.
- Quick Appearance Picker is reached through a secondary overflow control.
- System-bar icon brightness follows the selected appearance.
- Premium Glass uses translucent surfaces, not real-time blur.

## Theme contract
The selected MaterialTheme wraps the complete native screen subtree: browser chrome, panels, permission prompts, download confirmations, Reader Mode, Transfer Studio and notice dialogs. Every overlay must inherit the active appearance.

## Appearance specifications
### Dark
- Background: `#0D1118`; surface: `#171E28`.
- Primary: `#9CCBFF`; secondary: `#B8CDE2`.
- Foreground: `#E8EEF7`; error: `#FFB4AB`.

### Light
- Background: `#F3F6FA`; surface: `#FFFFFF`.
- Primary: `#315B85`; secondary: `#4D7192`.
- Foreground: `#172333`; error: `#BA1A1A`.
- Secondary `#4D7192` is approximately 5.13:1 against white.

### Premium Glass
- Background: `#101827`; surface token: `#CC26354B` (about 80% opaque).
- Primary: `#B7D5FF`; secondary: `#C4D8F0`; foreground: `#F3F7FF`.
- Use restrained translucent layers over a stable dark base; no real-time blur.
- Provide an opaque fallback when transparency is unsupported, noisy or expensive.

## Typography and accessibility
- Use Material 3 typography roles consistently; do not clamp user text scaling.
- Body copy and important controls remain readable at system font scale.
- Interactive targets should be at least 48 × 48 dp.
- Icon-only actions require localized TalkBack labels and state descriptions.
- Text fields require persistent labels or equivalent semantics.
- Active-tab, selected-filter, private-mode and security states must be exposed semantically.
- Long article text, transfer status and permission details must remain readable at large font scale.

## Layout and performance
- Preserve the 4 dp spacing rhythm and shared shape tokens.
- Adapt secondary toolbar actions for narrow screens; do not squeeze labels.
- Use available-window constraints and internal scrolling rather than fixed-height panels.
- Respect reduced-motion settings for future transitions.
- Avoid real-time blur, animated gradients and continuously moving effects on low-RAM devices.

## Current native surface inventory
- Browser chrome, omnibox and navigation controls — Main.
- Advanced Tab Universe — Main; search, filters, sorting, active state and accessible close/switch actions.
- Bookmarks, history and downloads panels — Main.
- Dark, Light and Glass palettes and secondary appearance picker — Main.
- Reader Mode overlay — PR #37 integration candidate.
- QR result confirmation dialog — PR #37 integration candidate.
- Transfer Studio — PR #37 integration candidate; search, filters, progress and explicit file actions.
- Developer Mode diagnostics — PR #37 integration candidate; read-only.
- Privacy Intelligence Center and site permission prompts — PR #37 integration candidate.
- Clear Browsing Data confirmation — PR #37 integration candidate.

## Manual acceptance checklist
- [ ] All native surfaces inherit the selected appearance.
- [ ] Text contrast meets WCAG AA in all three modes.
- [ ] TalkBack announces useful names, roles, values and selected states.
- [ ] Touch targets are at least 48 × 48 dp and usable at 320 dp width.
- [ ] System font scaling and long labels do not clip or overlap.
- [ ] Dialogs/panels scroll within the available window on small screens.
- [ ] Reduced-motion preferences are respected.
- [ ] Glass remains legible with transparency disabled and without GPU-heavy blur.
- [ ] Manual TalkBack, contrast and device screenshot checks are recorded.
