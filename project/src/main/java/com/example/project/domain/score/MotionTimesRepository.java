package com.example.project.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotionTimesRepository extends JpaRepository<MotionTimes, Long> {
    @Query("SELECT m.actionTime FROM MotionTimes m WHERE m.score.scoreId = :scoreId AND m.actionName = :actionName")
    List<Double> findActionTimesByScoreIdAndActionName(@Param("scoreId") Long scoreId, @Param("actionName") String actionName);
}