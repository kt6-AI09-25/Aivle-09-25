package com.example.project.domain.score2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "eye_times")
public class EyeTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eyeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score2_id", nullable = false)
    private Score2 score2;

    @Column(name = "eye_direction", nullable = false)
    private String eyeDirection;

    @Column(name = "action_time", nullable = false)
    private Double actionTime;
}
