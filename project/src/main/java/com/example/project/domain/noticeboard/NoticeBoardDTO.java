package com.example.project.domain.noticeboard;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeBoardDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long postId;
        private String title;
        private String content;
        private Long viewCount;
        private Long writerId;
        private String writerName;
        private LocalDateTime dateWrite;
        private Boolean isEdited;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String content;
    }
}
