# Android 16 KB Page Size and Accessibility Audit

Audit date: 2026-11-17  
Audited Main baseline: `2f560d21cbe345864c87f6587b7198cf3fb2f840`

## 16 KB page-size compatibility

### Build configuration
- Android Gradle Plugin: **9.5.1**, configured in root `build.gradle.kts`. This is newer than Android's documented AGP 8.5.1 minimum for 16 KB-aligned uncompressed shared libraries.
- Shipping target SDK: **36**; the 16 KB requirement applies to Android 15+ devices for apps targeting API 35+.
- Application ABIs: `arm64-v8a` and `armeabi-v7a`. The 16 KB device check is relevant to the 64-bit `arm64-v8a` ABI.
- No project-owned NDK, CMake or ndk-build source is present. An NDK version is therefore not pinned for project-native compilation. GeckoView 159 is a prebuilt SDK dependency and its packaged `.so` files must be validated as delivered; setting an arbitrary NDK version would not rebuild those binaries.
- The shipping app and benchmark application explicitly use `jniLibs.useLegacyPackaging = false`, keeping shared libraries uncompressed so AGP can package them with 16 KB ZIP alignment.

### Native library evidence and CI checks
- `tools/verify_16kb_alignment.py` inspects packaged 64-bit ELF shared libraries and rejects any PT_LOAD segment alignment below 16,384 bytes.
- The same check validates APK ZIP alignment with Android `zipalign -c -P 16 4`.
- Android CI builds shipping and benchmark APKs/AABs before running this checker. AAB inspection checks ELF alignment; APK inspection additionally checks ZIP alignment.
- This validates the packaged prebuilt native libraries, including GeckoView, rather than assuming compatibility from the AGP version alone.

### Remaining boundary
Static package inspection does not prove runtime behavior on a 16 KB kernel. Test the signed release candidate on an Android 15+ 16 KB emulator/device and review page-size assumptions in any future native code or SDK upgrades.

## TalkBack and accessibility audit

### UI inventory
- The shipping browser interface is Jetpack Compose; the repository contains no XML layout resources. XML resources are manifests and FileProvider path configuration, not visual layouts.
- The engine benchmark screen is created programmatically with Android Views.
- Browser page content is rendered by GeckoView's Android view and uses engine-provided web-content accessibility; browser chrome is Compose.

### Source changes in this audit
- Bookmark removal's visual `×` action now has a contextual accessible name.
- Appearance options are a single selectable row each with `Role.RadioButton`, selected-state semantics and an explicit 48 dp minimum height.
- Reader Mode's text-size slider has an explicit accessible name.
- Browser panel and Reader Mode titles are marked as semantic headings.
- Existing tab-close action retains its contextual label; other text-labeled Material controls retain their visible TalkBack names.
- Material 3 interactive controls provide their minimum interactive touch targets. Custom appearance rows explicitly meet 48 dp.
- Focus traversal follows the declared Compose/View hierarchy (top browser controls, page view, bottom navigation, then modal content); no custom focus-order override is needed for the current linear layout.

### Required device validation (not claimed complete)
- TalkBack traversal and announcements on a physical device.
- Keyboard/D-pad focus traversal and modal focus containment/return.
- 320 dp width, landscape, display-size changes and 200% font scaling.
- Contrast and visibility in Dark, Light and Premium Glass modes.
- Touch-target inspection with Accessibility Scanner, including benchmark controls.
- GeckoView page accessibility on representative sites.

## Evidence classification

Source inspection and CI static checks are not equivalent to on-device accessibility acceptance or runtime 16 KB validation. Keep these as explicit release QA gates until results are recorded against a build SHA and device model.
