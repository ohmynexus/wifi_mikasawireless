# Build & behavior notes

- Application ID: `id.mikasa.wireless`
- Display name: `MIKASA WIRELESS`
- Version: `37.0` (versionCode 37)
- Minimum Android: API 23
- Compile / target API: 35
- Renderer: Android System WebView; local asset is bundled with APK

## Native integration

The bundled HTML calls a narrow Android bridge for JSON/CSV exports, Android share sheet, print-to-PDF/system print, and vibration. The Android activity provides a file chooser for the app's existing image picker and backup-import flows. WebView DOM storage persists app data locally on the device.

## CI build

Run the **Android APK** workflow manually from the Actions tab. It uploads `app-debug.apk` as artifact `mikasa-wireless-debug-apk`.

The debug APK is intended for sideload testing. Play Store distribution requires a release build signed with a user-owned key; no signing secrets are committed.
