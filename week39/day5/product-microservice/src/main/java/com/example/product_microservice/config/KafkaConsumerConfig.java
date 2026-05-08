package com.example.product_microservice.config;

//import com.example.product_microservice.rest.ProductCreatedEvent; 
import com.example.core.event.ProductCreatedEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {


	@Bean(name = "kafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> kafkaListenerContainerFactory(
			KafkaTemplate<String, Object> kafkaTemplate
			)
	{
		Map<String, Object> map = new HashMap<>();
		
		map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		map.put(ConsumerConfig.GROUP_ID_CONFIG, "debug-group-v61");
		map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		map.put(
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
				, StringDeserializer.class);
		
		map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		
		map.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, 
				JsonDeserializer.class);
		
        map.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.example.core.event"
        );
        
        
        map.put(JsonDeserializer.VALUE_DEFAULT_TYPE, 
        		"com.example.core.event.ProductCreatedEvent");
        
        map.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );
        
        DefaultKafkaConsumerFactory<String, ProductCreatedEvent> consumerFactory =
        		new DefaultKafkaConsumerFactory<>(map);
        
        ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> factory = 
        		new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory);
        
//        factory.setCommonErrorHandler(new DefaultErrorHandler());
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        
        DefaultErrorHandler errorHandler = 
        		new DefaultErrorHandler(
        				recoverer,
        				new FixedBackOff(2000L,3)
        				);
        
        factory.setCommonErrorHandler(errorHandler);
        
        factory.getContainerProperties().setMissingTopicsFatal(false);
        
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        
        return factory;
	}
	
}
