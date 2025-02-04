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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 특정 사용자 ID로 이전 점수 및 현재 평가 데이터를 가져오는 메서드
     */
    @GetMapping("/results")
    public String getScores(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "uploadedFileId", required = false) Long uploadedFileId,
            Model model) {

        // userId가 없을 경우 현재 로그인한 사용자 ID 가져오기
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userId = scoreService.getLoggedInUserId(username);
        }

        List<ScoreDTO> previousScores = scoreService.getPreviousScores(userId)
                .stream().map(ScoreDTO::fromEntity).toList();

        Map<String, Object> evaluatingScore = scoreService.getEvaluatingScore(userId);

        // ✅
        if (evaluatingScore == null) {
            evaluatingScore = new HashMap<>();
        }

        if (uploadedFileId != null) {
            evaluatingScore.put("fileId", uploadedFileId);
            evaluatingScore.put("status", "IN_PROGRESS");
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
        response.put("script", scoreDetails.getScript());


        response.put("motionFrequency", scoreDetails.getMotionFrequency());
        response.put("expressionFrequency", scoreDetails.getExpressionFrequency());
        response.put("languageFrequency", scoreDetails.getLanguageFrequency());


        if (scoreDetails.getFile() != null) {
            response.put("fileId", scoreDetails.getFile().getFileId());
            response.put("fileName", scoreDetails.getFile().getFilePath());
        } else {
            response.put("fileId", null);
            response.put("fileName", "파일 없음");
        }


        Map<String, List<Double>> motionTimesMap = scoreDetails.getMotionTimes().stream()
                .collect(Collectors.groupingBy(
                        MotionTimes::getActionName,
                        Collectors.mapping(MotionTimes::getActionTime, Collectors.toList())
                ));
        response.put("motionTimes", motionTimesMap);

        Map<String, List<Double>> expressionTimesMap = scoreDetails.getExpressionTimes().stream()
                .collect(Collectors.groupingBy(
                        ExpressionTimes::getExpressionName,
                        Collectors.mapping(ExpressionTimes::getExpressionTime, Collectors.toList())
                ));
        response.put("expressionTimes", expressionTimesMap);

        Map<String, List<Double>> languageTimesMap = scoreDetails.getLanguageTimes().stream()
                .collect(Collectors.groupingBy(
                        LanguageTimes::getLanguageName,
                        Collectors.mapping(LanguageTimes::getLanguageTime, Collectors.toList())
                ));
        response.put("languageTimes", languageTimesMap);

        return ResponseEntity.ok(response);
    }

}
