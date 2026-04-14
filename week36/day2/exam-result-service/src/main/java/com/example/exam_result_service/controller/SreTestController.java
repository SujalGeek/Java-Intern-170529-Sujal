package com.example.exam_result_service.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SreTestController {

    private final Counter errorCounter;

    public SreTestController(MeterRegistry registry) {
        // Ye wahi metric hai jo Prometheus dekhega: "core_errors_total"
        this.errorCounter = Counter.builder("core_errors")
                .description("Total number of java errors for SRE test")
                .register(registry);
    }

    @GetMapping("/cause-error")
    public String causeError() {
        // Humne error counter badha diya
        errorCounter.increment();
        
        // Asli error trigger kar rahe hain
        int result = 10 / 0; 
        return "Result: " + result;
    }
    
    
}