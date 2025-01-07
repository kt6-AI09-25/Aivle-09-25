package com.example.project.domain.letter;

import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/letters")
public class LetterController {

    private final LetterService letterService;
    private final UserRepository userRepository;

    // 수신한 편지 목록
    @GetMapping("/received")
    public String getReceivedLetters(Model model) {
        Long userId = getCurrentUserId();
        List<LetterDTO.Response> letters = letterService.getReceivedLetters(userId);
        model.addAttribute("letters", letters);
        return "letters/received";
    }

    // 발신한 편지 목록
    @GetMapping("/sent")
    public String getSentLetters(Model model) {
        Long userId = getCurrentUserId();
        List<LetterDTO.Response> letters = letterService.getSentLetters(userId);
        model.addAttribute("letters", letters);
        return "letters/sent";
    }

    // 편지 작성 폼
    @GetMapping("/new")
    public String createLetterForm(Model model) {
        model.addAttribute("letter", new LetterDTO.Request());
        return "letters/createform";
    }

    // 편지 전송
    @PostMapping
    public String sendLetter(@ModelAttribute LetterDTO.Request request) {
        Long senderId = getCurrentUserId();
        letterService.sendLetter(request, senderId);
        return "redirect:/letters/sent";
    }

    // 현재 인증된 사용자 ID 가져오기
    private Long getCurrentUserId() {
        // 현재 인증된 사용자 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .map(user -> user.getId()) // User 엔티티에서 ID를 추출
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다: " + username));
    }
}
