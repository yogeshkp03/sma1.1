# Smart Meal Autopilot Backend

A Spring Boot backend API for the Smart Meal Autopilot (SMA) food ordering application.

## Features

- User authentication (Firebase/JWT)
- Restaurant and menu management
- Shopping cart functionality
- Smart Meal Autopilot (SMA) preferences
- AI-powered meal recommendations (Google Gemini)
- Scheduled automation for meal ordering
- Delhi NCR restaurant network (50 locations)

## Tech Stack

- Spring Boot 3.3
- PostgreSQL
- JWT Authentication
- Google Gemini API
- Spring Scheduler

## Setup

### Prerequisites

- Java 17+
- PostgreSQL
- Maven

### Database Configuration

Create a PostgreSQL database named `sma_db`:

```sql
CREATE DATABASE sma_db;
```

Update `src/main/resources/application.yaml` with your PostgreSQL credentials.

### Running the Application

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or run the JAR
java -jar target/sma-backend-1.0.0.jar
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login/Register with Firebase token

### Restaurants
- `GET /api/restaurants` - List all restaurants
- `GET /api/restaurants/{id}` - Get restaurant details
- `GET /api/restaurants/locations` - List all locations

### Menu
- `GET /api/menu/restaurant/{id}` - Get menu by restaurant
- `GET /api/menu/search` - Search menu items with filters

### Cart
- `GET /api/cart/{userId}` - Get user's cart
- `POST /api/cart/{userId}/add` - Add item to cart
- `POST /api/cart/{userId}/checkout` - Place order

### SMA Preferences
- `POST /api/sma/preferences/user/{userId}` - Create SMA preference
- `GET /api/sma/preferences/user/{userId}/active` - Get active preferences
- `PUT /api/sma/preferences/{id}/user/{userId}` - Update preference
- `DELETE /api/sma/preferences/{id}/user/{userId}` - Delete preference

## SMA Feature

The Smart Meal Autopilot (SMA) feature allows users to:
1. Set meal preferences (breakfast, lunch, dinner, snacks)
2. Define nutritional requirements (calories, protein, carbs, fat)
3. Set budget limits
4. Choose diet type (veg, non-veg, eggitarian, vegan)
5. Automate meal ordering 1 hour before scheduled time

## License

MIT
