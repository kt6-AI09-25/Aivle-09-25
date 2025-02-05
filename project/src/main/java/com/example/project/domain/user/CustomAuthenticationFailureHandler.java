package com.example.project.domain.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorMessage;
        if (exception.getMessage().contains("Maximum sessions")) {
            // 중복 로그인 시도 처리
            errorMessage = "중복 로그인이 감지되었습니다. 이미 로그인 중입니다.";
        } else if (exception.getMessage().contains("Bad credentials")) {
            // 아이디 또는 비밀번호 틀림 처리
            errorMessage = "아이디 또는 비밀번호가 잘못되었습니다.";
        } else {
            // 기타 로그인 실패 처리
            errorMessage = "로그인에 실패했습니다. 관리자에게 문의하세요.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + errorMessage + "\"}");
    }
}