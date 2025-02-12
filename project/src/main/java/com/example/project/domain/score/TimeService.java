package com.example.project.domain.score;

import com.example.project.domain.score.MotionTimesRepository;
import com.example.project.domain.score.ExpressionTimesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeService {

    private final MotionTimesRepository motionTimesRepository;
    private final ExpressionTimesRepository expressionTimesRepository;

    public TimeService(MotionTimesRepository motionTimesRepository, ExpressionTimesRepository expressionTimesRepository) {
        this.motionTimesRepository = motionTimesRepository;
        this.expressionTimesRepository = expressionTimesRepository;
    }

    public List<Double> getTimes(String scoreType, Long scoreId, String frequencyValue) {
        switch (scoreType) {
            case "motion":
                return motionTimesRepository.findActionTimesByScoreIdAndActionName(scoreId, frequencyValue);

            case "expression":
                return expressionTimesRepository.findExpressionTimesByScoreIdAndExpressionName(scoreId, frequencyValue);

            default:
                throw new IllegalArgumentException("Invalid scoreType: " + scoreType);
        }
    }
}
