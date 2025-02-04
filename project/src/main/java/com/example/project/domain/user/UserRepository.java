package com.example.project.domain.user;

import com.example.project.domain.noticeboard.NoticeBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);


    @Query("SELECT COUNT(u) FROM User u WHERE u.date_join BETWEEN :startOfDay AND :endOfDay")
    long countUsersByDateJoinBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
}