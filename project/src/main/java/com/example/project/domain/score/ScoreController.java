package com.example.project.domain.score;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 특정 사용자 ID로 이전 점수 및 현재 평가 데이터를 가져오는 메서드
     */
    @GetMapping("/results")
    public String getScores(@RequestParam("userId") Long userId,
                            @RequestParam(value = "uploadedFileId", required = false) Long uploadedFileId,
                            Model model) {
        List<ScoreDTO> previousScores = scoreService.getPreviousScores(userId)
                .stream()
                .map(ScoreDTO::fromEntity)
                .toList();

        Map<String, Object> evaluatingScore = scoreService.getEvaluatingScore(userId);

        if (uploadedFileId != null) {
            evaluatingScore = new HashMap<>();
            evaluatingScore.put("fileId", uploadedFileId);
            evaluatingScore.put("status", "IN_PROGRESS"); // 평가 중 상태
        }

        model.addAttribute("previousScores", previousScores);
        model.addAttribute("evaluatingScore", evaluatingScore);

        return "myresult";
    }


    @GetMapping("/results/api")
    public ResponseEntity<Map<String, Object>> getScoresForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = scoreService.getLoggedInUserId(username);

        List<ScoreDTO> previousScores = scoreService.getPreviousScores(userId)
                .stream()
                .map(ScoreDTO::fromEntity)
                .toList();

        Map<String, Object> evaluatingScore = scoreService.getEvaluatingScore(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("previousScores", previousScores);
        response.put("evaluatingScore", evaluatingScore);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getScoreDetails(@RequestParam("scoreId") Long scoreId) {
        Score scoreDetails = scoreService.getScoreDetails(scoreId);

        Map<String, Object> response = new HashMap<>();
        response.put("scoreId", scoreDetails.getScoreId());
        response.put("totalScore", scoreDetails.getTotalScore());
        response.put("motionScore", scoreDetails.getMotionScore());
        response.put("expressionScore", scoreDetails.getExpressionScore());
        response.put("languageScore", scoreDetails.getLanguageScore());
        response.put("tempo", scoreDetails.getTempo());
        response.put("date", scoreDetails.getDate().toString());
        response.put("fileName", scoreDetails.getFile() != null ? scoreDetails.getFile().getFilePath() : "파일 없음");

        // MotionTimes, ExpressionTimes, LanguageTimes 리스트를 JSON으로 변환
        List<Map<String, Object>> motionTimesList = scoreDetails.getMotionTimes().stream().map(motion -> {
            Map<String, Object> motionMap = new HashMap<>();
            motionMap.put("actionName", motion.getActionName());
            motionMap.put("actionTime", motion.getActionTime());
            return motionMap;
        }).toList();

        List<Map<String, Object>> expressionTimesList = scoreDetails.getExpressionTimes().stream().map(expression -> {
            Map<String, Object> expressionMap = new HashMap<>();
            expressionMap.put("expressionName", expression.getExpressionName());
            expressionMap.put("expressionTime", expression.getExpressionTime());
            return expressionMap;
        }).toList();

        List<Map<String, Object>> languageTimesList = scoreDetails.getLanguageTimes().stream().map(language -> {
            Map<String, Object> languageMap = new HashMap<>();
            languageMap.put("languageName", language.getLanguageName());
            languageMap.put("languageTime", language.getLanguageTime());
            return languageMap;
        }).toList();

        response.put("motionTimes", motionTimesList);
        response.put("expressionTimes", expressionTimesList);
        response.put("languageTimes", languageTimesList);

        return ResponseEntity.ok(response);
    }


}
