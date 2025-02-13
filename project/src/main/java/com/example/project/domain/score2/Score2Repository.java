package com.example.project.domain.score2;

import com.example.project.domain.score.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface Score2Repository extends JpaRepository<Score2, Long> {

    // 사용자 ID로 Score2 목록 조회
    List<Score2> findByUserId(Long userId);

    // 사용자 ID와 상태로 Score2 목록 조회
    List<Score2> findByUserIdAndStatus(Long userId, String status);

    // 사용자 ID로 상세 정보 포함하여 조회
    @Query("SELECT s FROM Score2 s " +
            "LEFT JOIN FETCH s.eyeTimes " +
            "LEFT JOIN FETCH s.headTimes " +
            "LEFT JOIN FETCH s.expressionTimes " +
            "LEFT JOIN FETCH s.languageTimes " +
            "WHERE s.user.id = :userId "+
            "ORDER BY s.date DESC")
    List<Score2> findByUserIdWithDetails(@Param("userId") Long userId);

    // 평균 점수 계산 쿼리
    @Query("SELECT AVG(s.eyeheadScore) FROM Score2 s")
    Double getAverageEyeheadScore();

    @Query("SELECT AVG(s.expressionScore) FROM Score2 s")
    Double getAverageExpressionScore();

    @Query("SELECT AVG(s.languageScore) FROM Score2 s")
    Double getAverageLanguageScore();

    @Query("SELECT AVG(s.totalScore) FROM Score2 s")
    Double getAverageTotalScore();

    // 최근 점수 데이터 조회
    @Query("SELECT DATE(s.date), COUNT(s) " +
            "FROM Score2 s " +
            "WHERE s.date >= :startDate " +
            "GROUP BY DATE(s.date) " +
            "ORDER BY DATE(s.date) ASC")
    List<Object[]> getRecentScore2sCount(@Param("startDate") LocalDateTime startDate);

    // 상위 4개 점수 조회
    @Query("SELECT s FROM Score2 s " +
            "WHERE s.totalScore = (SELECT MAX(sub.totalScore) FROM Score2 sub WHERE sub.user.id = s.user.id) " +
            "ORDER BY s.totalScore DESC")
    List<Score2> findTop4ByOrderByTotalScore2Desc();


    // 점수 상세 정보 조회
    @Query("SELECT new com.example.project.domain.score2.Score2DetailsDTO( " +
            "s.score2Id, s.totalScore, s.eyeheadScore, s.expressionScore, s.languageScore, " +
            "s.expressionFrequency, s.languageFrequency, " +
            "LEAST(s.eyeheadScore, s.expressionScore, s.languageScore), " +
            "CASE " +
            "WHEN s.eyeheadScore <= s.expressionScore AND s.eyeheadScore <= s.languageScore THEN 'eyehead' " +
            "WHEN s.expressionScore <= s.eyeheadScore AND s.expressionScore <= s.languageScore THEN 'expression' " +
            "WHEN s.languageScore <= s.eyeheadScore AND s.languageScore <= s.expressionScore THEN 'language' " +
            "END) " +
            "FROM Score2 s WHERE s.score2Id = :score2Id")
    Optional<Score2DetailsDTO> findScore2WithDetailsByScore2Id(@Param("score2Id") Long score2Id);

    // 빈도수 분포 조회
    @Query("SELECT s.expressionFrequency, COUNT(s) FROM Score2 s GROUP BY s.expressionFrequency")
    List<Object[]> getExpressionFrequencyDistribution();

    @Query("SELECT s.languageFrequency, COUNT(s) FROM Score2 s GROUP BY s.languageFrequency")
    List<Object[]> getLanguageFrequencyDistribution();
}
