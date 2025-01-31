package com.example.project.domain.letter;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LetterDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String receiverUsername;
        private String letterContent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long letterId;
        private Long senderId;
        private String senderName;
        private Long receiverId;
        private String receiverUsername;
        private String letterContent;
        private LocalDateTime dateSend;
        private Boolean state;
    }
}
