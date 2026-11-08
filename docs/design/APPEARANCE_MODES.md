# Appearance Modes

All native browser chrome must use the centralized semantic palettes in `RivoColorSchemes`. `BrowserScreen` applies the selected `MaterialTheme` around the browser surface and every modal panel, permission prompt, download confirmation and notice dialog. Web-page content remains controlled by the visited website.

## Dark

- Graphite background and surfaces, light text and blue-tinted accents.
- Current source palette: background `#0D1118`, surface `#171E28`, foreground `#E8EEF7`.
- An OLED-black variant is not currently implemented.

## Light

- Neutral light background, white surfaces, dark foreground text and blue accents.
- Current source palette: background `#F3F6FA`, surface `#FFFFFF`, foreground `#172333`.
- Secondary token is `#4D7192` (approximately 5.13:1 against white), meeting WCAG AA for normal-sized text.

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
