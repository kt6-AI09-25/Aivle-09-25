package com.example.project.domain.testpage;

import com.example.project.domain.score.Score;
import com.example.project.domain.score.ScoreDTO;
import com.example.project.domain.score.ScoreRepository;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {


    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {return "admin";}

    @GetMapping("/test")
    public String testPage() {
        return "test"; // templates/test.html을 렌더링
    }

    @GetMapping("/result")
    public String result() {
        return "result"; // result.html 반환
    }


    @GetMapping("/board")
    public String board() {
        return "board"; // test.html 반환
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


        List<ScoreDTO> previousScores = scoreRepository.findByUserIdWithDetails(userId)
                .stream()
                .map(ScoreDTO::fromEntity)
                .toList();

        System.out.println("=== 사용자 ID: " + userId + " ===");
        System.out.println("=== previousScores 조회 결과 ===");
        previousScores.forEach(score -> System.out.println("점수 ID: " + score.getScoreId() + " | 총 점수: " + score.getTotalScore()));

        model.addAttribute("previousScores", previousScores);

        return "myresult";
    }


}


