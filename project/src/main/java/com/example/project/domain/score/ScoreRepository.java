package com.example.project.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
