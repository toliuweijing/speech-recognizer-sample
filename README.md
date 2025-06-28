# Speech Recognizer App

A vibe-coded sample Android application that demonstrates real-time speech-to-text transcription via SpeechRecognizer API. 

![output](https://github.com/user-attachments/assets/9de2d1cf-9e96-47b5-97f8-ccab65fc1067)

## APK
https://github.com/toliuweijing/speech-recognizer-sample/issues/2


## Features

*   Real-time speech transcription.
*   Simple user interface with a toggle button to start and stop transcription.
*   Displays the transcribed text on the screen.
*   Requests and handles necessary microphone permissions (`RECORD_AUDIO`).

## How to Build

This is a standard Android Gradle project. To build the application, run the following command in the project's root directory:

```bash
./gradlew build
```

## How to Run

1.  Build the application using the command above or open the project in Android Studio.
2.  Run the application on an Android device or an emulator.
3.  The app will request microphone permission upon launch. Grant the permission to enable transcription.
4.  Click the "Start Transcription" button to begin transcribing audio. The button will change to "Stop Transcription".
5.  Click "Stop Transcription" to pause the transcription.

## Project Structure

*   `app/src/main/java/com/innosage/cmp/example/speechrecognizer/MainActivity.kt`: The main activity of the application, responsible for the UI (using Jetpack Compose) and handling user interactions. It observes data from the `MainViewModel`.
*   `app/src/main/java/com/innosage/cmp/example/speechrecognizer/MainViewModel.kt`: The ViewModel responsible for the application's business logic, including managing the speech recognition state (e.g., whether it's listening, the transcribed text, and any errors) and interacting with the speech recognition service (not detailed in the provided files, but implied by its role).
*   `app/src/main/AndroidManifest.xml`: The Android manifest file, which declares the app's components, permissions (like `RECORD_AUDIO`), and other essential information.
*   `app/build.gradle.kts`: The Gradle build script for the `app` module, defining dependencies and build configurations.
*   `build.gradle.kts` (root): The top-level Gradle build script for the entire project.

## Dependencies

The project uses several AndroidX and Jetpack libraries, including:

*   Jetpack Compose for the UI.
*   Kotlin Coroutines and Flow for asynchronous operations.
*   AndroidX Activity and Lifecycle components.

(For a complete list, refer to the `app/build.gradle.kts` file.)
