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

    @Query("SELECT new com.example.project.domain.score.ScoreDetailsDTO( " +
            "s.scoreId, s.totalScore, s.motionScore, s.expressionScore, s.languageScore, " +
            "s.motionFrequency, s.expressionFrequency, s.languageFrequency, " +
            "LEAST(s.motionScore, s.expressionScore, s.languageScore), " +
            "CASE " +
            "WHEN s.motionScore <= s.expressionScore AND s.motionScore <= s.languageScore THEN 'motion' " +
            "WHEN s.expressionScore <= s.motionScore AND s.expressionScore <= s.languageScore THEN 'expression' " +
            "WHEN s.languageScore <= s.motionScore AND s.languageScore <= s.expressionScore THEN 'language' " +
            "END, " +
            "CAST(PERCENT_RANK() OVER (ORDER BY s.motionScore ASC) AS DOUBLE) * 100, " +
            "CAST(PERCENT_RANK() OVER (ORDER BY s.expressionScore ASC) AS DOUBLE) * 100, " +
            "CAST(PERCENT_RANK() OVER (ORDER BY s.languageScore ASC) AS DOUBLE) * 100, " +
            "CAST(PERCENT_RANK() OVER (ORDER BY s.totalScore ASC) AS DOUBLE) * 100) " +
            "FROM Score s WHERE s.scoreId = :scoreId")
    Optional<ScoreDetailsDTO> findScoreWithDetailsByScoreId(@Param("scoreId") Long scoreId);



    @Query("SELECT s.motionFrequency, COUNT(s) FROM Score s GROUP BY s.motionFrequency")
    List<Object[]> getMotionFrequencyDistribution();

    @Query("SELECT s.expressionFrequency, COUNT(s) FROM Score s GROUP BY s.expressionFrequency")
    List<Object[]> getExpressionFrequencyDistribution();

    @Query("SELECT s.languageFrequency, COUNT(s) FROM Score s GROUP BY s.languageFrequency")
    List<Object[]> getLanguageFrequencyDistribution();


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-06 09:42 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}