package com.example.project.domain.noticeboard;

import com.example.project.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notice_board")
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private LocalDateTime dateWrite;

    @Column(nullable = false)
    private Boolean isEdited = false;

    @PrePersist
    public void prePersist() {
        this.dateWrite = LocalDateTime.now();
        this.viewCount = 0L;
        this.isEdited = false;
    }
}
