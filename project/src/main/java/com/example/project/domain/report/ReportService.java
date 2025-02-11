package com.example.project.domain.report;

import com.example.project.domain.comment.CommentService;
import com.example.project.domain.letter.LetterService;
import com.example.project.domain.noticeboard.NoticeBoardService;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;
    private final LetterService letterService;

    public List<ReportDto.Response> getAllReports() {
        List<ReportDto.Response> reports = reportRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Report::getProcessing_state))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return reports;
    }

    @Transactional
    public ReportDto.Response createReport(ReportDto.Request request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다."));


        Report newReport = new Report();
        newReport.setReportedId(request.getReported_id());
        newReport.setReport_details(request.getReport_details());
        newReport.setReporter(reporter);
        newReport.setReport_type(request.getReport_type());
        newReport.setProcessing_state(0);
        newReport.setReport_process_type(ReportProcessTypes.처리대기);

        User reportedUser;
        if (request.getReport_type() == 1) {
            reportedUser = noticeBoardService.getWriterByPostId(request.getReported_id());
        } else if (request.getReport_type() == 2) {
            reportedUser = commentService.getWriterByCommentId(request.getReported_id());
        } else if (request.getReport_type() == 3) {
            reportedUser = letterService.getSenderByLetterId(request.getReported_id());
        }else {
            throw new IllegalArgumentException("Unknown report type: " + request.getReport_type());
        }
        newReport.setReported_user(reportedUser);


        Report savedReport = reportRepository.save(newReport);
        return convertToResponseDTO(savedReport);
    }

    private void processReportedUser(User user, ReportProcessTypes processType) {
        user.setState(0);

        if (processType == ReportProcessTypes.삼일_작성정지) {
            user.setBan_end_time(LocalDateTime.now().plusMinutes(3));
        } else if (processType == ReportProcessTypes.일주일_작성정지) {
            user.setBan_end_time(LocalDateTime.now().plusMinutes(7));
        } else if (processType == ReportProcessTypes.계정삭제) {
            user.setBan_end_time(LocalDateTime.now().plusMinutes(30));
        } else {
            throw new IllegalArgumentException("Unknown ReportProcessType");
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateReportProcessTypeAndState(Long reportId, ReportProcessTypes processType) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));


        processReportedUser(report.getReported_user(), processType);

        // Update fields
        report.setDate_processing(LocalDateTime.now());
        report.setReport_process_type(processType);
        report.setProcessing_state(1); // Mark as processed

        // Save updated report
        reportRepository.save(report);
    }

    private ReportDto.Response convertToResponseDTO(Report report) {
        return ReportDto.Response.builder()
                .report_id(report.getReportId())
                .reporter_id(report.getReporter().getId())
                .reporter_name(report.getReporter().getUsername())
                .report_type(report.getReport_type())
                .reported_id(report.getReportedId())
                .processing_state(report.getProcessing_state())
                .date_processing(report.getDate_processing())
                .report_details(report.getReport_details())
                .report_process_type(report.getReport_process_type())
                .reported_user(report.getReported_user())
                .build();
    }

    public long getUnprocessedReports() {
        return reportRepository.countUnprocessedReports();
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findByReportId(reportId);
        reportRepository.delete(report);
    }
}