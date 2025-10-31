package com.example.monitoring_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LoggingController {

    private static final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @GetMapping("/test")
    public String testLogs(@RequestParam(defaultValue = "info") String level) {
        switch (level.toLowerCase()) {
            case "debug" -> logger.debug("DEBUG: Debug log triggered");
            case "warn" -> logger.warn("WARNING: Something might be wrong!");
            case "error" -> logger.error("ERROR: A simulated error occurred!");
            default -> logger.info("INFO: Default info log");
        }
        return "Logs generated at level: " + level.toUpperCase();
    }
}
