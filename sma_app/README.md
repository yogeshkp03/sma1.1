# SMA - Smart Meal Autopilot

A Flutter app for AI-powered meal ordering with automated meal preferences.

## Features

- 🔐 Firebase Authentication (Email/Password + Google Sign-In)
- 🍽️ Browse restaurants in Delhi NCR
- 🛒 Shopping cart functionality
- ⚡ Smart Meal Autopilot (SMA) - Set once, eat automatically
- 🤖 AI-powered meal recommendations (Google Gemini)
- 📍 Location-based restaurant search

## Getting Started

### Prerequisites

- Flutter SDK 3.0+
- Dart 3.0+
- Firebase Project
- PostgreSQL Database

### Setup

1. **Clone the repository**
```bash
cd sma_app
```

2. **Install dependencies**
```bash
flutter pub get
```

3. **Configure Firebase**
- Create a Firebase project
- Add `google-services.json` (Android) and `GoogleService-Info.plist` (iOS)
- Enable Authentication with Email/Password and Google providers

4. **Configure Backend**
- Update `lib/core/constants/api_constants.dart` with your backend URL
- Backend setup instructions in `backend/README.md`

5. **Run the app**
```bash
flutter run
```

## Project Structure

```
sma_app/
├── lib/
│   ├── main.dart
│   ├── core/
│   │   ├── constants/    # API endpoints, colors, themes
│   │   ├── theme/       # App theme configuration
│   │   └── utils/       # Validators
│   ├── data/
│   │   ├── models/      # Data models
│   │   ├── repositories/# Data repositories
│   │   └── services/    # API and auth services
│   ├── presentation/
│   │   ├── screens/     # UI screens
│   │   └── widgets/     # Reusable widgets
│   └── providers/       # State management
├── android/
├── ios/
└── pubspec.yaml
```

## SMA Feature

The Smart Meal Autopilot allows users to:
1. Set meal preferences (Morning Fuel, Power Hour, Twilight Feast, Crave Corner)
2. Define nutritional requirements (calories, protein, carbs, fat)
3. Set budget limits
4. Choose diet type (Veg, Non-Veg, Eggetarian, Vegan)
5. Automate meal ordering 1 hour before scheduled time

## Tech Stack

- Flutter 3.x
- Firebase Auth
- Provider (State Management)
- Dio (HTTP Client)
- Google Fonts
- Cached Network Image

## Delhi NCR Locations

- Saket
- Connaught Place
- Hauz Khas
- Dwarka
- Rajouri Garden
- Vasant Kunj
- Lajpat Nagar
- Karol Bagh
- Pitampura
- Shalimar Bagh

## License

MIT
