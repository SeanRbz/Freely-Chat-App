# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Single-module Android app named **Freely** (`:app`, package `com.example.composeapp`; the name comes from `@string/app_name`) built with Jetpack Compose, MVVM, Hilt, Retrofit and Coroutines/Flow. `README.md` is the public overview (features, setup, mock API, release signing); update it when those change.

## Build & test

No JDK is on `PATH` and `JAVA_HOME` is unset. Point Gradle at the JDK 17 that Android Studio uses (recorded in `.gradle/config.properties`):

```bash
# Git Bash
export JAVA_HOME="/c/Users/Admin/.jdks/jdk-17.0.20.1+1"
# PowerShell: $env:JAVA_HOME = "C:\Users\Admin\.jdks\jdk-17.0.20.1+1"; then use .\gradlew.bat
```

```bash
./gradlew compileDebugKotlin          # fastest compile check (includes kapt/Hilt codegen)
./gradlew assembleDebug               # build debug APK
./gradlew installDebug                # install on connected device/emulator
./gradlew testDebugUnitTest           # JVM unit tests (app/src/test)
./gradlew testDebugUnitTest --tests "com.example.composeapp.ExampleUnitTest.addition_isCorrect"
./gradlew connectedDebugAndroidTest   # instrumented tests (needs a device)
./gradlew lintDebug
```

A cold build takes about 4 minutes.

### Tests

- JVM unit tests live in `app/src/test`. They cover the repositories, the API contract and `DateUtils`. The instrumented tests are still the template ones.
- Write tests first (TDD). Run them to see them fail for the expected reason before writing or restoring the code that makes them pass.
- Structure each test as Arrange-Act-Assert, with `// Arrange`, `// Act` and `// Assert` comments (see `NetworkFreelyRepositoryTest`):
  - Make one call to the class under test in Act.
  - Set up state through fakes such as `FakeSentMessageDao` or the fake service, not through the class under test.
  - Check one behavior per test.
  - Name tests `method_condition_expectedResult`, e.g. `getInbox_apiUnreachable_returnsTheBuiltInInbox`.
- Repository tests pass `Dispatchers.Unconfined` and read results with `runBlocking { flow.first() }`.
  - `Result.Success` is a data class, so compare whole results with `assertEquals`.
  - `android.util.Log` isn't available in JVM tests.

## Toolchain constraints

- All versions live in `gradle/libs.versions.toml` (AGP 8.5.2, Kotlin 2.0.21, Hilt 2.51.1).
  - Plugins are declared under `[plugins]`. The root `build.gradle.kts` lists them with `alias(libs.plugins.x) apply false`, and `app/build.gradle.kts` applies them with `alias(libs.plugins.x)`. There's no `buildscript {}` block.
  - Plugin markers resolve from the `pluginManagement` repositories in `settings.gradle.kts`.
  - Libraries are referenced as `libs.*`.
- The Compose compiler comes from the `org.jetbrains.kotlin.plugin.compose` Gradle plugin.
  - All Kotlin plugins share the catalog's single `kotlin` version, which keeps the Compose compiler in lockstep with Kotlin.
  - Don't reintroduce `composeOptions.kotlinCompilerExtensionVersion`.
  - Android Studio's K2 mode fails on the old setup with "Cannot access class 'androidx.compose.runtime.internal.ComposableFunctionN'".
- Hilt and Room use **kapt**, not KSP. The warning "Kapt currently doesn't support language version 2.0+. Falling back to 1.9." is expected.
- `gradle.properties` sets `android.enableR8.fullMode=false`. Full mode strips the generic signatures Retrofit 2.9 needs for `suspend` service methods, so keep it off unless Retrofit is upgraded to a version that ships its own R8 rules.
- In the catalog, `androidx-ui-test-junit4` actually points to `kotlinx-coroutines-core`, so the Compose UI test APIs are not on the androidTest classpath.
- Release builds use R8 minify + resource shrinking. `proguard-rules.pro` keeps only `com.example.composeapp.repository.entities.**`. Gson parses DTOs by reflection, so new DTOs must go in that package.
- Release signing uses `app/keystore/freely.jks`. `app/build.gradle.kts` reads `storeFile`, `storePassword`, `keyAlias` and `keyPassword` from the gitignored `keystore.properties` at the repo root. Without that file, `assembleRelease` still succeeds but the APK is unsigned. Never put signing passwords in tracked files.
- There's no Firebase, Crashlytics or analytics. `Core` is a bare `@HiltAndroidApp` Application.

