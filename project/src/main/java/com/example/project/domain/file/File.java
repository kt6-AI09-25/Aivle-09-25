package com.example.project.domain.file;

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
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false)
    private String filePath; // 저장 경로

    @Column(nullable = false)
    private LocalDateTime uploadedAt; // 업로드 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false) // 외래 키
    private User user; // 파일을 업로드한 사용자

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
