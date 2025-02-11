package com.example.project.domain.score2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadTimesRepository extends JpaRepository<HeadTimes, Long> {
}
