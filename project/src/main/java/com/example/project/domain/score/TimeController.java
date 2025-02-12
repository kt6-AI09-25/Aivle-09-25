package com.example.project.domain.score;

import com.example.project.domain.score.TimeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class TimeController {

    private final TimeService scoreTimeService;

    public TimeController(TimeService TimeService) {
        this.scoreTimeService = TimeService;
    }

    @GetMapping("/times")
    public List<Double> getTimes(
            @RequestParam String scoreType,
            @RequestParam Long scoreId,
            @RequestParam String frequencyValue) {
        return scoreTimeService.getTimes(scoreType, scoreId, frequencyValue);
    }
}
