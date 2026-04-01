package com.smartmeal.repository;

import com.smartmeal.model.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {
    List<RecommendationHistory> findByUserIdOrderByRecommendedAtDesc(Long userId);
    List<RecommendationHistory> findTop10ByUserIdOrderByRecommendedAtDesc(Long userId);
}
