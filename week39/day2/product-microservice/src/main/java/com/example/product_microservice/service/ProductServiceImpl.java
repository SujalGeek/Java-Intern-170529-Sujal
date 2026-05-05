package com.example.product_microservice.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.product_microservice.rest.CreateProductRestModel;
import com.example.product_microservice.rest.ProductCreatedEvent;

@Service
public class ProductServiceImpl implements ProductService {
	
	private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@Override
	public String createProduct(CreateProductRestModel productRestModel) {
		
		String productId = UUID.randomUUID().toString();
		
		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
				productId,
				productRestModel.getTitle(),
				productRestModel.getPrice(),
				productRestModel.getQuantity()
				);
		
		CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
				kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
		
		future.whenComplete((result, exception) -> {
			if (exception != null) {
				LOGGER.error("Failed to send message: " + exception.getMessage());
			} else {
				LOGGER.info("Message sent successfully: " + result.getRecordMetadata());
			}
		});
		
		return productId;
	}

	// This is the listener that will verify the message was received by Kafka
	@KafkaListener(topics = "product-created-events-topic", groupId = "force-read-group-101")
	public void listen(ProductCreatedEvent event) {
	    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    System.err.println("!!! KAFKA VERIFIED - RECEIVED: " + event.getTitle());
	    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}