## Architecture

The UI is the "Freely" design from the Google Stitch project "Social Friendly Chat App": Sign In, Discover, Messages, Chat Thread and Profile. Contacts has no Stitch screen and reuses the Messages building blocks.

The app has since been renamed **Freely** in everything users see. Code names still say Freely (`FreelyTheme`, `FreelyRepository`, `FreelyComponents`, `loop.db`); leave them, since renaming `loop.db` would drop saved data. The Messages header reads "Active Messages", though the data still calls that row `activeFreelys`. Group names like "Design Freely" are in-app terms, not the app name.

### Data flow (one direction per feature)

`FreelyRepository` → use case → `@HiltViewModel` → Composable

- **Repository:** the `FreelyRepository` interface (`repository/FreelyRepository.kt`) is bound in `AppModule` to **`NetworkFreelyRepository`** (`repository/remote/`), which reads the Freely API through the Retrofit interface `FreelyService`.
  - The Freely API is a Postman mock server created from the "Freely Mock API" collection, `postman/freely-mock-api.postman_collection.json`, at `BaseUrlService.baseUrl`. It serves `GET discover`, `inbox`, `chats/{id}` (404 for unknown ids), `contacts` and `me`.
  - The data lives in three places that must agree: Postman's saved examples (what the app shows), the collection file, and `repository/mock/MockFreelyData.kt`. Change all three together. `FreelyApiContractTest` parses the file with the app's Gson and fails if it disagrees with `MockFreelyData`.
  - **Fallback:** when an API call throws (offline, timeout, HTTP error, unreadable JSON), `NetworkFreelyRepository.fetch()` returns the same data from `MockFreelyData`, still merged with sent messages.
    - A chat id with no built-in copy keeps the API's error.
    - Contacts are cached only when they come from the API.
    - Cancellation is rethrown.
    - A body that parses but is missing fields doesn't throw (Gson leaves them null), so it doesn't fall back.
  - **`MockFreelyRepository`** serves the same data from `MockFreelyData` without touching the network. Bind it in `provideFreelyRepository` to skip the API entirely.
  - Photos are Stitch-hosted googleusercontent URLs, and `sizedImageUrl()` appends `=s<px>` to load a resized copy. The exception is the current user's photo (`MockFreelyData.Images.SELF`), an Unsplash URL with its own size parameters, which `sizedImageUrl()` leaves alone.
  - Calls return `flow { emit(...) }` mapped to `Result.Success`, with `.catch { emit(Result.Error(it)) }.flowOn(io)`. `getInbox()` and `getChatThread()` combine with a Room flow, so they emit again after every sent message without refetching.
  - The API can't take messages yet. `sendMessage()` saves to the `sent_messages` table, and the helpers in `repository/local/SentMessageMerge.kt` append sent messages to the thread and move that conversation to the top of the inbox with a "You:" preview. A first message to a contact adds them to the inbox.
- **Local database:** Room, in `repository/local/`. `FreelyDatabase` (`loop.db`, provided by `di/module/DatabaseModule.kt`) has two tables:
  - `session`: a single row that exists while someone is signed in. `SessionRepository` → `LocalSessionRepository` reads and writes it.
  - `sent_messages`: one row per sent message, keyed by conversation id.
  - Room entities live in `repository/local/`, not `entities/`. The schema isn't exported, so changing a table needs a version bump and a `Migration`.
