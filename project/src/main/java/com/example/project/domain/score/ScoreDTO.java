package com.example.project.domain.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Long fileId;
    private LocalDateTime date;
    private String script;
    private Long userId; // 작성자 ID

    // 각 행동별 시간 정보를 Map으로 저장 (예: "handtohead": [0.2, 2.3, ...])
    private Map<String, List<Double>> motionTimes;
    private Map<String, List<Double>> expressionTimes;
    private Map<String, List<Double>> languageTimes;

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
        dto.setScript(score.getScript());


        // 파일 정보 설정
        if (score.getFile() != null) {
            dto.setFileId(score.getFile().getFileId());
            dto.setFileName(score.getFile().getFilePath());
        } else {
            dto.setFileId(null);
            dto.setFileName("파일 없음");
        }

        // 작성자 정보 설정 (User 엔티티에 getUserId()가 있다고 가정)
        if (score.getUser() != null) {
            dto.setUserId(score.getUser().getId());
        }


        dto.setMotionTimes(
                score.getMotionTimes().stream()
                        .collect(Collectors.groupingBy(
                                MotionTimes::getActionName,
                                Collectors.mapping(
                                        MotionTimes::getActionTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );


        dto.setExpressionTimes(
                score.getExpressionTimes().stream()
                        .collect(Collectors.groupingBy(
                                ExpressionTimes::getExpressionName,
                                Collectors.mapping(
                                        ExpressionTimes::getExpressionTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );


        dto.setLanguageTimes(
                score.getLanguageTimes().stream()
                        .collect(Collectors.groupingBy(
                                LanguageTimes::getLanguageName,
                                Collectors.mapping(
                                        LanguageTimes::getLanguageTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );

        return dto;
    }
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-06 09:42 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private String username;



    private List<ScoreDTO> top4Scores;

    public void setTop4Scores(List<ScoreDTO> top4Scores) {
        this.top4Scores = top4Scores;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-06 09:42 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
