# KMP FinBank Demo

A portfolio-grade Kotlin Multiplatform banking/fintech demo application showcasing shared business
logic, Compose Multiplatform UI, Clean Architecture, Ktor MockEngine, Koin, DataStore, and Android
biometric authentication.

> **Disclaimer:** This project uses mock data only. It does not connect to any real banking service,
> payment gateway, or customer data source.

---

## Overview

KMP FinBank Demo is a Kotlin Multiplatform sample project designed to demonstrate how a
fintech-style mobile application can be structured with shared business logic and shared UI across
Android and iOS.

The project focuses on clean architecture, modular code organization, reactive state management,
mocked backend communication, minor-unit based money handling, and platform-specific biometric
authentication on Android.

---

## Highlights

- Kotlin Multiplatform shared codebase
- Compose Multiplatform UI for Android and iOS
- Clean Architecture with MVVM-style presentation
- Repository and Use Case patterns
- Ktor Client with MockEngine for simulated backend responses
- Coroutines, Flow, and StateFlow for async/reactive flows
- Koin for dependency injection
- DataStore Preferences for local session/settings state
- Android biometric login using Android Keystore and BiometricPrompt
- Minor-unit based money representation using `Long`
- Safe enum mapping for mocked/API values
- Fintech-style flows: login, dashboard, accounts, cards, transactions, transfers, receipts, and
  profile

---

## Features

- Username/password login with mocked backend response
- Android biometric login after first successful login
- Dashboard with account overview
- Accounts list and account detail screens
- Cards list with masked card numbers
- Transaction history
- Money transfer flow
- Transfer confirmation and receipt screen
- Profile and settings screen
- Balance visibility toggle
- Simulated session timeout
- Local transaction cache

---

## Demo Credentials

The app uses a mocked backend, so any non-empty username and password can be used.

```text
Username: demo
Password: demo
```

---

## Tech Stack

| Area                 | Technologies                                            |
|----------------------|---------------------------------------------------------|
| Language             | Kotlin                                                  |
| Cross-platform       | Kotlin Multiplatform, Compose Multiplatform             |
| UI                   | Compose Multiplatform, Material Design                  |
| Architecture         | Clean Architecture, MVVM, Repository Pattern, Use Cases |
| Networking           | Ktor Client, Ktor MockEngine, Kotlinx Serialization     |
| Async                | Coroutines, Flow, StateFlow                             |
| Dependency Injection | Koin                                                    |
| Local Storage        | DataStore Preferences                                   |
| Android Security     | Android Keystore, BiometricPrompt                       |
| Testing              | Kotlin Test / Common Tests                              |

---

## Architecture

The project follows a layered architecture:

```text
Presentation -> Domain -> Data
```

### Presentation Layer

- Compose screens render immutable UI state.
- ViewModels expose `StateFlow<UiState>`.
- User actions are delegated from screens to ViewModels.
- Navigation and biometric prompts are handled at the UI boundary.

### Domain Layer

- Contains business models and use cases.
- Use cases coordinate app-specific business logic.
- Repository interfaces define contracts between domain and data layers.
- Money values are represented using minor units with `Long` to avoid floating-point precision
  issues.

### Data Layer

- Repositories coordinate remote calls, local storage, cache access, and mapping.
- Ktor MockEngine simulates backend responses.
- DTOs are mapped into domain models before reaching the UI.
- API/mock enum values are mapped safely to domain enums to avoid crashes from unexpected values.

---

## Data Flow

```text
Screen
  -> ViewModel
  -> UseCase
  -> Repository
  -> Mock API / Local Storage
  -> Repository
  -> UseCase
  -> ViewModel StateFlow
  -> Screen
```

---

## Money Handling

Money values are represented as minor units using `Long`.

For example, with USD:

```text
245075 -> 2,450.75 USD
50000  -> 500.00 USD
12599  -> 125.99 USD
```

This avoids floating-point precision issues that can happen when using `Double` for monetary values.

---

## Authentication Flow

On first login, the user enters a username and password. The mocked backend returns:

```text
accessToken
refreshToken
```

The `accessToken` is stored for the active session.

On Android, the `refreshToken` can be stored behind biometric authentication. During biometric
login, the app decrypts the stored refresh token, calls the mocked refresh endpoint, receives a new
access token, and navigates to the dashboard.

If biometric authentication fails, the biometric token is unavailable, or the session is considered
expired, the user is asked to log in again with username and password.

> iOS biometric authentication is intentionally not implemented in this demo version.

---

## Project Structure

```text
composeApp/src/commonMain/kotlin/dev/mjamali/kmpfinbank/
  biometric/              # Shared biometric abstractions
  common/                 # Shared utilities, result/resource models, time helpers
  data/                   # DTOs, mock API, repositories, local storage, mappers
  di/                     # Koin modules
  domain/                 # Domain models, repository contracts, use cases
  presentation/           # Screens, ViewModels, navigation, UI state

composeApp/src/androidMain/
  kotlin/                 # Android-specific implementations
  res/                    # Android resources

composeApp/src/iosMain/
  kotlin/                 # iOS-specific implementations

iosApp/
  iosApp.xcodeproj        # iOS application project
```

---

## Running the Project

### Android

Clone the repository:

```bash
git clone https://github.com/mjamalidev/kmp-finbank-demo.git
cd kmp-finbank-demo
```

Build the Android app:

```bash
./gradlew :composeApp:assembleDebug
```

Or open the project in Android Studio and run the Android target.

### iOS

Open the iOS project in Xcode:

```text
iosApp/iosApp.xcodeproj
```

Make sure Gradle dependencies are synced from Android Studio before running the iOS target.

---

## Running Tests

```bash
./gradlew :composeApp:allTests
```

The project includes common test setup and can be extended with additional tests for domain use
cases, repositories, mappers, formatting, and validation logic.

---

## Mocked Backend

All backend responses are mocked inside the project using Ktor MockEngine.

Mocked flows include:

- Login
- Token refresh
- Accounts
- Cards
- Transactions
- Money transfer
- Receipt generation

No real network request is sent to any external banking or payment service.

---

## Current Limitations

This project is intended as a portfolio/demo project, not a production banking application.

Current limitations:

- No real backend integration
- No real payment processing
- No real user or customer data
- iOS biometric authentication is not implemented
- Session timeout is simulated
- Currency formatting is simplified and currently optimized for USD-style minor units

---

## Roadmap

- Add more unit tests for use cases, repositories, and mappers
- Add GitHub Actions CI for build and test checks
- Add a short demo GIF or screenshots in the future
- Improve session timeout handling based on real user activity
- Add iOS biometric implementation using LocalAuthentication
- Add UI tests for critical flows
- Improve currency formatting and localization support

---

## Why This Project Exists

This repository demonstrates how a fintech-style Kotlin Multiplatform application can be structured
using modern Android/KMP development practices.

It is mainly focused on:

- Shared business logic
- Shared Compose UI
- Clean architecture
- Testable boundaries
- Mocked API-driven development
- Platform-specific security integration on Android
- Safer money modeling using minor units instead of floating-point values