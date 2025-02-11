package com.example.project.domain.score2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "language_times2")
public class LanguageTimes2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long language2Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score2_id", nullable = false)
    private Score2 score2;

    @Column(name = "language_name", nullable = false, length = 50)
    private String languageName;

    @Column(name = "language_time", nullable = false)
    private Double languageTime;
}
