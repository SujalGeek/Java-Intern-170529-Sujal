package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private ActivityAIService activityAIService;

    // FORCE A NEW GROUP ID HERE. Do not use ${...}
    @KafkaListener(
            topics = "activity-events",
            groupId = "debug-single-group"
    )
    public void processActivity(Activity activityJson) {
        log.info("SUCCESS (String): Received Raw JSON: {}", activityJson);
        activityAIService.generateRecommendation(activityJson);
    }
}