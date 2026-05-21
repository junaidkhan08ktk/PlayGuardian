# How to Open & Run PlayGuardian in Android Studio

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK installed (API 24–35)
- Internet connection for first Gradle sync

## Steps

### 1. Open the project
- Launch Android Studio
- Choose **File → Open**
- Navigate to and select the **PlayGuardian** root folder (the one containing `settings.gradle.kts`)
- Click **OK**

### 2. First-time sync
Android Studio will automatically:
- Download Gradle 8.7 (via the wrapper URL in `gradle/wrapper/gradle-wrapper.properties`)
- Download all dependencies from Maven Central and Google's Maven repository
- Index the project

This takes 1–5 minutes depending on your internet connection.

### 3. Run the sample app
- In the **Run** toolbar, select the **sample-app** configuration
- Connect a device or start an emulator (API 24+)
- Click the ▶ **Run** button (or press `Shift+F10`)

### 4. What you'll see in the app
The sample app has three main sections:

**Status Cards** (top) — Shows live grant status for all four permissions:
- ✓ Granted (green) or ✗ Not Granted (red)
- Updates every time you return from a settings screen

**Policy Audit** — Tap any audit button to run a risk evaluation:
- Returns severity (LOW / MEDIUM / HIGH) with developer warnings
- Does NOT open any settings or show any system UI

**Request Access** — Tap a request button to start the full workflow:
1. PlayGuardian runs a policy audit (shown in the output log)
2. If not already granted, the correct settings screen opens automatically
3. Grant or deny in settings, then press back
4. The app detects the result in `onResume` and logs it

**Output Log** (bottom, dark panel) — Color-coded, timestamped log of all events:
- 🟢 Green = granted / success
- 🟡 Yellow = warnings / policy concerns
- 🔴 Red = denied / errors
- 🔵 Blue = informational

### 5. Gradle wrapper note
The `gradle/wrapper/gradle-wrapper.jar` file is not included (it's a binary bootstrapper).
Android Studio does **not** need it — it uses its own bundled Gradle engine to sync.

If you want to use `./gradlew` from the command line, download the jar:
```bash
# Run from the PlayGuardian root directory:
./gradlew wrapper --gradle-version=8.7
```
Or download it manually from:
https://repo.maven.apache.org/maven2/org/gradle/gradle-wrapper/8.7/gradle-wrapper-8.7.jar
and place it at `gradle/wrapper/gradle-wrapper.jar`.

## Module structure
| Module | What it does |
|---|---|
| `playguardian-oem` | OEM detection + intent resolution + settings launcher |
| `playguardian-audit` | Policy risk evaluation + manifest scanner |
| `playguardian-core` | Public API: `PlayGuardian` singleton, verifier, callbacks |
| `sample-app` | Demo app wiring all library features together |
