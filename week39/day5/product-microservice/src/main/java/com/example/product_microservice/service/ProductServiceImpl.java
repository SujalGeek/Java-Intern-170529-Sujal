package com.example.product_microservice.service;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID; 
//import java.util.concurrent.CompletableFuture;

//import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.example.product_microservice.entity.ProcessedEvent;
import com.example.product_microservice.exception.NonRetryableException;
import com.example.product_microservice.exception.RetryableException;
import com.example.product_microservice.repository.ProcessedEventRepository;
import com.example.product_microservice.rest.CreateProductRestModel;
//import com.example.product_microservice.rest.ProductCreatedEvent;
//import org.springframework.kafka.support.KafkaHeaders;
import com.example.core.event.ProductCreatedEvent;
import org.springframework.retry.annotation.Backoff;

@Service
public class ProductServiceImpl implements ProductService {
	
	private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private Set<String> processedEvents = new HashSet<>();
	
	private final ProcessedEventRepository processedEventRepository;
	

	@Value("${server.port}")
	private String port;
	
	
	public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate,ProcessedEventRepository processedEventRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.processedEventRepository = processedEventRepository;
	}
	
	@Override
	public String createProduct(CreateProductRestModel productRestModel) throws Exception {
		
//		String productId = UUID.randomUUID().toString();
		String productId = "fixed-product-123";
		// RECIEVE THE DUPLICATE EVENT AND START THE IGNORING SO 
		// WE CAN VERIFY THE IDEMOPENET CONSUMER 
		System.out.println(TimeZone.getDefault());	
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
	@RetryableTopic(
	        attempts = "3",
	        backoff = @Backoff(delay = 2000),
	        dltTopicSuffix = "-dlt",
	        include = RetryableException.class
	)
	@KafkaListener(
		    topics = "product-created-events-topic-v4",
		    containerFactory = "kafkaListenerContainerFactory"
		)
		public void listen(ProductCreatedEvent event 
//				,Acknowledgment acknowledgment
				,@Header(KafkaHeaders.RECEIVED_PARTITION) int partition
				) {

		System.err.println("=================================");
		System.err.println("INSTANCE PORT : " + port);
		System.err.println("RECEIVED EVENT : " + event.getTitle());
		System.err.println("PARTITION : " + partition);
		System.err.println("THREAD : " + Thread.currentThread().getName());
		System.err.println("=================================");
//		    
//		if(processedEvents.contains(event.getProductId()))
//		{
//			System.err.println("DUPLICATE EVENT RECEIVED, IGNORING");
////			processedEvents.add(event.getProductId());
//			return ;
//		}
		
		
		if(processedEventRepository.existsById(event.getProductId()))
		{
			System.err.println("DUPLICATE EVENT RECEIVED");
			return ;
		}
//		    acknowledgment.acknowledge();
//		    System.err.println("OFFSET COMMITTED");
		    
//		    throw new RuntimeException("Testing DLT");
		    
//		    if(event.getPrice().intValue() > 100000)
//		    {
//		    	throw new RetryableException("Temporary issue");
//		    }
//		    
		    if(event.getTitle().contains("INVALID"))
		    {
		    	throw new NonRetryableException("Bad product");
		    }
		    
//		    processedEvents.add(event.getProductId());
		    processedEventRepository.save(
		    		new ProcessedEvent(event.getProductId()));
		    
		    System.err.println("EVENT PROCESSED SUCCESSFULLY");
		}
}