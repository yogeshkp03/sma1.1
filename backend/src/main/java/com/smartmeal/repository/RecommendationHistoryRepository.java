package com.smartmeal.repository;

import com.smartmeal.model.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {
    List<RecommendationHistory> findByUserIdOrderByRecommendedAtDesc(Long userId);
    List<RecommendationHistory> findTop10ByUserIdOrderByRecommendedAtDesc(Long userId);
    List<RecommendationHistory> findByUserId(Long userId);
    List<RecommendationHistory> findByUserIdAndRecommendedAtAfter(Long userId, LocalDateTime after);
    List<RecommendationHistory> findByUserIdAndRecommendedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    @Query("SELECT rh.menuItemId, COUNT(rh) FROM RecommendationHistory rh GROUP BY rh.menuItemId ORDER BY COUNT(rh) DESC")
    List<Object[]> getMostRecommendedItems();
}
