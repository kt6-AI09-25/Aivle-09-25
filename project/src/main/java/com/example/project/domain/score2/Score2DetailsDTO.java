package com.example.project.domain.score2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Score2DetailsDTO {
    private Long score2Id;
    private Double totalScore;
    private Double eyeheadScore;
    private Double expressionScore;
    private Double languageScore;
    private String expressionFrequency;
    private String languageFrequency;
    private Double totalFrequencyRatio;
    private String totalFrequency;

    // 퍼센타일
    private Double eyeheadPercentile;
    private Double expressionPercentile;
    private Double languagePercentile;
    private Double totalPercentile;

    // 전체 데이터에서 프리퀀시 비율
    private Double expressionFrequencyRatio;
    private Double languageFrequencyRatio;

    public Score2DetailsDTO(Long score2Id, Double totalScore, Double eyeheadScore, Double expressionScore, Double languageScore,
                            String expressionFrequency, String languageFrequency,
                            Double totalFrequencyRatio, String totalFrequency) {
        this.score2Id = score2Id;
        this.totalScore = totalScore;
        this.eyeheadScore = eyeheadScore;
        this.expressionScore = expressionScore;
        this.languageScore = languageScore;
        this.expressionFrequency = expressionFrequency;
        this.languageFrequency = languageFrequency;
        this.totalFrequencyRatio = totalFrequencyRatio;
        this.totalFrequency = totalFrequency;

        // Percentile 초기값
        this.eyeheadPercentile = 0.0;
        this.expressionPercentile = 0.0;
        this.languagePercentile = 0.0;
        this.totalPercentile = 0.0;
    }
}
