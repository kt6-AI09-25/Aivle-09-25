package com.example.project.domain.report;

import java.time.LocalDateTime;

public class AdminDTO {
    private final String username;
    private final String nickname;
    private final LocalDateTime dateJoin;
    private final LocalDateTime lastLogin;
    private final long postCount; // 게시글 수 추가
    private final String role;

    public AdminDTO(String username, String nickname, LocalDateTime dateJoin, LocalDateTime lastLogin, long postCount, String role) {
        this.username = username;
        this.nickname = nickname;
        this.dateJoin = dateJoin;
        this.lastLogin = lastLogin;
        this.postCount = postCount;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getNickname() { return nickname; }
    public LocalDateTime getDateJoin() { return dateJoin; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public long getPostCount() { return postCount; }
    public String getRole() { return role; }
}

