package com.example.project.domain.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityRepository activityRepository;

    @GetMapping("/recent-activities")
    public List<ActivityDTO> getRecentActivities() {
        return activityRepository.findRecentActivities()
                .stream()
                .map(activity -> new ActivityDTO(
                        activity.getUsername(),
                        activity.getAction(),
                        activity.getTargetId(),
                        activity.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}