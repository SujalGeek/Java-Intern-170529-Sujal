package com.example.course_service.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SreTestController {

    private final Counter errorCounter;

    // Yahan humne MeterRegistry ka parameter hata diya hai!
    public SreTestController() {
        // Ab hum Global Registry use kar rahe hain, ye kabhi crash nahi hogi
        this.errorCounter = Counter.builder("core_errors")
                .description("Total number of java errors for SRE test in Course Service")
                .register(Metrics.globalRegistry);
    }

    @GetMapping("/test-course-error")
    public String causeCourseError() {
        errorCounter.increment(); 
        throw new RuntimeException("Simulated bug in Course Service");
    }
}