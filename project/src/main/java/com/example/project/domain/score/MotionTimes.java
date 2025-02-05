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
@Table(name = "motion_times")
public class MotionTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long motionId;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @Column(name = "action_name", nullable = false, length = 50)
    private String actionName;

    @Column(name = "action_time", nullable = false)
    private Double actionTime;

}