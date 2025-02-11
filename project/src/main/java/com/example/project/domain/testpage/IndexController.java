package com.example.project.domain.testpage;

import com.example.project.domain.score.ScoreDTO;
import com.example.project.domain.score.ScoreRepository;
import com.example.project.domain.score.ScoreService;
import com.example.project.domain.score2.Score2DTO;
import com.example.project.domain.score2.Score2Repository;
import com.example.project.domain.score2.Score2Service;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final UserRepository userRepository;
    private final ScoreService scoreService;
    private final Score2Service score2Service;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/test")
    public String testPage() {
        return "test";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }

    @GetMapping("/choice")
    public String choice() {
        return "choice";
    }

    @GetMapping("/pr_test")
    public String pr_test() {
        return "pr_test";
    }

    @GetMapping("/myresult")
    public String myResult(
            @RequestParam(value = "userId", required = false) Long userId,
            Model model) {

        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userId = userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }

        List<ScoreDTO> previousScores = scoreService.getPreviousScores(userId)
                .stream()
                .map(ScoreDTO::fromEntity)
                .toList();

        List<Score2DTO> previousScores2 = score2Service.getPreviousScores(userId)
                .stream()
                .map(Score2DTO::fromEntity)
                .toList();

        Map<String, Object> evaluatingScore = scoreService.getEvaluatingScore(userId);
        Map<String, Object> evaluatingScore2 = score2Service.getEvaluatingScore(userId);

        model.addAttribute("previousScores", previousScores);
        model.addAttribute("previousScores2", previousScores2);
        model.addAttribute("evaluatingScore", evaluatingScore);
        model.addAttribute("evaluatingScore2", evaluatingScore2);

        return "myresult";
    }
}
