# Rivo Browser Design System

**Status:** RIVO-03 responsive/accessibility source refinements (2026-11-09); device verification pending.  
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
- The selected `MaterialTheme` scopes the browser surface and all panel/alert overlays (inherited from RIVO-02).
- Five equally weighted Material 3 icon buttons replace the crowded bottom text actions; each action exposes TalkBack text and the tabs action reports the open-tab count.
- The omnibox has a persistent label; menu, tab-close and bookmark-removal actions expose contextual semantics.
- Appearance options use selectable radio semantics. All modal lists and long alert text scroll within bounds derived from available display height.

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
- **Source-level contrast regression:** secondary `#4D7192` is approximately 5.13:1 against white. `RivoColorSchemesTest` checks primary, secondary, on-surface and error palette pairs for Light, Dark and composited Glass surfaces; rendered controls/states still need manual contrast verification.

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
- Keep primary navigation discoverable and stable. The bottom toolbar uses five equally weighted icon buttons rather than compressing text labels; each uses Material 3's minimum interactive sizing. Verify rendered bounds on 320 dp screens with increased font scale.
- Avoid fixed-height content panels where font scaling or a small display can clip content. Prefer available-window constraints with internal scrolling.
- Every icon-only action needs a localized TalkBack label and a meaningful state description. Bottom controls and secondary menu now expose explicit semantics; English strings should move to localized resources as localization is added.
- Text fields need a persistent label or equivalent semantics, not only a placeholder. The omnibox now uses a persistent label.
- Selected appearance and active-tab states must be exposed semantically, not only by a checkmark or color. Appearance choices are selectable radio rows; the tabs action announces the current count.
- Security and private-browsing states must retain explicit text labels; color is supplemental.

## Motion and performance

- No decorative motion is currently used in browser chrome. This naturally avoids introducing motion for users who prefer reduced motion.
- If transitions are added, keep them short and functional, respect Android animation/reduced-motion settings, and never animate essential status information.
- Do not add real-time blur, animated gradients or continuously moving glass effects to the low-RAM MVP.
- Prefer stable alpha surfaces and a solid-color fallback. Profile on representative 3–4 GB devices before increasing effects.

## Current UI inventory

- Browser home: the initial tab navigates to DuckDuckGo; there is no dedicated native Rivo start-page/home dashboard yet.
- Omnibox and Go action: implemented with persistent label; keyboard/IME and TalkBack behavior still require device verification.
- Tab interface: a basic “Your tabs” list dialog exists; a visual Tab Universe/grid is not implemented.
- Appearance: three modes and a secondary-menu picker exist; selected radio semantics are added in RIVO-03 and theme scope is inherited from RIVO-02.
- Private browsing: an explicit private-tab status label exists; the tab list prefixes private tabs.
- Bookmarks, history and downloads: native panels; lists are scrollable and constrained to 88% of the dialog's available height.
- Loading: a linear page progress indicator and initial “Starting Rivo Browser…” state exist.
- Errors and permissions: native AlertDialogs provide messages and Allow/Deny decisions inside the selected theme scope; long text is bounded and scrollable.
- Privacy Intelligence Center and Transfer Studio: not implemented; listed as later-phase features, not audited as existing screens.
- Dedicated settings, reader mode, QR and advanced tab-management screens: not present in the current Phase 1 UI.

## Release acceptance checklist

- [x] Selected appearance is applied to every native surface and overlay at source level; rendered visual verification in all three modes remains pending.
- [ ] Rendered text contrast meets WCAG AA (4.5:1 normal, 3:1 large) in all three modes. JVM tests cover semantic palette token pairs only; actual UI states, large text, Glass layers and non-text controls require manual verification.
- [ ] Manual TalkBack pass confirms accessible names, roles, values, focus order and selected states; source semantics were added for toolbar/menu/tab close/bookmark removal/appearance.
- [ ] Rendered touch bounds are at least 48 × 48 dp and remain usable at 320 dp width; bottom controls use Material 3 IconButton minimum sizing but require device measurement.
- [ ] System font scaling (including 2.0) and long labels do not clip or overlap; verify on short displays.
- [ ] Dialogs/panels scroll within the available window on small screens; source uses an 88% dialog-height cap and scrollable list/text content, but device verification is pending.
- [ ] Reduced-motion settings are respected by any future animation.
- [ ] Glass remains legible with transparency disabled and does not require GPU-heavy blur.
- [ ] Manual TalkBack, Accessibility Scanner, contrast and device screenshot checks are recorded before claiming compliance.
