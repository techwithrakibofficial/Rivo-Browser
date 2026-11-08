# Appearance Modes

All native browser chrome must use the centralized semantic palettes in `RivoColorSchemes`. The selected appearance must cover the complete native Compose subtree, including modal panels, permission prompts, download confirmations and notice dialogs. Web-page content remains controlled by the visited website.

## Dark

- Graphite background and surfaces, light text and blue-tinted accents.
- Current source palette: background `#0D1118`, surface `#171E28`, foreground `#E8EEF7`.
- An OLED-black variant is not currently implemented.

## Light

- Neutral light background, white surfaces, dark foreground text and blue accents.
- Current source palette: background `#F3F6FA`, surface `#FFFFFF`, foreground `#172333`.
- The current secondary token `#567D9F` reaches approximately 4.35:1 against white; do not use it for normal-sized text until darkened to at least 4.5:1.

## Premium Glass

- Layered translucent surfaces over a stable dark base.
- Current source uses an approximately 80%-opaque surface token (`#CC26354B`) and intentionally avoids real-time blur.
- Prefer translucent fills with sufficient contrast; provide a solid dark fallback.
- Do not describe selective blur, frosted-glass rendering or animated gradients as implemented features. They are not present in the current Compose source and are not required for the low-RAM MVP.

## Shared requirements

- Persist selection and apply it immediately to every native surface.
- Keep the picker in secondary controls; do not add a permanent theme icon to the main toolbar.
- Meet WCAG AA contrast for text and expose state without relying on color alone.
- Preserve system font scaling, TalkBack semantics, visible focus and minimum 48 × 48 dp touch targets.
- Keep panels responsive and scrollable on small displays.
- Avoid decorative motion; respect Android reduced-motion/animation settings if motion is introduced.
- Verify all three modes with device screenshots, TalkBack and contrast tooling before claiming full accessibility compliance.
