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
@Table(name = "expression_times")
public class ExpressionTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expressionId;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @Column(name = "expression_name", nullable = false, length = 50)
    private String expressionName;

    @Column(name = "expression_time", nullable = false)
    private Double expressionTime;

}
