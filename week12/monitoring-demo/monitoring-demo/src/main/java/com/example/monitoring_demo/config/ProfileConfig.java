package com.example.monitoring_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfileConfig {

    @Bean
    @Profile("dev")
    public String doBean(){
        System.out.println("Loaded development bean");
        return "Development Bean";
    }

    @Bean
    @Profile("prod")
    public String prodBean(){
        System.out.println("Loaded the production profile bean");
    return "Production Bean";
    }

    @Bean
    @Profile("test")
    public String testBean(){
        System.out.println("Loaded test profile bean");
    return "Test Bean";
    }
}
