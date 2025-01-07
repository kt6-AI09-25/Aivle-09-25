package com.example.project.domain.comment;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long commentId;
        private Long postId;
        private Long commenterId;
        private String commenterName;
        private String comment;
        private LocalDateTime dateComment;
        private Boolean isEdited;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long postId;
        private String comment;
    }
}
