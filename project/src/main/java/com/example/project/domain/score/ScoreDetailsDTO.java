package com.example.project.domain.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDetailsDTO {
    private Long scoreId;
    private Double totalScore;
    private Double motionScore;
    private Double expressionScore;
    private Double languageScore;
    private String motionFrequency;
    private String expressionFrequency;
    private String languageFrequency;
    private Double lowestScore;
    private String lowestScoreType;

    // ✅ 추가: 퍼센타일 (상위 몇 %)
    private Double motionPercentile;
    private Double expressionPercentile;
    private Double languagePercentile;
    private Double totalPercentile;

    // ✅ 추가: 전체 데이터에서 프리퀀시 비율
    private Double motionFrequencyRatio;
    private Double expressionFrequencyRatio;
    private Double languageFrequencyRatio;

    // ✅ 생성자 추가 (Hibernate 오류 방지)
    public ScoreDetailsDTO(Long scoreId, Double totalScore, Double motionScore, Double expressionScore, Double languageScore,
                           String motionFrequency, String expressionFrequency, String languageFrequency,
                           Double lowestScore, String lowestScoreType,
                           Double motionPercentile, Double expressionPercentile, Double languagePercentile, Double totalPercentile) {
        this.scoreId = scoreId;
        this.totalScore = totalScore;
        this.motionScore = motionScore;
        this.expressionScore = expressionScore;
        this.languageScore = languageScore;
        this.motionFrequency = motionFrequency;
        this.expressionFrequency = expressionFrequency;
        this.languageFrequency = languageFrequency;
        this.lowestScore = lowestScore;
        this.lowestScoreType = lowestScoreType;
        this.motionPercentile = motionPercentile;
        this.expressionPercentile = expressionPercentile;
        this.languagePercentile = languagePercentile;
        this.totalPercentile = totalPercentile;
    }
}


