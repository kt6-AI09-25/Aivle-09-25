package com.example.project.domain.noticeboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    NoticeBoard findByPostId(Long postId);
}
