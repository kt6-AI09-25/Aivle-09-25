package com.example.project.domain.comment;

import com.example.project.domain.noticeboard.NoticeBoard;
import com.example.project.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private NoticeBoard post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id", nullable = false)
    private User commenter;

    @Column(nullable = false, length = 512)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime dateComment;

    @Column(nullable = false)
    private Boolean isEdited = false;
}
