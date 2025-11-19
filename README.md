![AURORA Project Banner](https://www.aurora-h2020.eu/wp-content/uploads/2022/08/Logo-Website.png)

# AURORA Android App
**[Website](https://www.aurora-h2020.eu/) | [Download App](https://play.google.com/store/apps/details?id=eu.inscico.aurora_app)**

## About the Project

**AURORA** is a pioneering Innovation Action funded by the EU’s **Horizon 2020** programme. Starting in December 2021 with €4.6 million in funding, AURORA aims to demonstrate how ordinary citizens can drive the transition to a near-zero emission society.

The project engages approximately **7,000 citizens** across five locations (Denmark, England, Portugal, Slovenia, and Spain) to become "citizen scientists." These communities are not only reducing their own carbon footprint but are also crowd-funding local **photovoltaic (PV) facilities** to produce ~1 megawatt of renewable energy.

This Android application is a central tool in the AURORA ecosystem, enabling participants to:
*   Monitor their energy-related behaviors (heating, cooling, transport, electricity).
*   Receive tailored suggestions to lower energy demand and costs.
*   Track the impact of their community's renewable energy generation.

## Key Features

*   **Energy Monitoring**: Track personal behavioral patterns in heating, cooling, transport, and electricity usage.
*   **Smart Recommendations**: Get personalized tips to reduce carbon footprint and energy bills.
*   **PV Crowdfunding & Monitoring**: View the status and output of community-funded photovoltaic facilities.
*   **Impact Tracking**: Visualize individual and collective contributions to carbon reduction.
*   **Gamification**: Engage with challenges and achievements to stay motivated.

## Tech Stack

The AURORA Android app is built with modern Android development practices, leveraging **Jetpack Compose** for UI and **Firebase** for backend services.

*   **Language**: [Kotlin](https://kotlinlang.org/) (JVM Target 17)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3 Design)
*   **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
*   **Dependency Injection**: [Koin](https://insert-koin.io/)
*   **Networking**: [Retrofit](https://square.github.io/retrofit/) with [Moshi](https://github.com/square/moshi) for JSON parsing.
*   **Backend / Cloud**:
    *   [Firebase Authentication](https://firebase.google.com/docs/auth)
    *   [Firebase Firestore](https://firebase.google.com/docs/firestore)
    *   [Firebase Cloud Functions](https://firebase.google.com/docs/functions)
    *   [Firebase Storage](https://firebase.google.com/docs/storage)
*   **Charting**: [Vico](https://github.com/patrykandpatrick/vico)
*   **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

## Installation & Setup

### Prerequisites
*   [Android Studio](https://developer.android.com/studio) (Hedgehog or newer recommended)
*   JDK 17
*   Android SDK (minSdk 26, targetSdk 36)

### Getting Started

1.  **Clone the repository**
    ```bash
    git clone https://github.com/AURORA-H2020/AURORA-Android.git
    cd AURORA-Android
    ```

2.  **Firebase Configuration**
    *   This project relies on Firebase. You will need a `google-services.json` file.
    *   Place your `google-services.json` file in the `app/` directory.
    *   *Note: If you are a core developer, ask the project lead for the development environment credentials.*

3.  **Build the Project**
    Open the project in Android Studio and sync Gradle files.
    ```bash
    ./gradlew assembleDebug
    ```

4.  **Run the App**
    Select a device/emulator and click **Run**.

## Project Structure

The project follows a modular structure within the `app` module:

*   `eu.inscico.aurora_app`
    *   `core`: Core utilities and extensions.
    *   `model`: Data classes and domain models.
    *   `services`: Backend integration and business logic services.
    *   `ui`: Jetpack Compose UI components, screens, and themes.
        *   `components`: Reusable UI elements.
        *   `screens`: Feature-specific screens (Home, Login, Photovoltaic, etc.).
        *   `theme`: App theming (Colors, Typography).
    *   `utils`: General helper functions.

## The Project Consortium

The AURORA project is a collaboration between nine institutions across six countries:

*   **Technical University of Madrid** (Spain) - Project Coordinator
*   **Aarhus University** (Denmark)
*   **Centre for Sustainable Energy** (United Kingdom)
*   **Forest of Dean District Council** (United Kingdom)
*   **Institute for Science & Innovation Communication** (Germany)
*   **KempleyGreen Consultants** (United Kingdom)
*   **Qualifying Photovoltaics** (Spain)
*   **University of Ljubljana** (Slovenia)
*   **University of Évora** (Portugal)

## License & Funding

This project is part of the AURORA initiative.

<img src="https://www.aurora-h2020.eu/wp-content/uploads/elementor/thumbs/EU-Flag-psu6pdbcnlpmaljtwxkotmokm7piv22d31neeas0vc.png" width="100" align="left" style="margin-right: 20px;" />

**Funded by the European Union.**
This project has received funding from the European Union’s **Horizon 2020** research and innovation programme under grant agreement No **[101036418](https://cordis.europa.eu/project/id/101036418)**.
