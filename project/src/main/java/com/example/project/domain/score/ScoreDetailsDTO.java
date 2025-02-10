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
    private Double totalFrequencyRatio;
    private String totalFrequency;

    //퍼센타일 (상위 몇 %)
    private Double motionPercentile;
    private Double expressionPercentile;
    private Double languagePercentile;
    private Double totalPercentile;

    //전체 데이터에서 프리퀀시 비율
    private Double motionFrequencyRatio;
    private Double expressionFrequencyRatio;
    private Double languageFrequencyRatio;


    public ScoreDetailsDTO(Long scoreId, Double totalScore, Double motionScore, Double expressionScore, Double languageScore,
                           String motionFrequency, String expressionFrequency, String languageFrequency,
                           Double totalFrequencyRatio, String totalFrequency) {
        this.scoreId = scoreId;
        this.totalScore = Math.floor(totalScore);
        this.motionScore = motionScore;
        this.expressionScore = expressionScore;
        this.languageScore = Math.floor(languageScore);
        this.motionFrequency = motionFrequency;
        this.expressionFrequency = expressionFrequency;
        this.languageFrequency = languageFrequency;
        this.totalFrequencyRatio = totalFrequencyRatio;
        this.totalFrequency = totalFrequency;

        // Percentile은 나중에 ScoreService에서 설정
        this.motionPercentile = 0.0;
        this.expressionPercentile = 0.0;
        this.languagePercentile = 0.0;
        this.totalPercentile = 0.0;
    }
}


