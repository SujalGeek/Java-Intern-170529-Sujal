package com.example.activityservice.services;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.model.Activity;
import com.example.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String,Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    // --- NEW METHOD FOR GET REQUEST ---
    public List<ActivityResponse> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        // ... (Keep your existing validation and logic here) ...
        Boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());
        if (!isValidUser) {
            throw new RuntimeException("Invalid User!!!" + activityRequest.getUserId());
        }
        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .startTime(activityRequest.getStartTime())
                .type(activityRequest.getType())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        try {
            CompletableFuture<SendResult<String, Activity>> future =
                    kafkaTemplate.send(topicName, savedActivity.getUserId(), savedActivity);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent activity for user: {} with offset: {}",
                            savedActivity.getUserId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send activity for user: {}",
                            savedActivity.getUserId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Kafka Send Exception", e);
        }
        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(savedActivity.getId());
        activityResponse.setUserId(savedActivity.getUserId());
        activityResponse.setType(savedActivity.getType());
        activityResponse.setCaloriesBurned(savedActivity.getCaloriesBurned());
        activityResponse.setDuration(savedActivity.getDuration());
        activityResponse.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        activityResponse.setStartTime(savedActivity.getStartTime());
        activityResponse.setCreatedAt(savedActivity.getCreatedAt());
        activityResponse.setUpdatedAt(savedActivity.getUpdatedAt());

        return activityResponse;
    }
}