package com.example.project.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByUserId(Long userId);
    List<Score> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT s FROM Score s " +
            "LEFT JOIN FETCH s.motionTimes " +
            "LEFT JOIN FETCH s.expressionTimes " +
            "LEFT JOIN FETCH s.languageTimes " +
            "WHERE s.user.id = :userId")
    List<Score> findByUserIdWithDetails(@Param("userId") Long userId);

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-06 09:42 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Query("SELECT AVG(s.motionScore) FROM Score s")
    Double getAverageMotionScore();

    @Query("SELECT AVG(s.expressionScore) FROM Score s")
    Double getAverageExpressionScore();

    @Query("SELECT AVG(s.languageScore) FROM Score s")
    Double getAverageLanguageScore();

    @Query("SELECT DATE(s.date), COUNT(s) " +
            "FROM Score s " +
            "WHERE s.date >= :startDate " +
            "GROUP BY DATE(s.date) " +
            "ORDER BY DATE(s.date) ASC")
    List<Object[]> getRecentScoresCount(@Param("startDate") LocalDateTime startDate);

    List<Score> findTop4ByOrderByTotalScoreDesc();

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-06 09:42 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
