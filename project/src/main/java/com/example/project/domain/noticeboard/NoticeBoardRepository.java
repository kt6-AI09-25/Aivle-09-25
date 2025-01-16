package com.example.project.domain.noticeboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    NoticeBoard findByPostId(Long postId);
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-16 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    List<NoticeBoard> findByTitleContainingIgnoreCase(String titleKeyword);
    List<NoticeBoard> findByContentContainingIgnoreCase(String contentKeyword);
    List<NoticeBoard> findByWriter_UsernameContainingIgnoreCase(String usernameKeyword);
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-16 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
