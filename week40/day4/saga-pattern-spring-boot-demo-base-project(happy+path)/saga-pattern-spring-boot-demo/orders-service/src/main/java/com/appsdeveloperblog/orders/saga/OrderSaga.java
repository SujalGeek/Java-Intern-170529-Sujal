package com.appsdeveloperblog.orders.saga;

import com.appsdeveloperblog.core.dto.commands.ApproveOrderCommand;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.commands.ReservedProductCommand;
import com.appsdeveloperblog.core.dto.events.*;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        groupId = "orders-saga-group",
        topics = {
                "${orders.events.topic-name}",
                "${products.events.topic-name}",
                "${payments.events.topic-name}"
        }
)
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    private final String productsCommandTopicName;
    private final String paymentsCommandsTopicName;
    private final String ordersCommandsTopicName;

    public OrderSaga(
            KafkaTemplate<String, Object> kafkaTemplate,

            @Value("${products.commands.topic-name}")
            String productsCommandTopicName,

            @Value("${payments.commands.topic-name}")
            String paymentsCommandsTopicName,

            @Value("${orders.commands.topic-name}")
            String ordersCommandsTopicName,

            OrderHistoryService orderHistoryService
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandTopicName = productsCommandTopicName;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.ordersCommandsTopicName = ordersCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleOrderCreatedEvent(
            @Payload OrderCreatedEvent event
    ) {

        ReservedProductCommand command =
                new ReservedProductCommand(
                        event.getProductId(),
                        event.getOrderId(),
                        event.getProductQuantity()
                );

        kafkaTemplate.send(
                productsCommandTopicName,
                event.getOrderId().toString(),
                command
        );

        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.CREATED
        );

        System.out.println("ORDER CREATED EVENT RECEIVED");
    }

    @KafkaHandler
    public void handleProductReservedEvent(
            @Payload ProductReservedEvent event
    ) {

        ProcessPaymentCommand command =
                new ProcessPaymentCommand(
                        event.getOrderId(),
                        event.getProductId(),
                        event.getProductQuantity(),
                        event.getProductPrice()
                );

        kafkaTemplate.send(
                paymentsCommandsTopicName,
                event.getOrderId().toString(),
                command
        );

        System.out.println("PRODUCT RESERVED EVENT RECEIVED");
    }

    @KafkaHandler
    public void handlePaymentProcessedEvent(
            @Payload PaymentProcessedEvent event
    ) {

        ApproveOrderCommand command =
                new ApproveOrderCommand(
                        event.getOrderId()
                );

        kafkaTemplate.send(
                ordersCommandsTopicName,
                event.getOrderId().toString(),
                command
        );

        System.out.println("PAYMENT PROCESSED EVENT RECEIVED");
    }

    @KafkaHandler
    public void handleOrderApprovedEvent(
            @Payload OrderApprovedEvent event
    ) {

        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.APPROVED
        );

        System.out.println("ORDER APPROVED EVENT RECEIVED");
    }

    @KafkaHandler
    public void handleProductReservationFailedEvent(
            @Payload ProductReservationFailedEvent event
    ) {

        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.REJECTED
        );

        System.out.println("PRODUCT RESERVATION FAILED");
    }

    @KafkaHandler
    public void handlePaymentFailedEvent(
            @Payload PaymentFailedEvent event
    ) {

        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.REJECTED
        );

        System.out.println("PAYMENT FAILED");
    }
}