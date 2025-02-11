package com.example.project.domain.score2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "head_times")
public class HeadTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long headId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score2_id", nullable = false)
    private Score2 score2;

    @Column(name = "head_direction", nullable = false)
    private String headDirection;

    @Column(name = "action_time", nullable = false)
    private Double actionTime;
}
