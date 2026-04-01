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

## Quick Start

### Backend
```bash
cd backend
# Configure PostgreSQL in application.yaml
mvn spring-boot:run
```

### Flutter App
```bash
cd sma_app
flutter pub get
flutter run
```
