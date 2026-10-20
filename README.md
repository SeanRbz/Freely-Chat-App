# Freely

A friendly social chat app for Android, built with Jetpack Compose and MVVM. Discover people in your circles, say hi, and keep the conversation going.

> This is a portfolio project. The backend is a Postman mock server, and sign-in is simulated.

## Features

- **Sign in** with a phone number or email, or continue with Apple or Google. Sign-in is simulated, and the session stays on the device until you log out.
- **Discover** suggests people to meet, including who's new in your circles. Pass, Wave and Save respond in the app but aren't sent anywhere.
- **Messages** shows the Active Messages row and your conversations, with search.
- **Chat threads** show text, voice memo and place messages. Messages you send are saved on the device and move the conversation to the top of the inbox.
- **Contacts** lists your friends. Messaging someone for the first time adds them to the inbox.
- **Profile** shows your details, shares your profile link, and logs you out.

## Tech stack

| Area | Libraries |
| --- | --- |
| UI | Jetpack Compose, Material 3, Navigation Compose, Coil |
| Architecture | MVVM with use cases, Kotlin Coroutines and Flow |
| Dependency injection | Hilt |
| Network | Retrofit 2, Gson, OkHttp logging interceptor |
| Local storage | Room |
| Startup | AndroidX SplashScreen |
| Build | Gradle version catalog, AGP 8.5, Kotlin 2.0, kapt |

Supports Android 8.1 (API 27) and up; targets API 34.

## Architecture

Each feature's data flows one way:

```
FreelyRepository → use case → @HiltViewModel → Composable
```

- `NetworkFreelyRepository` reads the API through Retrofit (`FreelyService`) and merges in the messages you've sent, which Room stores on the device. If an API call fails, it serves the same data built into the app instead.
- Use cases extend `FlowUseCase` and return `Flow<Result<T>>`.
- ViewModels expose UI state as a `StateFlow`. Each screen has a stateful `XxxPage` that collects it and a stateless `XxxContent` that draws it, so previews don't need a ViewModel.
- There are two activities: `SplashActivity` shows the system splash and Sign In, and `MainActivity` hosts the bottom-bar tabs and chat threads.

## Getting started

You need Android Studio Koala or newer (for AGP 8.5) and JDK 17.

Open the project in Android Studio and run the `app` configuration, or use Gradle:

```bash
./gradlew assembleDebug       # build a debug APK
./gradlew installDebug        # install on a connected device or emulator
./gradlew testDebugUnitTest   # run the unit tests
```

If Gradle can't find a JDK, set `JAVA_HOME` to a JDK 17 install.

### Mock API

The app reads its data from a Postman mock server whose URL is in `repository/remote/BaseUrlService.kt`. To run your own:

1. Import `postman/freely-mock-api.postman_collection.json` into Postman.
2. Create a mock server from the collection.
3. Paste the mock server's URL into `BaseUrlService.baseUrl`.

If the mock server can't be reached or returns an error, the app falls back to the same data built into `MockFreelyData`, so it keeps working offline. Photos that weren't loaded before still need a connection. To skip the network entirely, bind `MockFreelyRepository` instead of `NetworkFreelyRepository` in `di/module/AppModule.kt`.

The collection and `MockFreelyData` must stay in sync; `FreelyApiContractTest` fails when they disagree. The app shows whatever the mock server's saved examples return, so update those in Postman too.

### Release builds

Release builds are shrunk with R8. Signing reads its settings from `keystore.properties` in the project root, which is gitignored:

```properties
storeFile=app/keystore/freely.jks
storePassword=<store password>
keyAlias=<key alias>
keyPassword=<key password>
```

Without that file, `./gradlew assembleRelease` still builds, but the APK is unsigned.

## Project structure

```
app/src/main/java/com/example/composeapp/
├── constants/          bottom-bar tabs
├── di/                 Hilt modules and dispatcher qualifiers
├── navigation/         routes and NavHosts for both activities
├── repository/
│   ├── entities/       API and domain models
│   ├── local/          Room database: session and sent messages
│   ├── mock/           built-in data and MockFreelyRepository
│   ├── remote/         Retrofit services and NetworkFreelyRepository
│   └── usecases/
├── ui/
│   ├── theme/          colors, type, shapes and gradients
│   ├── viewModel/
│   └── views/          one package per screen; shared components in base/
└── utils/
postman/                the mock API collection
```

## Tests

Unit tests in `app/src/test` cover the repositories, the API contract and date formatting:

```bash
./gradlew testDebugUnitTest
```

## Credits

- The UI design was generated with Google Stitch.
- Type is set in Plus Jakarta Sans, under the SIL Open Font License (`app/src/main/assets/licenses/`).
- People photos are Stitch-generated. The signed-in user's photo is from Unsplash.
