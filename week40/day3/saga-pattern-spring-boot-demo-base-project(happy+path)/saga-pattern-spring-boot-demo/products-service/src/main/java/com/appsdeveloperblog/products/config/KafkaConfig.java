package com.appsdeveloperblog.products.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${products.events.topic-name}")
    private String productsEventsTopicName;

    private final static Integer TOPIC_PARTITIONS = 3;
    private final static Integer TOPIC_REPLICATIONS = 3;



    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createProductTopic(){
        return TopicBuilder.name(productsEventsTopicName)
                .replicas(TOPIC_REPLICATIONS)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }
}
