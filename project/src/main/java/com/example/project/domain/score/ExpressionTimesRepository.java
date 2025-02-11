package com.example.project.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpressionTimesRepository extends JpaRepository<ExpressionTimes, Long> {
    @Query("SELECT e.expressionTime FROM ExpressionTimes e WHERE e.score.scoreId = :scoreId AND e.expressionName = :expressionName")
    List<Double> findExpressionTimesByScoreIdAndExpressionName(@Param("scoreId") Long scoreId, @Param("expressionName") String expressionName);
}