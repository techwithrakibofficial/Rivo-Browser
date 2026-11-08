# Rivo Browser Design System

## Principles
- Premium, calm, distinctive browser chrome.
- Consistent Dark, Light and Premium Glass appearance modes.
- Legible content, accessible contrast and predictable controls.
- Low-RAM and reduced-motion friendly; avoid expensive real-time blur.

## Centralized tokens in Phase 1
- RivoColorSchemes: semantic Material 3 color schemes for Dark, Light and Glass.
- RivoSpacing: compact, small, medium, large and extra-large spacing scale.
- RivoShapes: shared control/card/panel corner radii.
- Browser chrome consumes MaterialTheme semantic colors rather than styling website content.

## Appearance modes
### Dark
Deep neutral surfaces, light foreground text and blue-tinted accents.

### Light
Soft neutral background, white surfaces, dark text and blue accents.

### Premium Glass
Translucent dark surfaces and layered contrast. Real-time blur is intentionally avoided; this keeps rendering and memory costs predictable on 3–4 GB devices.

## Interaction
- Quick Appearance Picker lives in the secondary overflow menu.
- No permanent theme icon on the main browser toolbar.
- Address bar and dialogs use shared rounded shapes.
- Status-bar and navigation-bar icon contrast follows selected appearance.
- No decorative motion is currently used; reduced-motion preference is therefore respected by default.

## Future design work
- Expand semantic typography/elevation tokens.
- Add component previews and screenshot tests.
- Run TalkBack, touch-target and contrast audits on device.
- Add glass surface variants only after performance profiling.
