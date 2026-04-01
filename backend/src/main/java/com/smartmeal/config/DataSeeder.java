package com.smartmeal.config;

import com.smartmeal.model.MenuItem;
import com.smartmeal.model.Restaurant;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MacroLevel;
import com.smartmeal.repository.MenuItemRepository;
import com.smartmeal.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    
    public DataSeeder(RestaurantRepository restaurantRepository, 
                     MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }
    
    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            logger.info("Data already seeded, skipping...");
            return;
        }
        
        logger.info("Seeding database with Delhi restaurants and menu items...");
        
        List<Restaurant> restaurants = createRestaurants();
        restaurantRepository.saveAll(restaurants);
        
        List<MenuItem> allMenuItems = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            List<MenuItem> menuItems = createMenuItems(restaurant);
            allMenuItems.addAll(menuItems);
        }
        menuItemRepository.saveAll(allMenuItems);
        
        logger.info("Successfully seeded {} restaurants and {} menu items", 
                restaurants.size(), allMenuItems.size());
    }
    
    private List<Restaurant> createRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        
        restaurants.add(createRestaurant("McDonald's", "Saket", "Fast Food", 
            "American fast food chain known for burgers and fries", 4.2, 25, 40.00, 150.00));
        restaurants.add(createRestaurant("KFC", "Saket", "Fast Food", 
            "Finger lickin' good fried chicken", 4.1, 30, 50.00, 200.00));
        restaurants.add(createRestaurant("Domino's Pizza", "Saket", "Pizza", 
            "Hot and fresh pizza delivered to your door", 4.0, 35, 30.00, 200.00));
        restaurants.add(createRestaurant("Haldiram's", "Saket", "Indian Snacks", 
            "Traditional Indian sweets and snacks", 4.3, 20, 20.00, 100.00));
        restaurants.add(createRestaurant("Bikanervala", "Saket", "North Indian", 
            "Pure vegetarian Indian cuisine", 4.2, 25, 30.00, 150.00));
        
        restaurants.add(createRestaurant("Pizza Hut", "Connaught Place", "Pizza", 
            "Family pizza restaurant chain", 4.0, 30, 40.00, 250.00));
        restaurants.add(createRestaurant("Madhuban", "Connaught Place", "Vegetarian", 
            "Fine dining vegetarian restaurant", 4.4, 35, 50.00, 300.00));
        restaurants.add(createRestaurant("Azad Hind", "Connaught Place", "North Indian", 
            "Authentic North Indian cuisine", 4.1, 30, 40.00, 200.00));
        restaurants.add(createRestaurant("United Pizza", "Connaught Place", "Pizza", 
            "Gourmet pizzas with unique toppings", 4.2, 25, 30.00, 200.00));
        restaurants.add(createRestaurant("Burger King", "Connaught Place", "Fast Food", 
            "Flame-grilled burgers since 1954", 4.0, 25, 35.00, 150.00));
        
        restaurants.add(createRestaurant("Hauz Khas Social", "Hauz Khas", "Multi-cuisine", 
            "Trendy bar and restaurant with modern Indian menu", 4.3, 40, 60.00, 400.00));
        restaurants.add(createRestaurant("Kylin", "Hauz Khas", "Asian", 
            "Premium Asian dining experience", 4.2, 35, 50.00, 350.00));
        restaurants.add(createRestaurant("The Boat House", "Hauz Khas", "Continental", 
            "European cuisine with a scenic view", 4.4, 45, 70.00, 500.00));
        restaurants.add(createRestaurant("Cafe Lota", "Hauz Khas", "Indian", 
            "Contemporary Indian snacks and beverages", 4.5, 20, 30.00, 200.00));
        restaurants.add(createRestaurant("Mainland China", "Hauz Khas", "Chinese", 
            "Authentic Chinese flavors", 4.1, 35, 50.00, 400.00));
        
        restaurants.add(createRestaurant("Dwarka Dhaba", "Dwarka", "North Indian", 
            "Local Punjabi flavors at its best", 4.0, 30, 25.00, 150.00));
        restaurants.add(createRestaurant("Mahesh Lunch Home", "Dwarka", "South Indian", 
            "Famous for dosas and south Indian thalis", 4.3, 25, 30.00, 200.00));
        restaurants.add(createRestaurant("Ghee Hind", "Dwarka", "North Indian", 
            "Pure ghee preparations and north indian classics", 4.2, 35, 40.00, 250.00));
        restaurants.add(createRestaurant("Sardarji's", "Dwarka", "Punjabi", 
            "Legendary fish fry and Punjabi food", 4.1, 30, 35.00, 200.00));
        restaurants.add(createRestaurant("The Hunger Spot", "Dwarka", "Multi-cuisine", 
            "All-in-one multi-cuisine restaurant", 4.0, 25, 30.00, 180.00));
        
        restaurants.add(createRestaurant("Rajouri Garden Rolls", "Rajouri Garden", "Rolls", 
            "Famous for paratha rolls and wraps", 4.2, 20, 25.00, 100.00));
        restaurants.add(createRestaurant("Kulshan", "Rajouri Garden", "Multi-cuisine", 
            "Vegetarian multi-cuisine dining", 4.1, 30, 35.00, 200.00));
        restaurants.add(createRestaurant("Pind Balluchi", "Rajouri Garden", "Punjabi", 
            "Rural Punjabi village-themed restaurant", 4.3, 40, 50.00, 350.00));
        restaurants.add(createRestaurant("Sagar Ratna", "Rajouri Garden", "South Indian", 
            "South Indian vegetarian specialties", 4.2, 25, 30.00, 180.00));
        restaurants.add(createRestaurant("Bade Miyan", "Rajouri Garden", "Mughlai", 
            "Authentic Mughlai and tandoori cuisine", 4.1, 35, 40.00, 300.00));
        
        restaurants.add(createRestaurant("Vasant Kunj Food Court", "Vasant Kunj", "Multi-cuisine", 
            "Multiple cuisines under one roof", 4.0, 25, 30.00, 200.00));
        restaurants.add(createRestaurant("Dhaba Estd 1986", "Vasant Kunj", "North Indian", 
            "Highway dhaba style food experience", 4.4, 35, 45.00, 300.00));
        restaurants.add(createRestaurant("California Pizza", "Vasant Kunj", "Pizza", 
            "International pizza chain with unique flavors", 4.1, 30, 40.00, 250.00));
        restaurants.add(createRestaurant("Baba Chicken", "Vasant Kunj", "Mughlai", 
            "Famous for butter chicken and kebabs", 4.3, 35, 50.00, 350.00));
        restaurants.add(createRestaurant("Kwality Walls", "Vasant Kunj", "Desserts", 
            "Premium ice creams and desserts", 4.2, 15, 20.00, 150.00));
        
        restaurants.add(createRestaurant("Lajpat Nagar Chaat", "Lajpat Nagar", "Street Food", 
            "Famous for authentic Delhi chaats", 4.4, 20, 25.00, 100.00));
        restaurants.add(createRestaurant("Paranthe Wali Gali", "Lajpat Nagar", "North Indian", 
            "Legendary stuffed parathas since generations", 4.5, 25, 30.00, 150.00));
        restaurants.add(createRestaurant("Sagar Ratna", "Lajpat Nagar", "South Indian", 
            "Popular for south indian breakfast items", 4.2, 25, 30.00, 200.00));
        restaurants.add(createRestaurant("Om Sweets", "Lajpat Nagar", "Vegetarian", 
            "Pure vegetarian meals and sweets", 4.1, 20, 25.00, 150.00));
        restaurants.add(createRestaurant("Nandos", "Lajpat Nagar", "Chicken", 
            "Flame-grilled peri-peri chicken", 4.2, 30, 40.00, 300.00));
        
        restaurants.add(createRestaurant("Karol Bagh Tiffin", "Karol Bagh", "North Indian", 
            "Home-style north indian meals", 4.0, 25, 30.00, 150.00));
        restaurants.add(createRestaurant("Jai Jawan", "Karol Bagh", "Multi-cuisine", 
            "Multi-cuisine restaurant with Indian and Chinese", 4.1, 30, 35.00, 200.00));
        restaurants.add(createRestaurant("Deepak", "Karol Bagh", "South Indian", 
            "South Indian vegetarian specialities", 4.2, 25, 30.00, 180.00));
        restaurants.add(createRestaurant("Kesar", "Karol Bagh", "Vegetarian", 
            "Pure vegetarian restaurant with thalis", 4.1, 25, 30.00, 200.00));
        restaurants.add(createRestaurant("Mithaas", "Karol Bagh", "Sweets", 
            "Traditional Indian sweets and snacks", 4.3, 20, 25.00, 150.00));
        
        restaurants.add(createRestaurant("Pitampura Ke Paranthe", "Pitampura", "North Indian", 
            "Famous for stuffed parathas and lassi", 4.2, 20, 25.00, 120.00));
        restaurants.add(createRestaurant("Haldiram's", "Pitampura", "Snacks", 
            "Famous for namkeen and sweets", 4.1, 20, 20.00, 100.00));
        restaurants.add(createRestaurant("KFC", "Pitampura", "Fast Food", 
            "Crispy fried chicken and more", 4.0, 30, 40.00, 200.00));
        restaurants.add(createRestaurant("Pizaa Mania", "Pitampura", "Pizza", 
            "Fresh and cheesy pizzas", 4.1, 25, 30.00, 180.00));
        restaurants.add(createRestaurant("Shagun", "Pitampura", "Multi-cuisine", 
            "Family restaurant with multiple cuisines", 4.2, 30, 35.00, 250.00));
        
        restaurants.add(createRestaurant("Shalimar", "Shalimar Bagh", "Mughlai", 
            "Legendary biryani and kebabs", 4.3, 35, 40.00, 300.00));
        restaurants.add(createRestaurant("NCR Dhaba", "Shalimar Bagh", "North Indian", 
            "Authentic dhaba-style cooking", 4.2, 30, 35.00, 200.00));
        restaurants.add(createRestaurant("Burger King", "Shalimar Bagh", "Fast Food", 
            "Flame-grilled burgers", 4.0, 25, 35.00, 150.00));
        restaurants.add(createRestaurant("Nirula's", "Shalimar Bagh", "Ice Cream", 
            "Famous for ice cream and fast food", 4.2, 20, 25.00, 150.00));
        restaurants.add(createRestaurant("Pind Bistro", "Shalimar Bagh", "Multi-cuisine", 
            "Modern bistro with Indian and continental", 4.1, 35, 40.00, 280.00));
        
        return restaurants;
    }
    
    private Restaurant createRestaurant(String name, String location, String cuisine, 
                                       String description, double rating, int deliveryTime,
                                       double deliveryFee, double minOrder) {
        String imageUrl = getRestaurantImage(name, cuisine);
        
        return Restaurant.builder()
                .name(name)
                .location(location)
                .cuisine(cuisine)
                .description(description)
                .imageUrl(imageUrl)
                .rating(BigDecimal.valueOf(rating))
                .deliveryTimeMinutes(deliveryTime)
                .deliveryFee(BigDecimal.valueOf(deliveryFee))
                .minOrder(BigDecimal.valueOf(minOrder))
                .address(location + ", New Delhi")
                .isActive(true)
                .build();
    }
    
    private String getRestaurantImage(String name, String cuisine) {
        String n = name.toLowerCase();
        String c = cuisine.toLowerCase();
        
        if (n.contains("mcdonald") || n.contains("burger king") || n.contains("kfc")) {
            return "https://images.unsplash.com/photo-1567244589813-2d875c320940?w=400&h=300&fit=crop";
        }
        if (n.contains("domino") || n.contains("pizza hut") || n.contains("pizza")) {
            return "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop";
        }
        if (n.contains("haldiram") || n.contains("bikanervala") || n.contains("sweets")) {
            return "https://images.unsplash.com/photo-1597362925123-77861d3fbac7?w=400&h=300&fit=crop";
        }
        if (n.contains("dosa") || n.contains("sagar ratna") || n.contains("south indian")) {
            return "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=400&h=300&fit=crop";
        }
        if (n.contains("biryani") || n.contains("shalimar") || n.contains("mughlai") || n.contains("azad")) {
            return "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=400&h=300&fit=crop";
        }
        if (n.contains("dhaba") || n.contains("punjabi")) {
            return "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400&h=300&fit=crop";
        }
        if (n.contains("chaat") || n.contains("paranthe")) {
            return "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400&h=300&fit=crop";
        }
        if (n.contains("kylin") || n.contains("china") || n.contains("asian") || n.contains("mainland")) {
            return "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=400&h=300&fit=crop";
        }
        if (n.contains("social") || n.contains("cafe") || n.contains("lota")) {
            return "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop";
        }
        if (n.contains("boat") || n.contains("continental")) {
            return "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=300&fit=crop";
        }
        
        if (c.contains("pizza")) {
            return "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop";
        }
        if (c.contains("fast food") || c.contains("burger")) {
            return "https://images.unsplash.com/photo-1567244589813-2d875c320940?w=400&h=300&fit=crop";
        }
        if (c.contains("indian") || c.contains("north") || c.contains("punjabi")) {
            return "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=400&h=300&fit=crop";
        }
        if (c.contains("south")) {
            return "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=400&h=300&fit=crop";
        }
        if (c.contains("chinese") || c.contains("asian")) {
            return "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=400&h=300&fit=crop";
        }
        if (c.contains("vegetarian") || c.contains("sweets")) {
            return "https://images.unsplash.com/photo-1597362925123-77861d3fbac7?w=400&h=300&fit=crop";
        }
        
        return "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop";
    }
    
    private List<MenuItem> createMenuItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        String cuisine = restaurant.getCuisine();
        
        if ("Fast Food".equals(cuisine) || "Pizza".equals(cuisine)) {
            items.addAll(createFastFoodItems(restaurant));
        } else if ("North Indian".equals(cuisine) || "Punjabi".equals(cuisine) || "Mughlai".equals(cuisine)) {
            items.addAll(createNorthIndianItems(restaurant));
        } else if ("South Indian".equals(cuisine)) {
            items.addAll(createSouthIndianItems(restaurant));
        } else if ("Vegetarian".equals(cuisine)) {
            items.addAll(createVegetarianItems(restaurant));
        } else if ("Chinese".equals(cuisine) || "Asian".equals(cuisine)) {
            items.addAll(createAsianItems(restaurant));
        } else {
            items.addAll(createMultiCuisineItems(restaurant));
        }
        
        return items;
    }
    
    private List<MenuItem> createFastFoodItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Veg Burger", "Classic veggie burger with lettuce and tomato", 
            150.00, DietType.VEG, 350, 12.0, 45.0, 15.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Chicken Burger", "Grilled chicken patty with mayo", 
            180.00, DietType.NON_VEG, 420, 25.0, 40.0, 18.0, "Main Course"));
        items.add(createMenuItem(restaurant, "French Fries", "Crispy golden fries with seasoning", 
            99.00, DietType.VEG, 280, 4.0, 35.0, 14.0, "Starters"));
        items.add(createMenuItem(restaurant, "Coleslaw", "Creamy coleslaw salad", 
            79.00, DietType.VEG, 150, 2.0, 10.0, 12.0, "Starters"));
        items.add(createMenuItem(restaurant, "Chicken Wings (6 pcs)", "Crispy fried chicken wings", 
            249.00, DietType.NON_VEG, 450, 28.0, 8.0, 25.0, "Starters"));
        items.add(createMenuItem(restaurant, "Margherita Pizza", "Classic cheese and tomato pizza", 
            299.00, DietType.VEG, 850, 22.0, 95.0, 32.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Pepperoni Pizza", "Pizza with pepperoni toppings", 
            399.00, DietType.NON_VEG, 980, 35.0, 90.0, 38.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Cold Coffee", "Chilled coffee with ice cream", 
            149.00, DietType.VEG, 180, 5.0, 25.0, 8.0, "Beverages"));
        items.add(createMenuItem(restaurant, "Chocolate Brownie", "Rich chocolate brownie", 
            179.00, DietType.VEG, 380, 6.0, 45.0, 20.0, "Desserts"));
        items.add(createMenuItem(restaurant, "Chicken Nuggets (8 pcs)", "Crispy nugget bites", 
            199.00, DietType.NON_VEG, 380, 18.0, 25.0, 22.0, "Starters"));
        
        return items;
    }
    
    private List<MenuItem> createNorthIndianItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Paneer Butter Masala", "Creamy tomato gravy with paneer cubes", 
            280.00, DietType.VEG, 450, 18.0, 25.0, 32.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Butter Chicken", "Creamy tomato-based chicken curry", 
            320.00, DietType.NON_VEG, 520, 32.0, 20.0, 38.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Dal Makhani", "Creamy black lentils slow cooked", 
            220.00, DietType.VEG, 380, 15.0, 40.0, 18.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Tandoori Roti", "Whole wheat flatbread from clay oven", 
            40.00, DietType.VEG, 120, 4.0, 20.0, 2.0, "Breads"));
        items.add(createMenuItem(restaurant, "Chicken Biryani", "Aromatic rice with spiced chicken", 
            350.00, DietType.NON_VEG, 580, 28.0, 65.0, 22.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Paneer Tikka", "Grilled paneer marinated in spices", 
            260.00, DietType.VEG, 380, 22.0, 15.0, 28.0, "Starters"));
        items.add(createMenuItem(restaurant, "Seekh Kebab", "Minced meat kebabs grilled on skewer", 
            300.00, DietType.NON_VEG, 350, 25.0, 8.0, 24.0, "Starters"));
        items.add(createMenuItem(restaurant, "Lassi (Sweet)", "Thick yogurt-based sweet drink", 
            80.00, DietType.VEG, 180, 6.0, 25.0, 6.0, "Beverages"));
        items.add(createMenuItem(restaurant, "Gulab Jamun", "Deep-fried milk balls in sugar syrup", 
            120.00, DietType.VEG, 280, 5.0, 40.0, 12.0, "Desserts"));
        items.add(createMenuItem(restaurant, "Naan", "Leavened flatbread from tandoor", 
            50.00, DietType.VEG, 180, 5.0, 25.0, 6.0, "Breads"));
        
        return items;
    }
    
    private List<MenuItem> createSouthIndianItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Masala Dosa", "Crispy crepe with spiced potatoes", 
            120.00, DietType.VEG, 320, 8.0, 50.0, 12.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Idli Sambar", "Steamed rice cakes with lentil soup", 
            100.00, DietType.VEG, 250, 7.0, 40.0, 5.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Uttapam", "Thick pancake topped with vegetables", 
            110.00, DietType.VEG, 280, 6.0, 45.0, 8.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Medu Vada", "Crispy lentil fritters", 
            90.00, DietType.VEG, 220, 8.0, 25.0, 10.0, "Starters"));
        items.add(createMenuItem(restaurant, "Chicken Chettinad", "Spicy chicken curry from Tamil Nadu", 
            280.00, DietType.NON_VEG, 380, 28.0, 15.0, 22.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Rava Kesari", "Semolina dessert with saffron", 
            80.00, DietType.VEG, 200, 3.0, 35.0, 6.0, "Desserts"));
        items.add(createMenuItem(restaurant, "Coconut Chutney", "Fresh coconut chutney", 
            40.00, DietType.VEG, 60, 2.0, 5.0, 4.0, "Starters"));
        items.add(createMenuItem(restaurant, "Filter Coffee", "Strong South Indian coffee", 
            60.00, DietType.VEG, 80, 3.0, 8.0, 4.0, "Beverages"));
        
        return items;
    }
    
    private List<MenuItem> createVegetarianItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Mixed Veg Curry", "Seasonal vegetables in tomato gravy", 
            200.00, DietType.VEG, 280, 10.0, 30.0, 14.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Chole Bhature", "Chickpea curry with fried bread", 
            180.00, DietType.VEG, 480, 14.0, 55.0, 22.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Aloo Gobi", "Potato and cauliflower curry", 
            170.00, DietType.VEG, 250, 8.0, 35.0, 10.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Rajma Chawal", "Kidney beans with rice", 
            190.00, DietType.VEG, 420, 15.0, 60.0, 15.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Palak Paneer", "Spinach curry with paneer cubes", 
            240.00, DietType.VEG, 350, 18.0, 20.0, 24.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Samosa (2 pcs)", "Crispy pastry with spiced potatoes", 
            60.00, DietType.VEG, 200, 4.0, 22.0, 10.0, "Starters"));
        items.add(createMenuItem(restaurant, "Kheer", "Rice pudding with cardamom", 
            100.00, DietType.VEG, 220, 6.0, 35.0, 8.0, "Desserts"));
        items.add(createMenuItem(restaurant, "Masala Chaas", "Spiced buttermilk", 
            50.00, DietType.VEG, 80, 4.0, 10.0, 2.0, "Beverages"));
        items.add(createMenuItem(restaurant, "Veg Thali", "Complete meal with multiple dishes", 
            280.00, DietType.VEG, 650, 20.0, 80.0, 28.0, "Main Course"));
        
        return items;
    }
    
    private List<MenuItem> createAsianItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Hakka Noodles", "Wok-tossed noodles with vegetables", 
            220.00, DietType.VEG, 420, 12.0, 55.0, 18.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Chicken Fried Rice", "Wok-tossed rice with chicken", 
            250.00, DietType.NON_VEG, 480, 22.0, 50.0, 20.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Manchurian (Veg)", "Crispy vegetable balls in spicy sauce", 
            200.00, DietType.VEG, 350, 10.0, 40.0, 16.0, "Starters"));
        items.add(createMenuItem(restaurant, "Chicken Manchurian", "Chicken balls in manchurian sauce", 
            280.00, DietType.NON_VEG, 420, 25.0, 30.0, 22.0, "Starters"));
        items.add(createMenuItem(restaurant, "Spring Roll (4 pcs)", "Crispy rolls with vegetable filling", 
            150.00, DietType.VEG, 280, 6.0, 30.0, 14.0, "Starters"));
        items.add(createMenuItem(restaurant, "Sweet and Sour Pork", "Crispy pork in tangy sauce", 
            320.00, DietType.NON_VEG, 480, 20.0, 35.0, 28.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Vegetable Dumpling (6 pcs)", "Steamed dumplings with veg filling", 
            180.00, DietType.VEG, 300, 8.0, 40.0, 12.0, "Starters"));
        items.add(createMenuItem(restaurant, "Hot and Sour Soup", "Spicy and tangy soup", 
            120.00, DietType.VEG, 100, 4.0, 12.0, 4.0, "Beverages"));
        items.add(createMenuItem(restaurant, "Kung Pao Chicken", "Spicy diced chicken with peanuts", 
            300.00, DietType.NON_VEG, 400, 28.0, 20.0, 24.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Mango Custard", "Sweet mango dessert", 
            140.00, DietType.VEG, 180, 4.0, 30.0, 6.0, "Desserts"));
        
        return items;
    }
    
    private List<MenuItem> createMultiCuisineItems(Restaurant restaurant) {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(createMenuItem(restaurant, "Grilled Chicken", "Herb-marinated grilled chicken", 
            320.00, DietType.NON_VEG, 380, 35.0, 5.0, 22.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Pasta Arrabiata", "Penne in spicy tomato sauce", 
            250.00, DietType.VEG, 450, 14.0, 60.0, 18.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Caesar Salad", "Fresh greens with caesar dressing", 
            220.00, DietType.NON_VEG, 280, 12.0, 15.0, 20.0, "Starters"));
        items.add(createMenuItem(restaurant, "Mushroom Risotto", "Creamy Italian rice dish", 
            300.00, DietType.VEG, 520, 12.0, 65.0, 22.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Fish and Chips", "Crispy battered fish with fries", 
            350.00, DietType.NON_VEG, 550, 28.0, 45.0, 28.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Veggie Wrap", "Whole wheat wrap with grilled vegetables", 
            180.00, DietType.VEG, 320, 10.0, 40.0, 14.0, "Main Course"));
        items.add(createMenuItem(restaurant, "Garlic Bread", "Toasted bread with garlic butter", 
            120.00, DietType.VEG, 250, 6.0, 30.0, 12.0, "Starters"));
        items.add(createMenuItem(restaurant, "Chocolate Lava Cake", "Warm chocolate cake with molten center", 
            200.00, DietType.VEG, 400, 8.0, 45.0, 22.0, "Desserts"));
        items.add(createMenuItem(restaurant, "Fresh Lime Soda", "Refreshing citrus drink", 
            80.00, DietType.VEG, 60, 0.0, 15.0, 0.0, "Beverages"));
        items.add(createMenuItem(restaurant, "Paneer Tikka Wrap", "Grilled paneer in whole wheat wrap", 
            220.00, DietType.VEG, 380, 18.0, 35.0, 20.0, "Main Course"));
        
        return items;
    }
    
    private MenuItem createMenuItem(Restaurant restaurant, String name, String description,
                                   double price, DietType dietType, int calories,
                                   double protein, double carbs, double fat, String category) {
        String imageUrl = getFoodImage(name, category);
        
        return MenuItem.builder()
                .restaurant(restaurant)
                .name(name)
                .description(description)
                .price(BigDecimal.valueOf(price))
                .imageUrl(imageUrl)
                .dietType(dietType)
                .calories(calories)
                .proteinGrams(BigDecimal.valueOf(protein))
                .carbsGrams(BigDecimal.valueOf(carbs))
                .fatGrams(BigDecimal.valueOf(fat))
                .carbsLevel(carbs > 50 ? MacroLevel.HIGH : (carbs > 25 ? MacroLevel.MEDIUM : MacroLevel.LOW))
                .fatLevel(fat > 25 ? MacroLevel.HIGH : (fat > 15 ? MacroLevel.MEDIUM : MacroLevel.LOW))
                .category(category)
                .isAvailable(true)
                .build();
    }
    
    private String getFoodImage(String name, String category) {
        String n = name.toLowerCase();
        String c = category != null ? category.toLowerCase() : "";
        
        if (n.contains("burger")) {
            return "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop";
        }
        if (n.contains("pizza")) {
            return "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop";
        }
        if (n.contains("fries") || n.contains("fries")) {
            return "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=300&fit=crop";
        }
        if (n.contains("chicken") && (n.contains("wings") || n.contains("nuggets") || n.contains("fried"))) {
            return "https://images.unsplash.com/photo-1562967914-608f82629710?w=400&h=300&fit=crop";
        }
        if (n.contains("chicken")) {
            return "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=400&h=300&fit=crop";
        }
        if (n.contains("paneer")) {
            return "https://images.unsplash.com/photo-1626820371031-403a651c6dcc?w=400&h=300&fit=crop";
        }
        if (n.contains("biryani") || n.contains("rice")) {
            return "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=400&h=300&fit=crop";
        }
        if (n.contains("dal") || n.contains("lentil")) {
            return "https://images.unsplash.com/photo-1546833999-b9f581a2046c?w=400&h=300&fit=crop";
        }
        if (n.contains("naan") || n.contains("roti") || n.contains("paratha")) {
            return "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400&h=300&fit=crop";
        }
        if (n.contains("dosa")) {
            return "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=400&h=300&fit=crop";
        }
        if (n.contains("idli")) {
            return "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400&h=300&fit=crop";
        }
        if (n.contains("noodles") || n.contains("fried rice")) {
            return "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=400&h=300&fit=crop";
        }
        if (n.contains("manchurian")) {
            return "https://images.unsplash.com/photo-1525755662778-989d0524087e?w=400&h=300&fit=crop";
        }
        if (n.contains("kebab") || n.contains("seekh")) {
            return "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400&h=300&fit=crop";
        }
        if (n.contains("samosa") || n.contains("chaat")) {
            return "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400&h=300&fit=crop";
        }
        if (n.contains("lassi") || n.contains("butter") || n.contains("chaas")) {
            return "https://images.unsplash.com/photo-1527663598678-553a622fe892?w=400&h=300&fit=crop";
        }
        if (n.contains("coffee")) {
            return "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=300&fit=crop";
        }
        if (n.contains("tea")) {
            return "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400&h=300&fit=crop";
        }
        if (n.contains("cold drink") || n.contains("cola") || n.contains("soda")) {
            return "https://images.unsplash.com/photo-1625772299848-391b6a87d7b3?w=400&h=300&fit=crop";
        }
        if (n.contains("ice cream") || n.contains("brownie") || n.contains("dessert") || n.contains("cake") || n.contains("kheer") || n.contains("gulab")) {
            return "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=400&h=300&fit=crop";
        }
        if (n.contains("salad")) {
            return "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop";
        }
        if (n.contains("pasta")) {
            return "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=400&h=300&fit=crop";
        }
        if (n.contains("dumpling") || n.contains("momo")) {
            return "https://images.unsplash.com/photo-1496116218417-1a781b1c423c?w=400&h=300&fit=crop";
        }
        if (n.contains("wrap")) {
            return "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=300&fit=crop";
        }
        
        if (c.contains("main course")) {
            return "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop";
        }
        if (c.contains("starter")) {
            return "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop";
        }
        if (c.contains("beverage") || c.contains("drink")) {
            return "https://images.unsplash.com/photo-1527663598678-553a622fe892?w=400&h=300&fit=crop";
        }
        if (c.contains("dessert")) {
            return "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=400&h=300&fit=crop";
        }
        if (c.contains("bread")) {
            return "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400&h=300&fit=crop";
        }
        
        return "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop";
    }
}
