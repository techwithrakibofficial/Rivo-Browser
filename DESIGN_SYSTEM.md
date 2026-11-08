# Rivo Browser Design System

**Status:** Phase 1 browser-chrome baseline; RIVO-02 theme-scope implementation in progress (2026-11-08).  
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

**The selected `MaterialTheme` must wrap the complete native screen subtree.** That includes the browser chrome, tab/bookmark/history/download/appearance panels, permission prompts, download confirmations and notice dialogs. A dialog composed outside the selected theme silently falls back to the ambient/default Material theme and breaks three-mode consistency.

RIVO-02 moves the panel dialog and all browser `AlertDialog` overlays inside the same selected `MaterialTheme` scope as the main `Surface`. This prevents dialogs from falling back to the ambient/default Material theme.

## Appearance specifications

### Dark

- Background: `#0D1118`; surface: `#171E28`.
- Primary: `#9CCBFF`; secondary: `#B8CDE2`.
- Foreground: `#E8EEF7`; error: `#FFB4AB`.
- Use deep graphite surfaces and light foregrounds. An OLED-black variant is not currently implemented; do not advertise it as an available setting.

### Light

- Background: `#F3F6FA`; surface: `#FFFFFF`.
- Primary: `#315B85`; secondary: `#4D7192` (updated to exceed 4.5:1 against white).
- Foreground: `#172333`; error: `#BA1A1A`.
- Keep surfaces neutral and controls clearly separated.
- **Contrast:** secondary `#4D7192` is approximately 5.13:1 against white and clears the 4.5:1 WCAG AA normal-text threshold.

### Premium Glass

- Background: `#101827`; surface token: `#CC26354B` (about 80% opaque).
- Primary: `#B7D5FF`; secondary: `#C4D8F0`; foreground: `#F3F7FF`.
- Use restrained translucent layers over a stable dark base. Do not require blur or gradients for legibility.
- Provide an opaque dark-surface fallback when transparency is unsupported, visually noisy or expensive.
- Glass must not reduce text contrast or obscure focus, selection or security states.

## Typography

- Use Material 3 typography roles consistently; do not assign arbitrary sizes screen by screen.
- Body copy and important controls should remain comfortably readable at the user's system font scale.
- Reserve `labelSmall` / compact metadata for secondary information, not essential warnings, permission decisions or security explanations.
- Do not clamp user text scaling. Check long translated labels and large font settings for clipping and overlap.
- A dedicated Rivo typography token set is not yet implemented; current Compose UI relies on Material 3 defaults.

## Layout, touch and navigation

- Preserve the 4 dp spacing rhythm and use shared shape tokens.
- Interactive targets should be at least 48 × 48 dp, with adequate separation.
- Keep primary navigation discoverable and stable. On narrow phones, adapt or horizontally scroll secondary toolbar actions rather than squeezing labels.
- Avoid fixed-height content panels where font scaling or a small display can clip content. Prefer available-window constraints with internal scrolling.
- Every icon-only action needs a localized TalkBack label and a meaningful state description.
- Text fields need a persistent label or equivalent semantics, not only a placeholder.
- Selected appearance and active-tab states must be exposed semantically, not only by a checkmark or color.
- Security and private-browsing states must retain explicit text labels; color is supplemental.

## Motion and performance

- No decorative motion is currently used in browser chrome. This naturally avoids introducing motion for users who prefer reduced motion.
- If transitions are added, keep them short and functional, respect Android animation/reduced-motion settings, and never animate essential status information.
- Do not add real-time blur, animated gradients or continuously moving glass effects to the low-RAM MVP.
- Prefer stable alpha surfaces and a solid-color fallback. Profile on representative 3–4 GB devices before increasing effects.

## Current UI inventory

- Browser home: the initial tab navigates to DuckDuckGo; there is no dedicated native Rivo start-page/home dashboard yet.
- Omnibox and Go action: implemented; accessibility label/large-font review remains.
- Tab interface: a basic “Your tabs” list dialog exists; a visual Tab Universe/grid is not implemented.
- Appearance: three modes and a secondary-menu picker exist; selected-state semantics and overlay theme scope need correction.
- Private browsing: an explicit private-tab status label exists; the tab list prefixes private tabs.
- Bookmarks, history and downloads: implemented as native panels with empty states; dialogs need theme-scope and responsive-height fixes.
- Loading: a linear page progress indicator and initial “Starting Rivo Browser…” state exist.
- Errors and permissions: native AlertDialogs provide messages and Allow/Deny decisions; they currently sit outside the selected theme scope.
- Privacy Intelligence Center and Transfer Studio: not implemented; listed as later-phase features, not audited as existing screens.
- Dedicated settings, reader mode, QR and advanced tab-management screens: not present in the current Phase 1 UI.

## Release acceptance checklist

- [ ] Selected appearance is applied to every native surface and overlay.
- [ ] Text contrast meets WCAG AA (4.5:1 normal, 3:1 large) in all three modes; non-text controls/focus indicators meet 3:1 where applicable.
- [ ] TalkBack announces useful names, roles, values and selected states for every interactive control.
- [ ] All touch targets are at least 48 × 48 dp and remain usable at 320 dp width.
- [ ] System font scaling and long labels do not clip or overlap.
- [ ] Dialogs/panels scroll within the available window on small screens.
- [ ] Reduced-motion settings are respected by any future animation.
- [ ] Glass remains legible with transparency disabled and does not require GPU-heavy blur.
- [ ] Manual TalkBack, Accessibility Scanner, contrast and device screenshot checks are recorded before claiming compliance.
