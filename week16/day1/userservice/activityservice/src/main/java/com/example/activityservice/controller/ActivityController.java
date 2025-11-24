package com.example.activityservice.controller;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.services.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;


    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.trackActivity(activityRequest));
    }
}