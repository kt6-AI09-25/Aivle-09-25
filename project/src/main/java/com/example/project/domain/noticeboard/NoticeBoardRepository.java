package com.example.project.domain.noticeboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    NoticeBoard findByPostId(Long postId);

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-16 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    List<NoticeBoard> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword);

    List<NoticeBoard> findByWriter_UsernameContainingIgnoreCase(String usernameKeyword);
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-16 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-03 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Query("SELECT COUNT(n) FROM NoticeBoard n")
    long countAllPosts();

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-03 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}