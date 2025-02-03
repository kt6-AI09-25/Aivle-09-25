package com.example.project.domain.user;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/register")
    @CrossOrigin(origins = "http://aivle-ai9-25-bigproject-buasgtbpbgh8aagh.koreacentral-01.azurewebsites.net")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setNickname(nickname);
        newUser.setRole("USER");
        newUser.setLast_login(LocalDateTime.now());
        newUser.setDate_join(LocalDateTime.now());
        newUser.setLogin_type(0); // 로그인 타입 기본값
        newUser.setState(1); // 상태 기본값
        newUser.setLetter_state(0);
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }
}