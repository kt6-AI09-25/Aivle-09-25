package com.example.project.domain.report;

import com.example.project.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long report_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Integer report_type;

    @Column(nullable = false)
    private Long reported_id;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Integer processing_state;

    @Column(nullable = true)
    private LocalDateTime date_processing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportDetails report_details;
}
