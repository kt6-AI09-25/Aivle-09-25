package com.example.project.domain.score;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScoreDTO {
    private Long scoreId;
    private Double totalScore;
    private Double motionScore;
    private Double expressionScore;
    private Double languageScore;
    private String motionFrequency;
    private String expressionFrequency;
    private String languageFrequency;
    private Double tempo;
    private String status;
    private String fileName;
    private LocalDateTime date;

    public static ScoreDTO fromEntity(Score score) {
        ScoreDTO dto = new ScoreDTO();
        dto.setScoreId(score.getScoreId());
        dto.setTotalScore(score.getTotalScore());
        dto.setMotionScore(score.getMotionScore());
        dto.setExpressionScore(score.getExpressionScore());
        dto.setLanguageScore(score.getLanguageScore());
        dto.setMotionFrequency(score.getMotionFrequency());
        dto.setExpressionFrequency(score.getExpressionFrequency());
        dto.setLanguageFrequency(score.getLanguageFrequency());
        dto.setTempo(score.getTempo());
        dto.setStatus(score.getStatus());
        dto.setDate(score.getDate());

        if (score.getFile() != null) {
            dto.setFileName(score.getFile().getFilePath());
        } else {
            dto.setFileName("파일 없음");
        }

        return dto;
    }
}
