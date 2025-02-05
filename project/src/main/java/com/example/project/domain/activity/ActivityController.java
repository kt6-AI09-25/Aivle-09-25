package com.example.project.domain.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityRepository activityRepository;

    @GetMapping("/recent-activities")
    public List<ActivityDTO> getRecentActivities() {
        return activityRepository.findRecentActivities();
    }
}