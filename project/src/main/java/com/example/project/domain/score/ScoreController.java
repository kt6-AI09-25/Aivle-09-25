package com.example.project.domain.score;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

        // 이전 평가 점수
        List<ScoreDTO> previousScores = scoreService.getPreviousScores(userId)
                .stream().map(ScoreDTO::fromEntity).toList();

        // 현재 평가 점수 조회
        Map<String, Object> evaluatingScore = scoreService.getEvaluatingScore(userId);
        if (evaluatingScore == null || evaluatingScore.isEmpty()) {
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

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-06 09:42 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @GetMapping("/averages")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getAverageScores() {
        Map<String, Double> averages = new HashMap<>();
        averages.put("motionScore", scoreService.getAverageMotionScore());
        averages.put("expressionScore", scoreService.getAverageExpressionScore());
        averages.put("languageScore", scoreService.getAverageLanguageScore());
        averages.put("totalScore", scoreService.getAverageTotalScore());
        return ResponseEntity.ok(averages);
    }

    @GetMapping("/recent-counts")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRecentScoresCount() {
        return ResponseEntity.ok(scoreService.getRecentScoresCount());
    }

    @GetMapping("/{scoreId}/with-top4")
    public ResponseEntity<ScoreDTO> getScoreWithTop4(@PathVariable Long scoreId) {
        return ResponseEntity.ok(scoreService.getScoreWithTop4(scoreId));
    }

    @GetMapping("/top4")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTop4Scores() {
        List<ScoreDTO> top4Scores = scoreService.getTop4Scores();

        // 기존 DTO 변경 없이 추가 데이터를 Map 형태로 전달
        List<Map<String, Object>> response = top4Scores.stream().map(dto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("scoreId", dto.getScoreId());
            map.put("totalScore", dto.getTotalScore());
            map.put("userId", dto.getUserId());
            map.put("username", scoreService.getUsernameByUserId(dto.getUserId())); // 추가된 필드
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllScores() {
        List<Score> scores = scoreService.getAllScores();

        // 필요한 데이터만 추려서 반환
        List<Map<String, Object>> response = scores.stream().map(score -> {
            Map<String, Object> data = new HashMap<>();
            data.put("scoreId", score.getScoreId());
            data.put("userId", score.getUser().getUsername());
            data.put("date", score.getDate().toString());
            data.put("totalScore", Math.floor(score.getTotalScore()));
            data.put("motionScore", score.getMotionScore());
            data.put("expressionScore", score.getExpressionScore());
            data.put("languageScore", Math.floor(score.getLanguageScore()));
            return data;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details2")
    public ResponseEntity<ScoreDetailsDTO> getScoreDetailPoints(@RequestParam("scoreId") Long scoreId) {
        ScoreDetailsDTO details = scoreService.getScoreDetailPoints(scoreId);
        return ResponseEntity.ok(details);
    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-06 09:42 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // 재관 현재점수가 전체에서 몇퍼센트에 있는지 반환
    @GetMapping("/distribution")
    @ResponseBody
    public double distribution(@RequestParam("totalscore") double totalscore) {
        List<Score> scores = scoreService.getAllScores();
        // 모든 totalscore 값 추출
        List<Double> totalScores = scores.stream()
                .map(Score::getTotalScore)
                .sorted()
                .collect(Collectors.toList());

        int totalCount = totalScores.size();
        if (totalCount == 0) {
            return 0;
        }

        // 현재 입력받은 totalscore 값의 순위 찾기
        int rank = Collections.binarySearch(totalScores, totalscore);
        if (rank < 0) {
            rank = -rank - 1;
        }

        double percentile = (double) rank / totalCount * 100;
        return percentile;

    }

}