package com.example.project.domain.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
public class ActivityDTO {
    private String username;
    private String action;
    private Long targetId;
    private Timestamp createdAt;
}
