# Whisper AI — GitHub-ready V1

A personal Android floating assistant: tap the **W** bubble, capture the current screen, send it to Gemini, and show a concise answer in a floating window.

## What is included

- Floating W overlay
- Android screen capture using MediaProjection
- Screenshot → Gemini multimodal request
- Floating answer panel with selectable text
- Ask again
- Overlay transparency setting
- Optional user instruction
- GitHub Actions workflow that builds `app-debug.apk` in the cloud

## Build on GitHub — no PC required

1. Create a GitHub repository and upload the **contents of this folder** to the repository root.
2. Open **Actions** → **Build Whisper AI APK**.
3. Tap **Run workflow**.
4. After the run finishes, open the workflow run → **Artifacts** → `WhisperAI-debug-apk`.
5. Download the ZIP, extract it, and install `app-debug.apk` on your Android phone.

The workflow uses Java 17 and Gradle 8.10.2 and does not require Android Studio on your phone.

## First run

1. Open Whisper AI.
2. Enter your own Gemini API key.
3. Allow **Display over other apps**.
4. Grant Android screen-capture permission.
5. Start the overlay.
6. Open another app/document and tap the floating **W** bubble.

## Important

- Keep your Gemini API key private. Do not commit it to GitHub.
- The prototype stores the key locally in Android SharedPreferences.
- Screen contents are sent to Gemini when you tap the W bubble.
- Do not use it on screens containing passwords, banking information, private keys, or other sensitive information.
- API usage may incur charges or be subject to Google account/API limits.
