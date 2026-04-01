package com.smartmeal.repository;

import com.smartmeal.model.CartItem;
import com.smartmeal.model.MenuItem;
import com.smartmeal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUser(User user);
    
    List<CartItem> findByUserId(Long userId);
    
    Optional<CartItem> findByUserAndMenuItem(User user, MenuItem menuItem);
    
    Optional<CartItem> findByUserIdAndMenuItemId(Long userId, Long menuItemId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
