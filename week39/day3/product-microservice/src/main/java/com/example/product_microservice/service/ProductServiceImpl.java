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
	public String createProduct(CreateProductRestModel productRestModel) throws Exception {
		
		String productId = UUID.randomUUID().toString();
		
		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
				productId,
				productRestModel.getTitle(),
				productRestModel.getPrice(),
				productRestModel.getQuantity()
				);
		// For asynchronous we have to run this part of code
//		CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
//				kafkaTemplate.send("product-created-events-topic-v2", productId, productCreatedEvent);
//		
//		future.whenComplete((result, exception) -> {
//			if (exception != null) {
//				LOGGER.error("Failed to send message: " + exception.getMessage());
//			} else {
//				LOGGER.info("Message sent successfully: " + result.getRecordMetadata());
//			}
//		});
//		
//		For Synchronous Communication this part of code should be running
		// we can just add the future.join() it can work but we are learning it that why using this below code
		
		LOGGER.info("Before publishing a ProductCreatedEvent");
		
		SendResult<String, ProductCreatedEvent> result = 
				kafkaTemplate.send("product-created-events-topic-v4",productId,productCreatedEvent).get();
		LOGGER.info("Parition: "+result.getRecordMetadata().partition());
		LOGGER.info("Topic: "+result.getRecordMetadata().topic());
		LOGGER.info("Offset: "+result.getRecordMetadata().offset());

		
		LOGGER.info("*** Returning the product Id ***");
		
		return productId;
	}

	// This is the listener that will verify the message was received by Kafka
	@KafkaListener(
		    topics = "product-created-events-topic-v4",
		    containerFactory = "kafkaListenerContainerFactory"
		)
		public void listen(ProductCreatedEvent event) {

		    System.err.println(" RECEIVED EVENT ");
		    System.err.println(event.getTitle());
		}
}