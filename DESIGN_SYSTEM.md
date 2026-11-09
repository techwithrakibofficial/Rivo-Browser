# Rivo Browser Design System

**Status:** Phase 1 browser-chrome baseline; implementation exists in Main source, with device/UI acceptance still pending.  
**Scope:** Native Android/Compose browser UI. Website-rendered content is controlled by the visited site and is not recolored by Rivo's chrome theme.

## Product principles
- Premium, calm, distinctive browser chrome with predictable navigation.
- Consistent Dark, Light and Premium Glass appearance modes across every native surface, including dialogs, prompts, menus and empty/error states.
- Accessible by default: semantic controls, TalkBack names, scalable text, clear focus, and WCAG AA text contrast.
- Low-RAM friendly: avoid continuous blur, animated backgrounds and unnecessary recomposition.
- Never communicate state by color alone; pair color with text, iconography or semantics.

## Implemented foundations
- `RivoColorSchemes.forAppearance`: Material 3 semantic palettes for Dark, Light and Glass.
- `RivoSpacing`: 4, 8, 12, 18 and 24 dp spacing scale.
- `RivoShapes`: 18 dp controls, 20 dp cards and 24 dp panels.
- Browser chrome consumes Material 3 semantic colors; web content remains site-controlled.
- Quick Appearance Picker is reached from the secondary overflow menu.
- Status/navigation system-bar icon brightness follows the selected appearance.
- Premium Glass uses translucent surfaces, not real-time blur.

## Theme contract
The selected `MaterialTheme` must wrap the complete native screen subtree: browser chrome, panels, permission prompts, download confirmations and notice dialogs. Verify every overlay inherits the selected theme in all three appearance modes.

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
- Body copy and important controls must remain readable at system font scale.
- Interactive targets should be at least 48 × 48 dp.
- Icon-only actions need localized TalkBack labels and state descriptions.
- Text fields need persistent labels or equivalent semantics, not placeholders alone.
- Selected appearance and active-tab states must be exposed semantically.
- Security and private-browsing states require explicit text labels.

## Layout and performance
- Preserve the 4 dp spacing rhythm and shared shape tokens.
- Adapt secondary toolbar actions for narrow screens; do not squeeze labels.
- Avoid fixed-height panels; use available-window constraints and internal scrolling.
- Respect reduced-motion settings for any future transitions.
- Avoid real-time blur, animated gradients and continuously moving effects on low-RAM devices.

## Current UI inventory
- Initial tab navigates to DuckDuckGo; no dedicated native start-page dashboard is confirmed.
- Omnibox and Go action are implemented; accessibility and large-font verification remain.
- Advanced Tab Universe is implemented in Main through PR #23: searchable title/URL list, Normal/Private/Active filters, sorting, active state, and tab close/switch. Narrow-width filter scrolling and stronger TalkBack semantics are in a follow-up Core branch, not yet merged.
- Three appearance modes and secondary picker exist; overlay and selected-state verification remain.
- Private-tab status label exists.
- Bookmarks, history and downloads native panels exist; device theme/scroll verification remains.
- Privacy Intelligence Center and Transfer Studio are not confirmed in Main; privacy center is in open PR #21 stacked on #10.
- Reader mode, QR and advanced tab-management screens are not confirmed in Main.

## Verification checkpoint
Main HEAD after PR #23 merge: `d192baf0f38db835904aa76a901d7e7cb2a24e31`. PR #23 head passed CI run [#293](https://github.com/techwithrakibofficial/Rivo-Browser/actions/runs/42559942026), including assemble, lint, JVM tests and minified release bundle. This does not establish rendered contrast, TalkBack, touch bounds or responsive behavior; manual UI/device acceptance remains open.

## Release acceptance checklist
- [ ] Selected appearance is applied to every native surface and overlay.
- [ ] Text contrast meets WCAG AA in all three modes.
- [ ] TalkBack announces useful names, roles, values and selected states.
- [ ] Touch targets are at least 48 × 48 dp and usable at 320 dp width.
- [ ] System font scaling and long labels do not clip or overlap.
- [ ] Dialogs/panels scroll within the available window on small screens.
- [ ] Reduced-motion settings are respected by any future animation.
- [ ] Glass remains legible with transparency disabled and no GPU-heavy blur.
- [ ] Manual TalkBack, Accessibility Scanner, contrast and device screenshot checks are recorded.
