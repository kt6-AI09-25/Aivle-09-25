package com.example.project.domain.noticeboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    NoticeBoard findByPostId(Long postId);

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-04 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    List<NoticeBoard> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword);

    List<NoticeBoard> findByWriter_UsernameContainingIgnoreCase(String usernameKeyword);

    @Query("SELECT COUNT(n) FROM NoticeBoard n")
    long countAllPosts();

    Page<NoticeBoard> findAll(Pageable pageable);

    @Query("SELECT COUNT(n) FROM NoticeBoard n WHERE n.writer.username = :username")
    long countUsersPosts(@Param("username") String username);
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-04 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}