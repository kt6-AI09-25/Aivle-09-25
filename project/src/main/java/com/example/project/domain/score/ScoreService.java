package com.example.project.domain.score;

import com.example.project.domain.file.File;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final MotionTimesRepository motionTimesRepository;
    private final ExpressionTimesRepository expressionTimesRepository;
    private final LanguageTimesRepository languageTimesRepository;
    private final UserRepository userRepository;


    @Transactional
    public Score createInProgressScore(User user, File file) {
        Score score = new Score();
        score.setUser(user);
        score.setFile(file);
        score.setStatus("IN_PROGRESS");

        // 필요 시 초기값 세팅 (0점, 빈 문자열 등)
        score.setTotalScore(0.0);
        score.setMotionScore(0.0);
        score.setExpressionScore(0.0);
        score.setLanguageScore(0.0);
        score.setMotionFrequency("");
        score.setExpressionFrequency("");
        score.setLanguageFrequency("");
        score.setTempo(0.0);
        score.setScript("");

        // DB 저장
        return scoreRepository.save(score);
    }


    @Transactional
    public void completeScoreData(Long scoreId, Map<String, Object> result) {
        // 업로드 시 생성해둔 Score 조회
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("Score가 존재하지 않습니다."));

        score.setStatus("COMPLETED");

        // 분석 결과 반영
        score.setTotalScore(optionalDouble(result.get("totalScore")));
        score.setMotionScore(optionalDouble(result.get("motionScore")));
        score.setExpressionScore(optionalDouble(result.get("expressionScore")));
        score.setLanguageScore(optionalDouble(result.get("languageScore")));
        score.setMotionFrequency(optionalString(result.get("motionFrequency"), "없음"));
        score.setExpressionFrequency(optionalString(result.get("expressionFrequency"), "없음"));
        score.setLanguageFrequency(optionalString(result.get("languageFrequency"), "없음"));
        score.setTempo(optionalDouble(result.get("tempo")));
        score.setScript(optionalString(result.get("script"), ""));

        // ----- MotionTimes 저장 -----
        List<MotionTimes> motionTimesList = new ArrayList<>();
        Map<String, List<Double>> motionTimes = castMap(result.get("motionTimes"));
        if (motionTimes != null) {
            motionTimes.forEach((actionName, actionTimeList) -> {
                for (Double actionTime : actionTimeList) {
                    MotionTimes motionTime = new MotionTimes();
                    motionTime.setActionName(actionName);
                    motionTime.setActionTime(actionTime != null ? actionTime : 0);
                    motionTime.setScore(score);
                    motionTimesList.add(motionTime);
                }
            });
        }

        // ----- ExpressionTimes 저장 -----
        List<ExpressionTimes> expressionTimesList = new ArrayList<>();
        Map<String, List<Double>> expressionMap = castMap(result.get("expressionTimes"));
        if (expressionMap != null) {
            expressionMap.forEach((expressionName, expressionTimeList) -> {
                for (Double expressionTime : expressionTimeList) {
                    ExpressionTimes expressionTimeEntity = new ExpressionTimes();
                    expressionTimeEntity.setExpressionName(expressionName);
                    expressionTimeEntity.setExpressionTime(expressionTime != null ? expressionTime : 0);
                    expressionTimeEntity.setScore(score);
                    expressionTimesList.add(expressionTimeEntity);
                }
            });
        }

        // ----- LanguageTimes 저장 -----
        List<LanguageTimes> languageTimesList = new ArrayList<>();
        Map<String, List<Double>> languageMap = castMap(result.get("languageTimes"));
        if (languageMap != null) {
            languageMap.forEach((languageName, languageTimeList) -> {
                for (Double languageTime : languageTimeList) {
                    LanguageTimes languageTimeEntity = new LanguageTimes();
                    languageTimeEntity.setLanguageName(languageName);
                    languageTimeEntity.setLanguageTime(languageTime != null ? languageTime : 0);
                    languageTimeEntity.setScore(score);
                    languageTimesList.add(languageTimeEntity);
                }
            });
        }

        // Score 저장
        scoreRepository.save(score);
        // Times 저장
        motionTimesRepository.saveAll(motionTimesList);
        expressionTimesRepository.saveAll(expressionTimesList);
        languageTimesRepository.saveAll(languageTimesList);
    }


    public List<Score> getPreviousScores(Long userId) {
        return scoreRepository.findByUserIdWithDetails(userId);
    }

    /**
     * "IN_PROGRESS" 상태의 Score를 찾아,
     * 현재 평가중인 정보를 맵 형태로 반환.
     */
    public Map<String, Object> getEvaluatingScore(Long userId) {
        return scoreRepository.findByUserIdAndStatus(userId, "IN_PROGRESS")
                .stream().findFirst()
                .map(score -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("scoreId", score.getScoreId());
                    result.put("totalScore", score.getTotalScore());
                    result.put("motionScore", score.getMotionScore());
                    result.put("expressionScore", score.getExpressionScore());
                    result.put("languageScore", score.getLanguageScore());
                    result.put("tempo", score.getTempo());
                    result.put("motionFrequency", score.getMotionFrequency());
                    result.put("expressionFrequency", score.getExpressionFrequency());
                    result.put("languageFrequency", score.getLanguageFrequency());
                    result.put("script", score.getScript());
                    result.put("date", score.getDate());
                    result.put("userId", score.getUser().getId());

                    if (score.getFile() != null) {
                        result.put("fileId", score.getFile().getFileId());
                        result.put("fileName", score.getFile().getFilePath());
                        result.put("status", score.getStatus()); // "IN_PROGRESS"
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

    public Score getScoreDetails(Long scoreId) {
        return scoreRepository.findById(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("점수를 찾을 수 없습니다."));
    }


    // Object -> double 변환
    private double optionalDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }

    // Object -> String 변환
    private String optionalString(Object obj, String defaultVal) {
        return (obj != null) ? obj.toString() : defaultVal;
    }

    // generic 형변환을 안전하게 감싸는 헬퍼
    @SuppressWarnings("unchecked")
    private Map<String, List<Double>> castMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return (Map<String, List<Double>>) obj;
        }
        return null;
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-06 09:42 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public Double getAverageMotionScore() {
        return scoreRepository.getAverageMotionScore();
    }

    public Double getAverageExpressionScore() {
        return scoreRepository.getAverageExpressionScore();
    }

    public Double getAverageLanguageScore() {
        return scoreRepository.getAverageLanguageScore();
    }

    public Double getAverageTotalScore() {
        return scoreRepository.getAverageTotalScore();
    }

    public List<Map<String, Object>> getRecentScoresCount() {
        // 최근 7일간의 날짜 계산
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDate today = LocalDate.now();

        // 최근 7일간의 날짜 초기화 (값이 없으면 0으로 표시)
        Map<LocalDate, Integer> dateCountMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            dateCountMap.put(today.minusDays(i), 0);
        }

        // DB에서 최근 7일간의 데이터 가져오기
        List<Object[]> results = scoreRepository.getRecentScoresCount(sevenDaysAgo);
        for (Object[] result : results) {
            LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
            int count = ((Number) result[1]).intValue();
            dateCountMap.put(date, count);
        }

        // 최종적으로 날짜와 개수를 리스트로 변환하여 반환
        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", entry.getKey().toString()); // 날짜
            data.put("count", entry.getValue()); // 등록 개수
            response.add(data);
        }

        return response;
    }

    public List<ScoreDTO> getTop4Scores() {
        List<Score> topScores = scoreRepository.findTop4ByOrderByTotalScoreDesc();

        Map<Long, ScoreDTO> uniqueUserScores = new LinkedHashMap<>();
        for (Score score : topScores) {
            if (!uniqueUserScores.containsKey(score.getUser().getId())) {
                uniqueUserScores.put(score.getUser().getId(), ScoreDTO.fromEntity(score));
            }
            if (uniqueUserScores.size() == 4) break;
        }

        return new ArrayList<>(uniqueUserScores.values());
    }


    public ScoreDTO getScoreWithTop4(Long scoreId) {
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new RuntimeException("Score not found"));

        ScoreDTO scoreDTO = ScoreDTO.fromEntity(score);
        scoreDTO.setTop4Scores(getTop4Scores());

        return scoreDTO;
    }

    public String getUsernameByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("알 수 없음");
    }

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    private double castToDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }

    public Map<String, Double> calculateFrequencyRatios() {
        long totalCount = scoreRepository.count(); // 전체 데이터 개수

        Map<String, Double> motionFrequencyRatios = calculateRatio(scoreRepository.getMotionFrequencyDistribution(), totalCount);
        Map<String, Double> expressionFrequencyRatios = calculateRatio(scoreRepository.getExpressionFrequencyDistribution(), totalCount);
        Map<String, Double> languageFrequencyRatios = calculateRatio(scoreRepository.getLanguageFrequencyDistribution(), totalCount);

        Map<String, Double> frequencyRatios = new HashMap<>();
        frequencyRatios.putAll(motionFrequencyRatios);
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

    public ScoreDetailsDTO getScoreDetailPoints(Long scoreId) {
        ScoreDetailsDTO details = scoreRepository.findScoreWithDetailsByScoreId(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("Score not found for ID: " + scoreId));

        Map<String, Double> frequencyRatios = calculateFrequencyRatios();
        details.setMotionFrequencyRatio(frequencyRatios.getOrDefault(details.getMotionFrequency(), 0.0));
        details.setExpressionFrequencyRatio(frequencyRatios.getOrDefault(details.getExpressionFrequency(), 0.0));
        details.setLanguageFrequencyRatio(frequencyRatios.getOrDefault(details.getLanguageFrequency(), 0.0));

        // 전체 데이터 가져오기
        List<Score> allScores = scoreRepository.findAll();

        // 모든 점수를 리스트로 변환하여 정렬
        List<Double> motionScores = allScores.stream().map(Score::getMotionScore).sorted().toList();
        List<Double> expressionScores = allScores.stream().map(Score::getExpressionScore).sorted().toList();
        List<Double> languageScores = allScores.stream().map(Score::getLanguageScore).sorted().toList();
        List<Double> totalScores = allScores.stream().map(Score::getTotalScore).sorted().toList();

        // 현재 점수의 percentile 계산
        details.setMotionPercentile(calculatePercentile(motionScores, details.getMotionScore()));
        details.setExpressionPercentile(calculatePercentile(expressionScores, details.getExpressionScore()));
        details.setLanguagePercentile(calculatePercentile(languageScores, details.getLanguageScore()));
        details.setTotalPercentile(calculatePercentile(totalScores, details.getTotalScore()));

        return details;
    }

    private double calculatePercentile(List<Double> sortedScores, double score) {
        int totalCount = sortedScores.size();
        if (totalCount == 0) return 0.0;

        int rank = Collections.binarySearch(sortedScores, score);
        if (rank < 0) rank = -rank - 1; // 삽입 위치 계산

        double percentile = ((double) rank / totalCount) * 100;

        //하위 몇 % → 상위 몇 % 변환
        return 100.0 - percentile;
    }






    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-06 09:42 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
