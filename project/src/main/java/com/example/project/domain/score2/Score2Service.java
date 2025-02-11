package com.example.project.domain.score2;

import com.example.project.domain.file.File;
import com.example.project.domain.score.Score;
import com.example.project.domain.score.ScoreDTO;
import com.example.project.domain.score.ScoreDetailsDTO;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class Score2Service {

    private final Score2Repository score2Repository;
    private final EyeTimesRepository eyeTimesRepository;
    private final HeadTimesRepository headTimesRepository;
    private final ExpressionTimes2Repository expressionTimes2Repository;
    private final LanguageTimes2Repository languageTimes2Repository;
    private final UserRepository userRepository;

    @Transactional
    public Score2 createInProgressScore2(User user, File file) {
        Score2 score2 = new Score2();
        score2.setUser(user);
        score2.setFile(file);
        score2.setStatus("IN_PROGRESS");

        score2.setTotalScore(0.0);
        score2.setEyeheadScore(0.0);
        score2.setExpressionScore(0.0);
        score2.setLanguageScore(0.0);
        score2.setExpressionFrequency("");
        score2.setLanguageFrequency("");
        score2.setTempo(0.0);
        score2.setScript("");

        return score2Repository.save(score2);
    }

    @Transactional
    public void completeScore2Data(Long score2Id, Map<String, Object> result) {
        Score2 score2 = score2Repository.findById(score2Id)
                .orElseThrow(() -> new IllegalArgumentException("Score2가 존재하지 않습니다."));

        score2.setStatus("COMPLETED");

        score2.setTotalScore(optionalDouble(result.get("totalScore")));
        score2.setEyeheadScore(optionalDouble(result.get("eyeheadScore")));
        score2.setExpressionScore(optionalDouble(result.get("expressionScore")));
        score2.setLanguageScore(optionalDouble(result.get("languageScore")));
        score2.setExpressionFrequency(optionalString(result.get("expressionFrequency"), "없음"));
        score2.setLanguageFrequency(optionalString(result.get("languageFrequency"), "없음"));
        score2.setTempo(optionalDouble(result.get("tempo")));
        score2.setScript(optionalString(result.get("script"), ""));

        List<EyeTimes> eyeTimesList = new ArrayList<>();
        Map<String, List<Double>> eyeTimes = castMap(result.get("eyeTimes"));
        if (eyeTimes != null) {
            eyeTimes.forEach((direction, times) -> {
                for (Double time : times) {
                    EyeTimes eyeTime = new EyeTimes();
                    eyeTime.setEyeDirection(direction);
                    eyeTime.setActionTime(time);
                    eyeTime.setScore2(score2);
                    eyeTimesList.add(eyeTime);
                }
            });
        }

        List<HeadTimes> headTimesList = new ArrayList<>();
        Map<String, List<Double>> headTimes = castMap(result.get("headTimes"));
        if (headTimes != null) {
            headTimes.forEach((direction, times) -> {
                for (Double time : times) {
                    HeadTimes headTime = new HeadTimes();
                    headTime.setHeadDirection(direction);
                    headTime.setActionTime(time);
                    headTime.setScore2(score2);
                    headTimesList.add(headTime);
                }
            });
        }

        List<ExpressionTimes2> expressionTimesList = new ArrayList<>();
        Map<String, List<Double>> expressionMap = castMap(result.get("expressionTimes"));
        if (expressionMap != null) {
            expressionMap.forEach((name, times) -> {
                for (Double time : times) {
                    ExpressionTimes2 expTime = new ExpressionTimes2();
                    expTime.setExpressionName(name);
                    expTime.setExpressionTime(time);
                    expTime.setScore2(score2);
                    expressionTimesList.add(expTime);
                }
            });
        }

        List<LanguageTimes2> languageTimesList = new ArrayList<>();
        Map<String, List<Double>> languageMap = castMap(result.get("languageTimes"));
        if (languageMap != null) {
            languageMap.forEach((name, times) -> {
                for (Double time : times) {
                    LanguageTimes2 langTime = new LanguageTimes2();
                    langTime.setLanguageName(name);
                    langTime.setLanguageTime(time);
                    langTime.setScore2(score2);
                    languageTimesList.add(langTime);
                }
            });
        }

        score2Repository.save(score2);
        eyeTimesRepository.saveAll(eyeTimesList);
        headTimesRepository.saveAll(headTimesList);
        expressionTimes2Repository.saveAll(expressionTimesList);
        languageTimes2Repository.saveAll(languageTimesList);
    }

    public List<Score2> getPreviousScores(Long userId) {
        return score2Repository.findByUserIdWithDetails(userId);
    }

    public Map<String, Object> getEvaluatingScore(Long userId) {
        return score2Repository.findByUserIdAndStatus(userId, "IN_PROGRESS")
                .stream()
                .findFirst()
                .map(score2 -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("score2Id", score2.getScore2Id());
                    result.put("totalScore", score2.getTotalScore());
                    result.put("eyeheadScore", score2.getEyeheadScore());
                    result.put("expressionScore", score2.getExpressionScore());
                    result.put("languageScore", score2.getLanguageScore());
                    result.put("tempo", score2.getTempo());
                    result.put("expressionFrequency", score2.getExpressionFrequency());
                    result.put("languageFrequency", score2.getLanguageFrequency());
                    result.put("script", score2.getScript());
                    result.put("date", score2.getDate());
                    result.put("userId", score2.getUser().getId());

                    if (score2.getFile() != null) {
                        result.put("fileId", score2.getFile().getFileId());
                        result.put("fileName", score2.getFile().getFilePath());
                        result.put("status", score2.getStatus());
                    } else {
                        result.put("fileId", null);
                        result.put("fileName", "파일 없음");
                        result.put("status", "IN_PROGRESS");
                    }
                    return result;
                })
                .orElseGet(HashMap::new);
    }

    public Long getLoggedInUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private double optionalDouble(Object obj) {
        return obj instanceof Number ? ((Number) obj).doubleValue() : 0.0;
    }

    private String optionalString(Object obj, String defaultVal) {
        return obj != null ? obj.toString() : defaultVal;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Double>> castMap(Object obj) {
        return obj instanceof Map<?, ?> ? (Map<String, List<Double>>) obj : null;
    }

    //  평균 점수 계산 메서드
    public Double getAverageEyeheadScore() {
        return score2Repository.getAverageEyeheadScore();
    }

    public Double getAverageExpressionScore() {
        return score2Repository.getAverageExpressionScore();
    }

    public Double getAverageLanguageScore() {
        return score2Repository.getAverageLanguageScore();
    }

    public Double getAverageTotalScore() {
        return score2Repository.getAverageTotalScore();
    }

    //  최근 7일간의 점수 등록 개수 조회
    public List<Map<String, Object>> getRecentScore2sCount() {
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDate today = LocalDate.now();

        Map<LocalDate, Integer> dateCountMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            dateCountMap.put(today.minusDays(i), 0);
        }

        List<Object[]> results = score2Repository.getRecentScore2sCount(sevenDaysAgo);
        for (Object[] result : results) {
            LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
            int count = ((Number) result[1]).intValue();
            dateCountMap.put(date, count);
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", entry.getKey().toString());
            data.put("count", entry.getValue());
            response.add(data);
        }

        return response;
    }


    public List<Score2DTO> getTop4Score2s() {
        List<Score2> topScores2 = score2Repository.findTop4ByOrderByTotalScore2Desc();

        Map<Long, Score2DTO> uniqueUserScore2s = new LinkedHashMap<>();
        for (Score2 score2 : topScores2) {
            if (!uniqueUserScore2s.containsKey(score2.getUser().getId())) {
                uniqueUserScore2s.put(score2.getUser().getId(), Score2DTO.fromEntity(score2));
            }
            if (uniqueUserScore2s.size() == 4) break;
        }

        return new ArrayList<>(uniqueUserScore2s.values());
    }

    public Score2DTO getScore2WithTop4(Long score2Id) {
        Score2 score2 = score2Repository.findById(score2Id)
                .orElseThrow(() -> new RuntimeException("Score2 not found"));

        Score2DTO score2DTO = Score2DTO.fromEntity(score2);
        score2DTO.setTop4Scores(getTop4Score2s());

        return score2DTO;
    }

    //  유저 ID로 유저명 가져오기
    public String getUsernameByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("알 수 없음");
    }

    public List<Score2> getAllScore2s() {
        return score2Repository.findAll();
    }

    //  빈도수 비율 계산
    public Map<String, Double> calculateFrequencyRatios() {
        long totalCount = score2Repository.count();

        Map<String, Double> expressionFrequencyRatios = calculateRatio(score2Repository.getExpressionFrequencyDistribution(), totalCount);
        Map<String, Double> languageFrequencyRatios = calculateRatio(score2Repository.getLanguageFrequencyDistribution(), totalCount);

        Map<String, Double> frequencyRatios = new HashMap<>();
        frequencyRatios.putAll(expressionFrequencyRatios);
        frequencyRatios.putAll(languageFrequencyRatios);

        return frequencyRatios;
    }

    private Map<String, Double> calculateRatio(List<Object[]> data, long totalCount) {
        Map<String, Double> result = new HashMap<>();
        for (Object[] row : data) {
            String frequency = (String) row[0];
            long count = ((Number) row[1]).longValue();
            result.put(frequency, (count * 100.0) / totalCount);
        }
        return result;
    }

    public Score2DetailsDTO getScore2DetailPoints(Long scoreId) {
        Score2DetailsDTO details = score2Repository.findScore2WithDetailsByScore2Id(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("Score not found for ID: " + scoreId));

        Map<String, Double> frequencyRatios = calculateFrequencyRatios();
        details.setExpressionFrequencyRatio(frequencyRatios.getOrDefault(details.getExpressionFrequency(), 0.0));
        details.setLanguageFrequencyRatio(frequencyRatios.getOrDefault(details.getLanguageFrequency(), 0.0));

        // 전체 데이터 가져오기
        List<Score2> allScores = score2Repository.findAll();

        // 모든 점수를 리스트로 변환하여 정렬
        List<Double> motionScores = allScores.stream().map(Score2::getEyeheadScore).sorted().toList();
        List<Double> expressionScores = allScores.stream().map(Score2::getExpressionScore).sorted().toList();
        List<Double> languageScores = allScores.stream().map(Score2::getLanguageScore).sorted().toList();
        List<Double> totalScores = allScores.stream().map(Score2::getTotalScore).sorted().toList();

        // 현재 점수의 percentile 계산
        details.setEyeheadPercentile(calculatePercentile(motionScores, details.getEyeheadScore()));
        details.setExpressionPercentile(calculatePercentile(expressionScores, details.getExpressionScore()));
        details.setLanguagePercentile(calculatePercentile(languageScores, details.getLanguageScore()));
        details.setTotalPercentile(calculatePercentile(totalScores, details.getTotalScore()));

        return details;
    }

    //  퍼센타일 계산 메서드
    private double calculatePercentile(List<Double> sortedScore2s, double score2) {
        int totalCount = sortedScore2s.size();
        if (totalCount == 0) return 0.0;

        int rank = Collections.binarySearch(sortedScore2s, score2);
        if (rank < 0) rank = -rank - 1;

        double percentile = ((double) rank / totalCount) * 100;
        return 100.0 - percentile;  // 상위 몇 %인지 반환
    }
}

