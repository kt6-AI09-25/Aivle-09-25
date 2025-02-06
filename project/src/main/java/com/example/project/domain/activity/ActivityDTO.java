package com.example.project.domain.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@AllArgsConstructor
public class ActivityDTO {
    private String username;
    private String action;
    private Long targetId;
    private Timestamp createdAt;

    public String getFormattedCreatedAt() {
        return createdAt.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
