package com.example.project.domain.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class UserUtils {

    public static Integer getCurrentUserState() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getState();
        }
        return null; // 비로그인 상태
    }

    public static LocalDateTime getCurrentUserBanEndTime() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getBanEndTime();
        }
        return null; // 비로그인 상태
    }
}