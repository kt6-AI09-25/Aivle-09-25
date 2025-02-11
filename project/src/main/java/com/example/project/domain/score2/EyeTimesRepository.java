package com.example.project.domain.score2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EyeTimesRepository extends JpaRepository<EyeTimes, Long> {
}
