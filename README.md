# SMA Project Structure

```
sma/
├── backend/                 # Spring Boot Backend
│   ├── src/main/java/com/smartmeal/
│   │   ├── config/        # Configuration classes
│   │   ├── controller/    # REST controllers
│   │   ├── dto/           # Data transfer objects
│   │   ├── exception/     # Exception handlers
│   │   ├── model/         # Entity models
│   │   ├── repository/    # JPA repositories
│   │   ├── scheduler/     # Scheduled tasks
│   │   └── service/       # Business logic
│   ├── src/main/resources/
│   │   └── application.yaml
│   └── pom.xml
│
├── sma_app/                # Flutter Mobile App
│   ├── lib/
│   │   ├── main.dart
│   │   ├── core/          # Constants, theme, utils
│   │   ├── data/          # Models, services, repositories
│   │   ├── presentation/   # Screens, widgets
│   │   └── providers/      # State management
│   ├── android/
│   ├── ios/
│   └── pubspec.yaml
│
└── README.md               # This file
```

## Features

- Smart Meal Autopilot (SMA) - Automated meal recommendations
- **Local Recommendation Engine** - No external API required
- Diet-based filtering (Vegetarian, Non-Veg, Eggetarian, Vegan)
- Budget and nutritional constraint enforcement
- User history influence with recency decay
- Restaurant affinity tracking
- Scheduled meal automation

## Quick Start

### Backend
```bash
cd backend
# Uses H2 in-memory database by default
mvn spring-boot:run
```

### Flutter App
```bash
cd sma_app
flutter pub get
flutter run
```
