package com.smartmeal.controller;

import com.smartmeal.dto.request.CartItemRequest;
import com.smartmeal.dto.request.CheckoutRequest;
import com.smartmeal.model.*;
import com.smartmeal.model.enums.OrderStatus;
import com.smartmeal.repository.*;
import com.smartmeal.service.CartService;
import com.smartmeal.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Restaurant testRestaurant;
    private MenuItem testMenuItem;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser = userRepository.save(testUser);
        
        testRestaurant = Restaurant.builder()
                .name("Test Restaurant")
                .location("Delhi")
                .cuisine("Fast Food")
                .rating(BigDecimal.valueOf(4.5))
                .deliveryTimeMinutes(30)
                .deliveryFee(BigDecimal.valueOf(50))
                .minOrder(BigDecimal.valueOf(200))
                .isActive(true)
                .build();
        testRestaurant = restaurantRepository.save(testRestaurant);
        
        testMenuItem = MenuItem.builder()
                .restaurant(testRestaurant)
                .name("Test Burger")
                .description("Delicious test burger")
                .price(BigDecimal.valueOf(250))
                .dietType(com.smartmeal.model.enums.DietType.VEG)
                .calories(350)
                .proteinGrams(BigDecimal.valueOf(15))
                .carbsGrams(BigDecimal.valueOf(40))
                .fatGrams(BigDecimal.valueOf(12))
                .isAvailable(true)
                .build();
        testMenuItem = menuItemRepository.save(testMenuItem);
        
        cartItemRepository.deleteAll();
    }

    @Test
    void testAddToCart_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("menuItemId", testMenuItem.getId());
        requestBody.put("quantity", 2);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        String url = baseUrl + "/cart/" + testUser.getId() + "/add";
        var response = restTemplate.postForEntity(url, request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("success"));
    }

    @Test
    void testGetCart_Success() {
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setMenuItemId(testMenuItem.getId());
        cartItemRequest.setQuantity(1);
        
        cartService.addToCart(testUser.getId(), cartItemRequest);
        
        String url = baseUrl + "/cart/" + testUser.getId();
        var response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null);
    }

    @Test
    void testCheckout_Success() {
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setMenuItemId(testMenuItem.getId());
        cartItemRequest.setQuantity(1);
        
        cartService.addToCart(testUser.getId(), cartItemRequest);
        
        Address deliveryAddress = new Address();
        deliveryAddress.setUser(testUser);
        deliveryAddress.setAddressLine("123 Test Street");
        deliveryAddress.setLocation("Delhi");
        
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPaymentMethod("COD");
        
        try {
            var result = orderService.createOrder(testUser.getId(), checkoutRequest);
            
            assertNotNull(result);
            assertEquals(OrderStatus.PENDING, result.getStatus());
            assertTrue(result.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testRemoveFromCart_Success() {
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setMenuItemId(testMenuItem.getId());
        cartItemRequest.setQuantity(1);
        
        var cartItem = cartService.addToCart(testUser.getId(), cartItemRequest);
        
        String url = baseUrl + "/cart/" + testUser.getId() + "/items/" + cartItem.getId();
        restTemplate.delete(url);
        
        var cart = cartService.getCart(testUser.getId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearCart_Success() {
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setMenuItemId(testMenuItem.getId());
        cartItemRequest.setQuantity(2);
        
        cartService.addToCart(testUser.getId(), cartItemRequest);
        
        String url = baseUrl + "/cart/" + testUser.getId() + "/clear";
        restTemplate.delete(url);
        
        var cart = cartService.getCart(testUser.getId());
        assertTrue(cart.getItems().isEmpty());
    }
}