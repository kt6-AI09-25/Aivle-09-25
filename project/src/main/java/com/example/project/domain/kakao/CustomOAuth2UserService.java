package com.example.project.domain.kakao;

import com.example.project.domain.user.CustomUserDetails;
import com.example.project.domain.user.CustomUserDetailsService;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public CustomOAuth2UserService(UserRepository userRepository, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();

            if (attributes == null || attributes.isEmpty()) {
                throw new OAuth2AuthenticationException("카카오에서 사용자 정보를 가져오지 못했습니다.");
            }

            Map<String, Object> kakaoAccount = castToMap(attributes.get("kakao_account"));
            Map<String, Object> properties = castToMap(attributes.get("properties"));

            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            String nickname = properties != null ? (String) properties.get("nickname") : null;

            if (email == null) {
                throw new OAuth2AuthenticationException("카카오 이메일 정보가 없습니다.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            User user = userRepository.findByUsername(email).orElseGet(() -> createNewUser(email, nickname));

            return new CustomUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.getState(),
                    user.getBan_end_time(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole())),
                    attributes
            );

        } catch (OAuth2AuthenticationException e) {
            throw e;
        }
    }


    @Transactional
    private User createNewUser(String email, String nickname) {
        logger.info("신규 사용자 생성: {}", email);
        User newUser = new User();
        newUser.setUsername(email);
        newUser.setNickname(nickname);
        newUser.setPassword("_"); // 비밀번호는 사용하지 않음
        newUser.setRole("USER");
        newUser.setLogin_type(1); // 소셜 로그인 타입
        newUser.setDate_join(LocalDateTime.now());
        newUser.setLast_login(LocalDateTime.now());
        newUser.setState(1); // 기본 상태
        newUser.setLetter_state(0); // 기본값 설정
        return userRepository.save(newUser);
    }

    // 안전한 Map 캐스팅 유틸리티 메서드
    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null; // 캐스팅 불가능한 경우 null 반환
    }
}