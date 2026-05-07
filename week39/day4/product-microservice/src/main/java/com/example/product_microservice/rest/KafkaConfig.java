package com.example.product_microservice.rest;

import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
	NewTopic createTopic() {
		return TopicBuilder.name("product-created-events-topic-v4")
				.partitions(3)
				.replicas(3)
				.config("min.insync.replicas","2")
				.build();
	}
	
	
	@Bean
	NewTopic deadLetterTopic() {
		return TopicBuilder.name("product-created-events-topic-v4-dlt")
				.partitions(3)
				.replicas(3)
				.config("min.insync.replicas", "2")
				.build();
	}
}
