package com.example.project.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserUsername(String username);
}
