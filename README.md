# 🌸 Lumi — Intelligent, Privacy-First Cycle & Fertility Tracker

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" width="96" height="96" alt="Lumi Icon" />
</p>

<p align="center">
  <b>A private, intelligent, and beautifully crafted cycle tracking application built with modern Android & Kotlin Multiplatform architecture.</b>
</p>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?logo=kotlin" alt="Kotlin" /></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-2026.02-green.svg?logo=android" alt="Compose" /></a>
  <a href="https://insert-koin.io/"><img src="https://img.shields.io/badge/DI-Koin%204.0-orange.svg" alt="Koin" /></a>
  <a href="https://developer.android.com/training/data-storage/room"><img src="https://img.shields.io/badge/Database-Room%20SQLite-yellow.svg" alt="Room" /></a>
  <a href="https://github.com/aselahemantha/Lumi/actions"><img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue.svg?logo=github-actions" alt="CI/CD" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-purple.svg" alt="License" /></a>
</p>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [UI Showcase](#-ui-showcase)
- [Key Features](#-key-features)
- [Cycle & Phase Calculation Engine](#-cycle--phase-calculation-engine)
- [Adaptive Home Screen Layouts](#-adaptive-home-screen-layouts)
- [Architecture & Tech Stack](#-architecture--tech-stack)
- [Project Directory Structure](#-project-directory-structure)
- [Security & Privacy](#-security--privacy)
- [CI/CD & Release Pipeline](#-cicd--release-pipeline)
- [Getting Started](#-getting-started)
- [Running Automated Tests](#-running-automated-tests)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [Support & Contact](#-support--contact)
- [Acknowledgements](#-acknowledgements)

---

## 🌟 Overview

**Lumi** is designed around a simple yet critical philosophy: **Reproductive health data belongs exclusively to the user.** 

Unlike conventional cloud-centric cycle trackers, Lumi implements an **offline-first, zero-knowledge architecture**. All cycle logs, basal body temperatures (BBT), LH test strips, symptoms, and moods are encrypted and stored locally on the device with hardware-backed biometric authentication.

---

## 📱 UI Showcase

<p align="center">
  <img src="app/src/main/res/drawable/onboarding_hero.png" width="400" alt="Lumi Onboarding Hero" />
</p>

Lumi's interface is carefully designed to be calming, intuitive, and accessible. The onboarding experience guides users gently through setting up their initial health profile without overwhelming them with data entry.

---

## ✨ Key Features

### 🔄 Dynamic Cycle & Flow Intelligence
- **Single-Entry Baseline**: Starts predicting cycle phases immediately from current date, cycle length, and flow length.
- **3-Month Historical Calibration**: Calculates accurate rolling averages when past cycle logs are entered manually during onboarding.
- **Continuous Recalculation**: Automatically updates rolling cycle and period duration averages every time a new flow is logged and closed.

### 🏠 3 Adaptive Home Layouts
The Home Screen dynamically reshapes its layout based on the user's active biological phase:
1. **Cycle Ring Layout**: For Menstruation, Follicular, and mid-Luteal phases.
2. **Fertility & Ovulation Dashboard**: For the peak Fertile Window (CD 11–15 on a 28-day cycle) with BBT/LH logging shortcuts.
3. **Symptom Grid & PMS Monitor**: For Late Luteal (CD 22–28) as progesterone drops.

### 📝 Comprehensive Daily Health Logging
- **Flow Intensity**: Light, Medium, Heavy, Spotting.
- **Moods**: Calm, Energetic, Sensitive, Tired.
- **Symptoms**: Cramps, Bloating, Headache, Acne, Backache + custom symptom creation.
- **Biomarkers**:
  - **Basal Body Temperature (BBT)**: Manual or Bluetooth thermometer tracking with sleep disturbance & fever filters.
  - **LH Ovulation Strips**: Low, High, and Peak surge intensity tracking with test brand metadata.

### 📅 Calendar & Visual Insights
- **Color-Coded Interactive Calendar**: Highlights predicted periods, confirmed flow days, fertile window, and ovulation day.
- **30-Day Symptom Trend Charts**: Visualizes recurring symptoms and correlates them with cycle phases.
- **Personalized Science-Backed Insights**: Dynamic daily guidance tailored to the user's active phase and logged symptoms.

### 🔒 Biometric Security
- **Biometric Prompt Gate**: Optional Fingerprint / Face ID unlock required upon app resume to protect sensitive health records.

---

## 🧮 Cycle & Phase Calculation Engine

Lumi calculates phases using mathematical physiology formulas derived from rolling completed cycles:

```
Cycle Length (L)       = Average of last completed cycles (Default: 28 days)
Period Duration (P)    = Average logged flow duration (Default: 5 days)

Ovulation Day (O)      = L - 14
Fertile Window Start   = max(O - 3, P + 1)
Fertile Window End     = O + 1
Late Luteal Start      = L - 6
```

### Phase Mapping Table

| Phase | Day Range (28-Day Cycle) | Hormonal Context | App Layout |
| :--- | :--- | :--- | :--- |
| **Menstruation** | Day $1 \dots P$ | Estrogen & progesterone at baseline | `CYCLE_RING` |
| **Follicular** | Day $(P + 1) \dots (\text{FertileStart} - 1)$ | Estrogen rising; energy & focus climbing | `CYCLE_RING` |
| **Fertile Window** | Day $\text{FertileStart} \dots \text{FertileEnd}$ | LH surge & Ovulation peak | `FERTILITY_DASHBOARD` |
| **Luteal** | Day $(\text{FertileEnd} + 1) \dots (\text{LateLutealStart} - 1)$ | Progesterone dominant | `CYCLE_RING` |
| **Late Luteal** | Day $\text{LateLutealStart} \dots L$ | Progesterone tapering; PMS symptoms | `SYMPTOM_GRID` |

---

## 🏛 Architecture & Tech Stack

Lumi is built using **Clean Architecture** and the **MVI (Model-View-Intent)** presentation pattern.

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  Compose UI  ◄──  UI StateFlow  ◄──  MVI ViewModel          │
│                      Actions / Events                       │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                       Domain Layer                          │
│     Typed Result Wrappers, Business Models, Phase Logic     │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                        Data Layer                           │
│  CycleRepository  │  DailyLogRepository  │  BbtRepository   │
│  UserProfileDao   │  CycleDao            │  DailyLogDao     │
│                   Room SQLite Local Database                │
└─────────────────────────────────────────────────────────────┘
```

### Key Technologies
- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose (Material 3)
- **Typography**: Literata (Serif Headings) & Manrope (Body/Data)
- **Dependency Injection**: Koin 4.0 (`singleOf`, `viewModelOf`)
- **Persistence**: Room Database (2.7+ SQLite with Kotlin Flow)
- **Asynchronous / Reactive**: Kotlin Coroutines & StateFlow
- **Navigation**: Type-Safe Navigation Compose 2.8+
- **Background Work**: Android WorkManager & AlarmManager
- **Serialization**: Kotlinx Serialization JSON

---

## 📁 Project Directory Structure

```
app/src/main/java/com/nebulatech/lumi/
├── calendar/               # Calendar Screen, monthly grid, day cell renderers
├── core/                   # Result<T, E> wrapper, ObserveAsEvents, shared utilities
├── data/
│   ├── local/              # Room database, Entities, and DAOs
│   ├── mapper/             # Entity <-> Domain mappers
│   ├── model/              # Domain models (Cycle, DailyLog, UserProfile, Enums)
│   └── repository/         # RoomCycleRepository, RoomDailyLogRepository, etc.
├── di/                     # Koin DI Modules (DatabaseModule, ViewModelModule)
├── home/                   # Home Screen MVI, 3 Layout Variants, Cycle Ring
├── insights/               # Cycle History, 30-Day Symptom Trends, Insights engine
├── logging/                # Bottom Sheets for Flow, BBT, and LH strip logging
├── onboarding/             # Step-by-step onboarding flow & manual past cycle modal
├── profile/                # User profile, notifications, feedback & security
├── security/               # BiometricAuthenticator and encryption helpers
└── ui/theme/               # Color palette, Literata/Manrope typography, Theme
```

---

## 🔒 Security & Privacy

1. **Local-First Storage**: User health data is stored strictly in the device's sandboxed SQLite Room database.
2. **Biometric Protection**: Built-in `BiometricAuthenticator` using AndroidX Biometrics.
3. **Zero Analytics on Health Data**: No cycle information is shared with third parties or advertising networks.

---

## 🚀 CI/CD & Release Pipeline

Lumi features an automated **GitHub Actions CI/CD Pipeline** (`.github/workflows/release.yml`):

- **Automatic Trigger**: Triggers automatically on git tags matching `v*` (e.g. `v1.0.1`, `v1.0.2`).
- **Test Gate**: Runs all unit tests before compiling release artifacts.
- **Release Build**: Executes `./gradlew assembleRelease` with ProGuard/R8 minification and resource shrinking.
- **Native APK Signing**: Automatically aligns and signs the APK using `$ANDROID_HOME` `zipalign` and `apksigner`.
- **Automated GitHub Release**: Publishes a GitHub Release and attaches the signed `.apk` and ProGuard `mapping.txt`.

---

## 💻 Getting Started

### Prerequisites
- **Android Studio**: Meerkat (2024.3+) or Ladybug (2024.2+)
- **JDK**: Java 21
- **Android SDK**: Compile SDK 37, Min SDK 24, Target SDK 36

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/aselahemantha/Lumi.git
cd Lumi

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease
```

---

## 🧪 Running Automated Tests

Lumi includes a comprehensive test suite verifying the mathematical accuracy of cycle calculations, historical rolling averages, and phase transitions:

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific period calculation verification test
./gradlew testDebugUnitTest --tests "com.nebulatech.lumi.PeriodCalculationTest"
```

---

## 🗺️ Roadmap

- **Wear OS Companion App**: Quick logging and phase viewing directly from your wrist.
- **PDF Export**: Generate monthly health reports to share with healthcare providers.
- **Dark Mode Optimizations**: Refined true-black themes for OLED displays.
- **Extended Wearable Integrations**: Sync with more Bluetooth basal thermometers and health bands.
- **Advanced Pregnancy Mode**: Transition seamlessly to pregnancy tracking without losing cycle history.

---

## 🤝 Contributing

We welcome contributions from the community! Whether you want to fix a bug, improve documentation, or propose a new feature, your help is appreciated.

1. **Fork the Repository**: Click the 'Fork' button at the top right of this page.
2. **Clone your Fork**: `git clone https://github.com/YOUR_USERNAME/Lumi.git`
3. **Create a Branch**: `git checkout -b feature/your-feature-name`
4. **Make your Changes**: Write code, add tests, and update documentation as needed.
5. **Commit**: `git commit -m "Add some feature"` (Please use descriptive commit messages).
6. **Push to your Branch**: `git push origin feature/your-feature-name`
7. **Submit a Pull Request**: Go to the original repository and click 'New Pull Request'.

Please make sure to run all tests (`./gradlew testDebugUnitTest`) before submitting a PR.

---

## 📬 Support & Contact

If you have any questions, encounter issues, or have feature requests, please check the [Issues](https://github.com/aselahemantha/Lumi/issues) page first. If your issue isn't listed, feel free to open a new one.

For direct contact, you can reach out via email or through our community channels.

---

## 🙌 Acknowledgements

A special thanks to the amazing open-source community and the creators of the libraries that power Lumi:
- **[Kotlin](https://kotlinlang.org/)**
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)**
- **[Koin](https://insert-koin.io/)**
- **[Room](https://developer.android.com/training/data-storage/room)**
- **[Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)**

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
