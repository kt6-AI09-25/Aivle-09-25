package com.example.project.domain.score;

import com.example.project.domain.file.File;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void saveScoreData(Map<String, Object> result, User user, File file) {
        // 1. Score 엔티티 생성 및 데이터 저장
        Score score = new Score();
        score.setStatus("COMPLETED");
        score.setTotalScore(Optional.ofNullable((Double) result.get("totalScore")).orElse(0.0));
        score.setMotionScore(Optional.ofNullable((Double) result.get("motionScore")).orElse(0.0));
        score.setExpressionScore(Optional.ofNullable((Double) result.get("expressionScore")).orElse(0.0));
        score.setLanguageScore(Optional.ofNullable((Double) result.get("languageScore")).orElse(0.0));
        score.setMotionFrequency(Optional.ofNullable((String) result.get("motionFrequency")).orElse("없음"));
        score.setExpressionFrequency(Optional.ofNullable((String) result.get("expressionFrequency")).orElse("없음"));
        score.setLanguageFrequency(Optional.ofNullable((String) result.get("languageFrequency")).orElse("없음"));
        score.setTempo(Optional.ofNullable((Double) result.get("tempo")).orElse(0.0));
        score.setUser(user);
        score.setFile(file);
        score.setScript(Optional.ofNullable((String) result.get("script")).orElse(""));

        // MotionTimes 저장 (리스트 저장)
        List<MotionTimes> motionTimesList = new ArrayList<>();
        Map<String, List<Double>> motionTimes = (Map<String, List<Double>>) result.get("motionTimes");
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

        // ExpressionTimes 저장
        List<ExpressionTimes> expressionTimesList = new ArrayList<>();
        Map<String, List<Double>> expressionTimes = (Map<String, List<Double>>) result.get("expressionTimes");
        if (expressionTimes != null) {
            expressionTimes.forEach((expressionName, expressionTimeList) -> {
                for (Double expressionTime : expressionTimeList) {
                    ExpressionTimes expressionTimeEntity = new ExpressionTimes();
                    expressionTimeEntity.setExpressionName(expressionName);
                    expressionTimeEntity.setExpressionTime(expressionTime != null ? expressionTime : 0);
                    expressionTimeEntity.setScore(score);
                    expressionTimesList.add(expressionTimeEntity);
                }
            });
        }

        // LanguageTimes 저장
        List<LanguageTimes> languageTimesList = new ArrayList<>();
        Map<String, List<Double>> languageTimes = (Map<String, List<Double>>) result.get("languageTimes");
        if (languageTimes != null) {
            languageTimes.forEach((languageName, languageTimeList) -> {
                for (Double languageTime : languageTimeList) {
                    LanguageTimes languageTimeEntity = new LanguageTimes();
                    languageTimeEntity.setLanguageName(languageName);
                    languageTimeEntity.setLanguageTime(languageTime != null ? languageTime : 0);
                    languageTimeEntity.setScore(score);
                    languageTimesList.add(languageTimeEntity);
                }
            });
        }

        scoreRepository.save(score);
        motionTimesRepository.saveAll(motionTimesList);
        expressionTimesRepository.saveAll(expressionTimesList);
        languageTimesRepository.saveAll(languageTimesList);
    }

    /**
     * 특정 사용자의 이전 점수를 가져오는 메서드
     */
    public List<Score> getPreviousScores(Long userId) {
        return scoreRepository.findByUserId(userId);
    }

    /**
     * 특정 사용자의 현재 평가 중인 점수를 가져오는 메서드
     */
    public Map<String, Object> getEvaluatingScore(Long userId) {
        return scoreRepository.findByUserIdAndStatus(userId, "IN_PROGRESS")
                .stream().findFirst() //  리스트에서 첫 번째 값 가져오기
                .map(score -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("scoreId", score.getScoreId());
                    result.put("totalScore", score.getTotalScore());
                    result.put("motionScore", score.getMotionScore());
                    result.put("expressionScore", score.getExpressionScore());
                    result.put("languageScore", score.getLanguageScore());
                    result.put("tempo", score.getTempo());
                    result.put("status", score.getStatus());
                    result.put("fileName", score.getFile() != null ? score.getFile().getFilePath() : "파일 없음");
                    result.put("script", score.getScript());
                    return result;
                })
                .orElseGet(HashMap::new); // ✅
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
}
