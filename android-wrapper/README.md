# MIKASA WIRELESS — Android wrapper

Android Studio project wrapping the original `Mikasa_Wireless_Tema_v37.html` in a local WebView. The source HTML is copied byte-for-byte to `app/src/main/assets/index.html` before Android integration. Runtime bridge callbacks are added only in the bundled copy so the uploaded original remains unchanged.

## Build APK

- Open `android-wrapper/` in Android Studio.
- Use JDK 17 and Android SDK Platform 35.
- Run `:app:assembleDebug` or start the **Android APK** workflow in GitHub Actions.
- APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Runtime notes

- UI loads locally from Android assets; a hosted website is not required.
- JavaScript and DOM storage are enabled because the original application uses JavaScript and `localStorage`.
- The WebView file chooser supports backup import and profile photo selection.
- Export, share, print, and vibration use Android native bridge methods.
- Customer and transaction data remains in local WebView storage. Keep regular backup exports.
- External links open in the system browser or matching apps; mixed HTTP content is blocked.

## Source integrity

Original uploaded HTML SHA-256: `9d9e9b4dbefd87f25829ec88d66b1dbcd742c210f42c905fc50c9444efb2a8dd`.
