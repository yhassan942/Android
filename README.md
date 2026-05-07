# Android Photo Album App

An Android photo management app for organizing photos into albums, tagging them, and searching by tags. Built with Java and Kotlin using Jetpack Compose, RecyclerView, and Material Design.

---

## Prerequisites

- Android Studio (for SDK/emulator)
- Java 11
- Android SDK 36

---

## Setup

Set `ANDROID_HOME` in your environment variables to your Android SDK path, then add `%ANDROID_HOME%\platform-tools` to your `Path`.

Verify it works:
```bash
adb --version
```

---

## Build

```bash
# Windows
.\gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

## Install

Start your emulator or plug in a device, then:
```bash
# Windows
adb install app\build\outputs\apk\debug\app-debug.apk

# Mac/Linux
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Usage

- Long press an album or photo to rename, delete, or move it
- Use the search icon to search by tags across all albums
- Toggle AND/OR in search to broaden or narrow results