package com.example.project.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionTimesRepository extends JpaRepository<ExpressionTimes, Long> {
}