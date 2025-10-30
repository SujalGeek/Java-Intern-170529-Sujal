package com.example.monitoring_demo.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final MeterRegistry meterRegistry;

    public MetricController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("custom_request_counter", counter);
    }

    @GetMapping("/increment")
    public String incrementCounter() {
        int value = counter.incrementAndGet();
        return "Metric counter incremented: " + value;
    }
}
