package com.example.project.domain.score2;

import com.example.project.domain.file.File;
import com.example.project.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "score2")
public class Score2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long score2Id;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "eyehead_score", nullable = false)
    private Double eyeheadScore;

    @Column(name = "expression_score", nullable = false)
    private Double expressionScore;

    @Column(name = "language_score", nullable = false)
    private Double languageScore;

    @Column(name = "expression_frequency", nullable = false, length = 50)
    private String expressionFrequency;

    @Column(name = "language_frequency", nullable = false, length = 50)
    private String languageFrequency;

    @OneToMany(mappedBy = "score2", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EyeTimes> eyeTimes;

    @OneToMany(mappedBy = "score2", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HeadTimes> headTimes;

    @OneToMany(mappedBy = "score2", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpressionTimes2> expressionTimes;

    @OneToMany(mappedBy = "score2", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LanguageTimes2> languageTimes;

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
