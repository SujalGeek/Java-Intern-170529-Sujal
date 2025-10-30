package com.example.monitoring_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> healthStatus(){
        Map<String,Object> status = new HashMap<>();
        status.put("status","UP");
        status.put("timestamp",System.currentTimeMillis());
        status.put("customCheck","Database and check are running fine");
        return status;
    }
}
