package com.example.project.domain.score2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "expression_times2")
public class ExpressionTimes2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expression2Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score2_id", nullable = false)
    private Score2 score2;

    @Column(name = "expression_name", nullable = false, length = 50)
    private String expressionName;

    @Column(name = "expression_time", nullable = false)
    private Double expressionTime;
}
