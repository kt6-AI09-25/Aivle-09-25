package com.example.project.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Integer login_type;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime last_login;

    @Column(nullable = false)
    private LocalDateTime date_join;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Integer state;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Integer letter_state;

    @Column(nullable = true)
    private LocalDateTime ban_end_time;
}