- **Domain models** live in `repository/entities/FreelyModels.kt` (`Person`, `Conversation`, `ChatThread`, `ChatMessage`, `UserProfile`, …).
- **Legacy Retrofit layer:** `UserService` → `UserRepositoryImpl` → the Login/Signup/GetNearbyUsers use cases is still wired in `AppModule`, but no screen uses it, and its endpoints (`/userList`, `/login`, `/signup`) don't exist on the current mock server.
- **Use cases** (`repository/usecases/`): extend `utils/FlowUseCase<P, R>`. Call them as functions, `useCase(params)`, which returns `Flow<Result<R>>`.
- **ViewModels** (`ui/viewModel/`): `launch` + `collectLatest` on the use case, then fold the result into a private `MutableStateFlow<XxxUiState>` exposed as `StateFlow`.
  - UI-state classes live next to their screens (`ui/views/<feature>/<Feature>UiState.kt`).
  - Start initial loads from the ViewModel's `init`, never from a composable body.
  - `ChatThreadViewModel` reads its conversation id from `SavedStateHandle[ARG_CONVERSATION_ID]`.
- **Composables** are split into two layers:
  - A stateful `XxxPage(viewModel, …)` collects with `collectAsStateWithLifecycle()`.
  - It delegates to a stateless `XxxContent(uiState, onXxx callbacks, modifier)`.
  - `@Preview`s target the `Content` composable, because `hiltViewModel()` doesn't work in previews.
- `Result` is the app's own sealed class `com.example.composeapp.utils.Result` (Success/Error/Loading), not `kotlin.Result`. Import it explicitly.

### DI

- `di/module/AppModule.kt` provides:
  - `FreelyRepository` → `NetworkFreelyRepository`, plus `FreelyService`.
  - The network stack: Gson (lenient, with `ChatMessageDeserializer` registered), OkHttp with a BODY-level logging interceptor, and one Retrofit qualified `@Named("retrofitUserService")` that both `FreelyService` and the legacy `UserService` are created from.
  - The concrete legacy `UserRepositoryImpl`.
- Inject dispatchers with the qualifiers in `di/qualifiers/CoroutineQualifiers.kt` (`@IoDispatcher`, `@DefaultDispatcher`, `@MainDispatcher`, `@MainImmediateDispatcher`) instead of referencing `Dispatchers.*` directly.
- The base URL is hardcoded in `repository/remote/BaseUrlService.kt` and points at the Freely API's Postman mock server.

### Serialization gotcha

Retrofit uses `GsonConverterFactory`. The kotlinx `@Serializable` and `@JsonNames` annotations on entities are **not** used for network parsing. JSON keys must match Kotlin property names exactly: `@JsonNames("status_code")` on `BaseResponseEntity.statusCode` has no effect. Response DTOs implement `BaseResponseEntity` (`statusCode`, `message`). The RxJava2 call adapter is registered, but every service method is `suspend`.

Chat messages carry a `type` field (`Text`/`VoiceMemo`/`Place`), which `ChatMessageDeserializer` uses to pick the `ChatMessage` subclass. Gson resolves to **2.8.5** through Retrofit's converter, so newer Gson APIs such as `JsonParser.parseString` aren't available.

### Navigation: two activities, each with its own NavHost

- **`SplashActivity`** (launcher) shows the system splash, then `navigation/NavHostSplashActivity.kt` hosts `SignInPage`.
  - Sign-in is mocked: a valid phone or email, or either social button, saves a session through `SignInUseCase`. `SignInPage` then calls `onSignedIn` from a `LaunchedEffect` keyed on `isSignedIn`. That callback is `SplashActivity.openMain`, which starts `MainActivity` and finishes `SplashActivity`.
  - On launch, `SplashActivity` keeps the system splash up while `SessionViewModel` reads the session, and goes straight to `MainActivity` when one exists.
  - Navigation happens inside callbacks. Never call `navigate()` or `startActivity()` from a composable body; it re-fires on every recomposition.
