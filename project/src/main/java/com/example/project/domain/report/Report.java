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
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer report_type;

    @Column(nullable = false, name = "reported_id")
    private Long reportedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user", nullable = false)
    private User reported_user;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer processing_state;

    @Column(nullable = true)
    private LocalDateTime date_processing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportDetails report_details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ReportProcessTypes report_process_type;
}