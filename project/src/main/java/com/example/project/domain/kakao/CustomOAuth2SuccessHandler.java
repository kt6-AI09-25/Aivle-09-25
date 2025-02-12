package com.example.project.domain.kakao;

import com.example.project.domain.user.CustomUserDetails;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomOAuth2SuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.sendRedirect("/");
            return;
        }

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));


        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getState(),
                user.getBan_end_time(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())),
                oAuth2User.getAttributes()
        );


        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        request.getSession().setMaxInactiveInterval(30 * 60); // 30분 유지

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Login successful");
        responseData.put("role", user.getRole());

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    @Transactional
    private User createNewUser(String email, String nickname) {
        logger.info("신규 사용자 생성: {}", email);
        User newUser = new User();
        newUser.setUsername(email);
        newUser.setNickname(nickname);
        newUser.setPassword("_"); // 소셜 로그인 사용자는 비밀번호 필요 없음
        newUser.setRole("USER");
        newUser.setLogin_type(1); // 소셜 로그인 타입
        newUser.setDate_join(LocalDateTime.now());
        newUser.setLast_login(LocalDateTime.now());
        newUser.setState(1); // 기본 상태
        newUser.setLetter_state(0); // 기본값 설정
        return userRepository.save(newUser);
    }
}