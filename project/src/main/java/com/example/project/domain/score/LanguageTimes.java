package com.example.project.domain.score;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "language_times")
public class LanguageTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long languageId;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @Column(name = "language_name", nullable = false, length = 50)
    private String languageName;

    @Column(name = "language_time", nullable = false)
    private Double languageTime;

}