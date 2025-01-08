package com.example.project.domain.report;

import com.example.project.domain.noticeboard.NoticeBoardRepository;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final UserRepository userRepository;

    public List<ReportDto.Response> getAllReports() {
        List<ReportDto.Response> reports = reportRepository.findAll()
                .stream()
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
        newReport.setReported_id(request.getReported_id());
        newReport.setReport_details(request.getReport_details());
        newReport.setReporter(reporter);
        newReport.setReport_type(1);
        newReport.setProcessing_state(0);

        Report savedReport = reportRepository.save(newReport);
        return convertToResponseDTO(savedReport);
    }

    private ReportDto.Response convertToResponseDTO(Report report) {
        return ReportDto.Response.builder()
                .report_id(report.getReport_id())
                .reporter_id(report.getReporter().getId())
                .reporter_name(report.getReporter().getUsername())
                .reported_id(report.getReported_id())
                .processing_state(report.getProcessing_state())
                .date_processing(report.getDate_processing())
                .report_details(report.getReport_details())
                .build();
    }
}