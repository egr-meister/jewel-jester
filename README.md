# Jewel Jester

Jewel Jester is a native Android game with a jewels-and-jester (carnival) theme.
It has two offline modes: a **Quiz** (three themed categories) and **Matching
Pairs** (a memory game across nine levels), plus menu, results, settings, pause,
and rules screens. All progress is stored locally on the device.

## Main features

- Quiz mode with three categories (Gem Master, Jester's Court, Carnival Legend), 10 questions each, with correct/incorrect highlighting and best-score saving.
- Matching Pairs memory game: 9 levels of increasing difficulty (2 → 12 pairs), per-level countdown timer, win (Well Done) / lose (Out of Time) states.
- Level unlocking: clear a level to unlock the next.
- Results summary and a Settings screen (sound toggle, privacy, rate, reset with confirmation).
- Custom jewel/jester visual system: purple + gold, real image assets for logo, background, buttons, panels and 12 jewel items.

## Offline-only architecture

The app is **fully offline**. There is no networking code, no `INTERNET`
permission, and no external services. Everything runs locally.

## Local storage & DataStore

All saved state is a single serialized JSON string kept in **DataStore
Preferences** (key `game_json`). Serialization uses **Kotlinx Serialization**.
The stored model (`GameData`) holds best quiz scores per category, best matched
pairs per level, and settings. Every field has a default, so old/partial JSON
stays compatible after model changes. Reading is fully guarded: empty storage,
missing key, empty JSON, and corrupted JSON all fall back to defaults instead of
crashing (see `GameSerializer` and its unit tests).

## Privacy

Jewel Jester stores only game progress and settings locally on the device. No
account, no cloud sync, no internet access, no ads, no analytics, no payments,
no location, no notifications. Nothing leaves the device.

## Missing integrations (by design)

No backend, no remote API, no Firebase, no cloud sync, no account/login, no ads,
no analytics, no payments/subscriptions/IAP, no push notifications, no
background services, no camera/microphone/location/contacts/storage/sensors.

## Visual concept

- Style: "Carnival Jewel Court" — deep purple stage with gold-framed jewels and jester motifs.
- Main visual object: the jester mascot and jewel set, over a gem-bordered background (`bg_main`).
- Layout uniqueness: the home screen is a logo + full jester mascot + a single Start plate — not a generic mascot→title→stats→button-stack dashboard.
- Buttons are a reusable empty plate image (`btn_plate`) with the label drawn on top in code, so one plate serves every button.
- Palette: Jewel Deep `#2A0A4A`, Jewel Purple `#4A148C`, Jewel Light `#6A1FB0`, Gold `#F5C542`, Gold Dark `#B8860B`; status Green `#2F8F5B`, Red `#B73A3A`.
- App icon: adaptive icon, gold crown on a deep-purple background (`ic_launcher`).
- Splash: system window background uses the deep-purple theme color; no heavy splash asset.

## Replacing the graphics

Assets live in `app/src/main/res/drawable-nodpi/`. Replace a file with your own
using the same name (see `data/Assets.kt`):
`bg_main.jpg`, `logo.png`, `banner.png`, `jester_main.png`, `btn_plate.png`,
`plate_round.png`, `panel_bg.png`, `icon_back.png`, `icon_pause.png`,
`jewel_01.png … jewel_12.png`.

## Technology stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Android ViewModel ·
Kotlin Coroutines · Kotlin Flow · DataStore Preferences · Kotlinx Serialization ·
Gradle Kotlin DSL · JDK 17.

## Architecture

Simple MVVM. A single local `GameRepository` (DataStore + Kotlinx
Serialization). `ProgressViewModel`, `QuizViewModel`, `PairsViewModel` expose
immutable UI state via `StateFlow`; screens collect with
`collectAsStateWithLifecycle`. The repository is created once in
`JewelJesterApp` and passed to ViewModel factories (no DI framework). No Room.

## Requirements

- Android Studio (Koala or newer).
- JDK 17.
- Android SDK Platform 36 and Build Tools 36.0.0.
- `compileSdk = 36`, `targetSdk = 36`, `minSdk = 24`.
- Portrait orientation, edge-to-edge with safe insets, standard Back behavior.
- 16 KB memory page-size compatible: the app is pure Kotlin/Compose with no native libraries, so the release AAB is compatible. Still verify the final bundle.

## Open the project

Open the `JewelJester` folder in Android Studio. Gradle syncs and downloads
dependencies (internet needed on first sync only). Then Run ▶.

### Gradle wrapper

The Gradle wrapper binary (`gradle-wrapper.jar`, `gradlew`) is intentionally not
committed. Android Studio generates it automatically the first time you open the
project. To build from the command line before that, generate it once (requires
Gradle 8.9 installed locally):

```bash
gradle wrapper --gradle-version 8.9
```

After that, `./gradlew` works. GitHub Actions provisions Gradle 8.9 itself, so CI
does not need the committed wrapper — commands below use `./gradlew` for local
use once the wrapper exists.

## Build a debug APK

```bash
./gradlew :app:assembleDebug
```

## Build a non-minified release (do this FIRST — staged R8)

R8 and resource shrinking are **off** by default. Build, install and verify the
release build once with shrinking disabled before enabling it.

```bash
export ANDROID_KEYSTORE_PATH=/absolute/path/jeweljester-release-key.p12
export ANDROID_KEYSTORE_PASSWORD=yourStorePassword
export ANDROID_KEY_ALIAS=jeweljester_key
export ANDROID_KEY_PASSWORD=yourStorePassword
./gradlew :app:assembleRelease :app:bundleRelease
```

The build **fails on purpose** if the four signing variables are missing — it
never falls back to a debug certificate.

### Enable R8 after verification

In `app/build.gradle.kts`, inside `buildTypes.release`, flip:

```kotlin
isMinifyEnabled = true
isShrinkResources = true
```

Rebuild, reinstall, and re-verify serialization, DataStore, navigation and all
detail screens. Kotlinx Serialization keep rules are already in
`proguard-rules.pro`.

## Generate a PKCS12 keystore

```bash
keytool -genkeypair -v -storetype PKCS12 \
  -keystore jeweljester-release-key.p12 -alias jeweljester_key \
  -keyalg RSA -keysize 2048 -validity 10000
```

Use the same password for the store and key. Never commit the keystore or
passwords (see `.gitignore`).

## Local signing

Signing values are read from environment variables only (see the release build
section). No keystore path or password is stored in the repo.

## GitHub Secrets

- `ANDROID_KEYSTORE_BASE64` — base64 of your `.p12` (`base64 -i jeweljester-release-key.p12`)
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

## GitHub Actions

`.github/workflows/android-build.yml` runs on push to `main` and via manual
dispatch. It uses JDK 17, installs SDK Platform 36 and Build Tools 36.0.0,
decodes the PKCS12 from `ANDROID_KEYSTORE_BASE64`, builds the signed release APK
and AAB, runs `apksigner verify --print-certs`, **fails if the certificate
contains `CN=Android Debug`** or if verification fails, and uploads the APK
(test artifact) and AAB (Google Play artifact). It does not print secrets and
does not run an emulator smoke test. CI proves the build/signing — not that the
app launches; verify launch locally.

## APK and AAB

The release build produces a signed APK (for local install/testing) and a signed
AAB (for Google Play). **Only the `.aab` is uploaded to Google Play.**

## Verify a release certificate

```bash
$ANDROID_HOME/build-tools/36.0.0/apksigner verify --print-certs app-release.apk
```

The certificate must **not** contain `CN=Android Debug`.

## Install and inspect a release APK

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
adb logcat
```

## Local launch checklist

1. First launch with empty storage (no crash, no reset).
2. Start → Menu navigation.
3. Quiz: each category, correct/incorrect highlight, final score, best score saved.
4. Quiz pause: Continue / Restart / Menu.
5. Matching Pairs: play a level, match pairs, win before time (Well Done).
6. Let the timer run out (Out of Time).
7. Level unlock after clearing the previous level.
8. Pairs pause: Continue / Restart / Menu.
9. Results screen reflects saved bests.
10. Settings: toggle sound, open Privacy, open Rate, Reset with confirmation.
11. Relaunch — progress persists. Reset — progress cleared.
12. Airplane mode — everything still works (fully offline).
13. Confirm no INTERNET permission and no runtime permission dialog.
14. `adb logcat` shows no crashes (serialization, DataStore, navigation, NumberFormat).
15. Repeat after enabling R8.

## Data reset

Settings → Reset Results clears all saved quiz scores and level progress
(`GameRepository.resetAll()`), after a confirmation dialog.

## Tests

Unit tests (`app/src/test`): deck building (correct pair counts), level
unlock/completion logic, completed-level counting, and safe JSON handling
(null/empty/corrupted/unknown-keys/missing-fields round trips).

```bash
./gradlew :app:testDebugUnitTest
```

## Known limitations

Quiz question texts are placeholder content for the jewel/jester theme and can
be freely edited in `data/QuizData.kt`. Level difficulty (pairs/columns/seconds)
is defined in `data/PairsData.kt`.
