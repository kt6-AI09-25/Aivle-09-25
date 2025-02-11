package com.example.project.domain.score2;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/scores2")
@RequiredArgsConstructor
public class Score2Controller {

    private final Score2Service score2Service;

    @GetMapping("/results")
    public String getScores(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "uploadedFileId", required = false) Long uploadedFileId,
            Model model) {

        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userId = score2Service.getLoggedInUserId(username);
        }

        List<Score2DTO> previousScores = score2Service.getPreviousScores(userId)
                .stream().map(Score2DTO::fromEntity).toList();

        Map<String, Object> evaluatingScore = score2Service.getEvaluatingScore(userId);
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

        Long userId = score2Service.getLoggedInUserId(username);

        List<Score2DTO> previousScores = score2Service.getPreviousScores(userId)
                .stream()
                .map(Score2DTO::fromEntity)
                .toList();

        Map<String, Object> evaluatingScore = score2Service.getEvaluatingScore(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("previousScores", previousScores);
        response.put("evaluatingScore", evaluatingScore);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/averages")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getAverageScores() {
        Map<String, Double> averages = new HashMap<>();
        averages.put("eyeheadScore", score2Service.getAverageEyeheadScore());
        averages.put("expressionScore", score2Service.getAverageExpressionScore());
        averages.put("languageScore", score2Service.getAverageLanguageScore());
        averages.put("totalScore", score2Service.getAverageTotalScore());
        return ResponseEntity.ok(averages);
    }

    @GetMapping("/recent-counts")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRecentScore2sCount() {
        return ResponseEntity.ok(score2Service.getRecentScore2sCount());
    }

    @GetMapping("/{score2Id}/with-top4")
    public ResponseEntity<Score2DTO> getScore2WithTop4(@PathVariable Long score2Id) {
        return ResponseEntity.ok(score2Service.getScore2WithTop4(score2Id));
    }

    @GetMapping("/top4")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTop4Score2s() {
        List<Score2DTO> top4Score2s = score2Service.getTop4Score2s();

        List<Map<String, Object>> response = top4Score2s.stream().map(dto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("score2Id", dto.getScore2Id());
            map.put("totalScore", dto.getTotalScore());
            map.put("userId", dto.getUserId());
            map.put("username", score2Service.getUsernameByUserId(dto.getUserId()));
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllScores() {
        List<Score2> score2s = score2Service.getAllScore2s();

        List<Map<String, Object>> response = score2s.stream().map(score2 -> {
            Map<String, Object> data = new HashMap<>();
            data.put("score2Id", score2.getScore2Id());
            data.put("userId", score2.getUser().getUsername());
            data.put("date", score2.getDate().toString());
            data.put("totalScore", score2.getTotalScore());
            data.put("eyeheadScore", score2.getEyeheadScore());
            data.put("expressionScore", score2.getExpressionScore());
            data.put("languageScore", score2.getLanguageScore());
            return data;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details2")
    @ResponseBody
    public ResponseEntity<Score2DetailsDTO> getScore2Details(@RequestParam("score2Id") Long score2Id) {
        Score2DetailsDTO details = score2Service.getScore2DetailPoints(score2Id);
        return ResponseEntity.ok(details);
    }


    @GetMapping("/distribution")
    @ResponseBody
    public double distribution(@RequestParam("totalscore") double totalscore) {
        List<Score2> scores = score2Service.getAllScore2s();
        List<Double> totalScores = scores.stream()
                .map(Score2::getTotalScore)
                .sorted()
                .collect(Collectors.toList());

        int totalCount = totalScores.size();
        if (totalCount == 0) {
            return 0;
        }

        int rank = Collections.binarySearch(totalScores, totalscore);
        if (rank < 0) {
            rank = -rank - 1;
        }

        double percentile = (double) rank / totalCount * 100;
        return percentile;
    }
}
