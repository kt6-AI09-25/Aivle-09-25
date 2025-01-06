package com.example.project.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입 페이지
    @GetMapping("/register")
    public String showRegisterForm(){return "register";}

    //회원가입 처리
    @PostMapping("/register")
    public String registerUser(String username, String password, String nickname, Model model){
        if (userRepository.findByUsername(username).isPresent()){
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setLogin_type(0);
        newUser.setNickname(nickname);
        newUser.setRole("ROLE_USER");
        newUser.setLast_login(LocalDateTime.now());
        newUser.setDate_join(LocalDateTime.now());
        newUser.setState(1);
        newUser.setLetter_state(0);

        userRepository.save(newUser);

        return "redirect:/login";
    }
}
