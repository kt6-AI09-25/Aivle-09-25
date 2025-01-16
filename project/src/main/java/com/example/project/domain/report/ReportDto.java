package com.example.project.domain.report;

import com.example.project.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long report_id;
        private Long reporter_id;
        private String reporter_name;
        private Integer report_type;
        private Long reported_id;
        private Integer processing_state;
        private LocalDateTime date_processing;
        private ReportDetails report_details;
        private ReportProcessTypes report_process_type;
        private User reported_user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long reported_id;
        private Integer report_type;
        private ReportDetails report_details;
    }
}
