package com.example.project.domain.letter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByReceiver_Id(Long receiverId);

    List<Letter> findBySender_Id(Long senderId);
}