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

    @Value("${products.commands.topic-name}")
    private String productsCommandsTopicName;

    private final static Integer TOPIC_PARTITIONS = 3;
    private final static Integer TOPIC_REPLICATIONS = 3;



    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createProductTopic(){
        return TopicBuilder.name("product-commands")
                .replicas(TOPIC_REPLICATIONS)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }

    @Bean
    NewTopic createProductCommandsTopic() {
        return TopicBuilder.name(productsCommandsTopicName)
                .replicas(TOPIC_REPLICATIONS)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }
}