- **`MainActivity`**: a `Scaffold` with `FreelyTopBar` and `FreelyBottomBar`, shown only on tab routes. The chat thread is full screen.
  - The tabs come from `constants/Constants.NavBottomList`: **Discover, Messages, Contacts, Profile**. Discover is first and is the NavHost start destination, so it's the landing screen after sign-in.
  - The selected tab is read from `currentBackStackEntryAsState()`.
  - Switch tabs with `NavHostController.navigateToTab()`, which pops to start with `saveState`, plus `launchSingleTop` and `restoreState`.
  - `MainActivityScreens.ChatThread.createRoute(id)` opens a chat. The id is a conversation id (`maya`, `weekend-hikers`, …) or a person id (`chloe`, `sofia`, …).
  - Log Out calls `SessionViewModel.signOut()`. When `MainActivity` sees there's no session, it restarts `SplashActivity` with `NEW_TASK | CLEAR_TASK`. Sent messages are kept, the way a server would keep them.
  - The NavHost is in `navigation/NavHostMainActivity.kt`. Each destination gets its ViewModel via `hiltViewModel()` inside its `composable {}` block, so it's scoped to the back stack entry.
  - The NavHost applies `consumeWindowInsets(innerPadding)`, so `imePadding()` inside screens doesn't double-count the system bars. Both activities declare `windowSoftInputMode="adjustResize"`.
- Add new routes to the matching sealed class (`SplashScreens` / `MainActivityScreens`) and register them in that NavHost.

### UI conventions

- **Theme:** both activities wrap content in `FreelyTheme` (`ui/theme/`). It's light-only, with no dynamic color, and built from the Stitch tokens:
  - Material 3 color roles live in `Theme.kt`.
  - Brand accents (`SunnyCoral`, `GoldenMarigold`, `ZestyMint`, `DeepEspresso`, `CocoaMute`, `PeachCreme`, …) live in `Color.kt`.
  - Reusable brushes live in `FreelyGradients`.
  - Type is Plus Jakarta Sans (`res/font/plus_jakarta_sans_*`; the OFL license is in `assets/licenses/`), mapped onto M3 slots in `Type.kt`.
  - Shapes: small 16dp, medium 24dp, large 32dp.
- **Styling:** use `MaterialTheme.colorScheme` / `typography` / `shapes` and the brand constants. Don't hardcode hex colors or font sizes in screens.
- **Components:** reuse the shared ones in `ui/views/base/FreelyComponents.kt`:
  - `Avatar` (ring, presence dot; a null URL shows a group icon) and `PresenceDot`
  - `FreelyLogoMark`, `FreelyTopBar`, `GradientButton`, `SoftPill`, `IconCircle`, `FreelyCard`
  - `SectionHeader`, `FreelyTextInput`, `FreelySearchField`, `UnreadBadge`, `EmptyHint`
- **Icons** come from `material-icons-extended`. Use the `Icons.AutoMirrored.*` variants for arrows, send and logout.
- **Lazy lists** key items by model `id`.
- **XML theme:** `Theme.ComposeApp` is non-fullscreen on purpose. With `enableEdgeToEdge()`, the fullscreen parent left a black band over the display cutout.
- **Launcher icon and splash** are XML versions of `FreelyLogoMark`; change them together with it.
  - The launcher icon is adaptive only (`mipmap-anydpi/ic_launcher*.xml`). minSdk 27 never needs bitmap fallbacks, so don't add `.webp`/`.png` launcher icons.
  - `ic_launcher_background` is the Sunrise gradient, spread over the 72dp the launcher mask shows. `ic_launcher_foreground` is the white `ChatBubbleOutline`, which is also the themed-icon monochrome layer.
  - `Theme.Splash` shows `ic_splash_logo` (gradient tile with the bubble) on `@color/loop_background`, so it hands off to Sign In without a color flash.
  - Android 13+ shows the splash icon only for launcher starts. `adb shell am start` gets a plain background, so check it by tapping the app icon.

## Incomplete areas

These are as of this writing; check the code before relying on them.

- **No real backend.** The Freely API is a Postman mock server that only answers GETs with saved examples. The session and sent messages are stored on the device in Room.
- **Decorative controls:**
  - Settings rows, Share profile, call/video/info, attach/emoji/mic, "View All" and "Directions" don't do anything yet.
  - Discover's Pass, Wave and Save only update `DiscoverViewModel` state.
  - Profile's Share Link opens the system share sheet.
- **Old resources:** the drawables and fonts used by the removed screens (`ic_home`, `ic_splash_backgorund_1`, Montserrat, Salsa, …) are no longer referenced. Release resource shrinking drops them.
