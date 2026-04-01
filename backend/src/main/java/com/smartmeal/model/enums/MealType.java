package com.smartmeal.model.enums;

public enum MealType {
    MORNING_FUEL("Morning Fuel", "Breakfast"),
    POWER_HOUR("Power Hour", "Lunch"),
    TWILIGHT_FEAST("Twilight Feast", "Dinner"),
    CRAVE_CORNER("Crave Corner", "Snacks");

    private final String displayName;
    private final String category;

    MealType(String displayName, String category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }
}
