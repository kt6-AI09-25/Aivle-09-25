package com.example.project.domain.score;

import com.example.project.domain.file.File;
import com.example.project.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "motion_score", nullable = false)
    private Double motionScore;

    @Column(name = "expression_score", nullable = false)
    private Double expressionScore;

    @Column(name = "language_score", nullable = false)
    private Double languageScore;

    @Column(name = "motion_frequency", nullable = false, length = 50)
    private String motionFrequency;

    @Column(name = "expression_frequency", nullable = false, length = 50)
    private String expressionFrequency;

    @Column(name = "language_frequency", nullable = false, length = 50)
    private String languageFrequency;

    @OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MotionTimes> motionTimes;

    @OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpressionTimes> expressionTimes;

    @OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LanguageTimes> languageTimes;

    @Column(name = "tempo", nullable = false)
    private Double tempo;

    @Column(name = "script", nullable = false)
    private String script;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime date;

    @PrePersist
    public void prePersist() {
        this.date = LocalDateTime.now();
    }
}
