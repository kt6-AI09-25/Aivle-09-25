package com.example.project.domain.activity;

import com.example.project.domain.noticeboard.NoticeBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<NoticeBoard, Long> {

    @Query(value = """
        (SELECT u.username AS username, '게시글을 작성했습니다' AS action, n.post_id AS targetId, n.date_write AS createdAt
         FROM notice_board n 
         JOIN `user` u ON n.writer_id = u.id
         ORDER BY n.date_write DESC 
         LIMIT 5)
        UNION ALL
        (SELECT u.username AS username, '댓글을 작성했습니다' AS action, c.post_id AS targetId, c.date_comment AS createdAt
         FROM comment c 
         JOIN `user` u ON c.commenter_id = u.id
         ORDER BY c.date_comment DESC 
         LIMIT 5)
        UNION ALL
        (SELECT u.username AS username, '회원가입을 했습니다' AS action, NULL AS targetId, u.date_join AS createdAt
         FROM `user` u 
         ORDER BY u.date_join DESC 
         LIMIT 5)
        UNION ALL
        (SELECT u.username AS username, '발표를 등록했습니다' AS action, s.score_id AS targetId, s.date AS createdAt
         FROM score s 
         JOIN `user` u ON s.user_id = u.id
         ORDER BY s.date DESC 
         LIMIT 5)
          UNION ALL
        (SELECT u.username AS username, '면접평가를 등록했습니다' AS action, s2.score2id AS targetId, s2.date AS createdAt
         FROM score2 s2 
         JOIN `user` u ON s2.user_id = u.id
         ORDER BY s2.date DESC 
         LIMIT 5)
        ORDER BY createdAt DESC
        LIMIT 5
        """, nativeQuery = true)
    List<ActivityDTO> findRecentActivities();
}
