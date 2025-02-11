package com.example.project.domain.score2;

import com.example.project.domain.file.File;
import com.example.project.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class Score2DTO {
    private Long score2Id;
    private Double totalScore;
    private Double eyeheadScore;
    private Double expressionScore;
    private Double languageScore;
    private String expressionFrequency;
    private String languageFrequency;
    private Double tempo;
    private String status;
    private String fileName;
    private Long fileId;
    private LocalDateTime date;
    private String script;
    private Long userId; // 작성자 ID

    // 행동 시간 정보 (Map 형식)
    private Map<String, List<Double>> eyeTimes;
    private Map<String, List<Double>> headTimes;
    private Map<String, List<Double>> expressionTimes;
    private Map<String, List<Double>> languageTimes;

    // 사용자 정보
    private String username;

    // 상위 4개 점수
    private List<Score2DTO> top4Scores;

    public void setTop4Scores(List<Score2DTO> top4Scores) {
        this.top4Scores = top4Scores;
    }

    // Entity → DTO 변환 메서드
    public static Score2DTO fromEntity(Score2 score2) {
        Score2DTO dto = new Score2DTO();
        dto.setScore2Id(score2.getScore2Id());
        dto.setTotalScore(score2.getTotalScore());
        dto.setEyeheadScore(score2.getEyeheadScore());
        dto.setExpressionScore(score2.getExpressionScore());
        dto.setLanguageScore(score2.getLanguageScore());
        dto.setExpressionFrequency(score2.getExpressionFrequency());
        dto.setLanguageFrequency(score2.getLanguageFrequency());
        dto.setTempo(score2.getTempo());
        dto.setStatus(score2.getStatus());
        dto.setDate(score2.getDate());
        dto.setScript(score2.getScript());

        // 파일 정보
        File file = score2.getFile();
        if (file != null) {
            dto.setFileId(file.getFileId());
            dto.setFileName(file.getFilePath());
        } else {
            dto.setFileId(null);
            dto.setFileName("파일 없음");
        }

        // 사용자 정보
        User user = score2.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
        }

        // EyeTimes 매핑
        dto.setEyeTimes(
                score2.getEyeTimes().stream()
                        .collect(Collectors.groupingBy(
                                EyeTimes::getEyeDirection,
                                Collectors.mapping(
                                        EyeTimes::getActionTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );

        // HeadTimes 매핑
        dto.setHeadTimes(
                score2.getHeadTimes().stream()
                        .collect(Collectors.groupingBy(
                                HeadTimes::getHeadDirection,
                                Collectors.mapping(
                                        HeadTimes::getActionTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );

        // ExpressionTimes2 매핑
        dto.setExpressionTimes(
                score2.getExpressionTimes().stream()
                        .collect(Collectors.groupingBy(
                                ExpressionTimes2::getExpressionName,
                                Collectors.mapping(
                                        ExpressionTimes2::getExpressionTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );

        // LanguageTimes2 매핑
        dto.setLanguageTimes(
                score2.getLanguageTimes().stream()
                        .collect(Collectors.groupingBy(
                                LanguageTimes2::getLanguageName,
                                Collectors.mapping(
                                        LanguageTimes2::getLanguageTime,
                                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                                            list.sort(Double::compareTo);
                                            return list;
                                        })
                                )
                        ))
        );

        return dto;
    }
}
