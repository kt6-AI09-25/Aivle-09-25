package com.example.project.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(r) FROM Report r WHERE processing_state = 0")
    long countUnprocessedReports();

    @Query("SELECT r FROM Report r WHERE r.reportId = :reportId")
    Report findByReportId(@Param("reportId") Long reportId);

    @Query("SELECT r FROM Report r WHERE r.reportedId = :reportedId")
    Optional<Report> findByReportedId(@Param("reportedId") Long reportedId);
}
