package com.example.project.domain.letter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Sort;
@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByReceiver_Id(Long receiverId, Sort sort);

    List<Letter> findBySender_Id(Long senderId, Sort sort);

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-10 13:35 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    Letter findByLetterId(Long letterId);
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-10 13:35 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
