package com.appsdeveloperblog.orders.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

//    order.events.topic-name

    @Value("${orders.events.topic-name}")
    private String orderEventTopicName;

    @Value("${products.commands.topic-name}")
    private String productsCommandsName;

    @Value("${payments.commands.topic-name}")
    private String paymentsCommandsTopicName;

    @Value("${orders.commands.topic-name}")
    private String ordersCommandsTopicName;


    private final static Integer TOPIC_REPLICATION = 3;
    private final static Integer TOPIC_PARTITIONS = 3;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createTopic(){
        return TopicBuilder.name(orderEventTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION)
                .build();
    }

    @Bean
    NewTopic createProductsCommandsTopic(){
        return TopicBuilder.name(productsCommandsName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION)
                .build();
    }

    @Bean
    NewTopic createPaymentsCommandsTopic(){
        return TopicBuilder.name(paymentsCommandsTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION)
                .build();
    }

    @Bean
    NewTopic createOrdersCommandsTopic(){
        return TopicBuilder.name(ordersCommandsTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION)
                .build();
    }

}
