package com.appsdeveloperblog.orders.saga;

import com.appsdeveloperblog.core.dto.commands.ApproveOrderCommand;
import com.appsdeveloperblog.core.dto.commands.ReservedProductCommand;
import com.appsdeveloperblog.core.dto.events.*;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.dto.OrderHistory;
import com.appsdeveloperblog.core.dto.events.PaymentFailedEvent;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic-name}",
        "${products.events.topic-name}",
        "${payments.events.topic-name}"

}
)
public class OrderSaga {

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final String productsCommandTopicName;
    private final OrderHistoryService orderHistoryService;
    private final String paymentsCommandsTopicName;
    private final String ordersCommandsTopicName;

    public OrderSaga(KafkaTemplate<String,Object> kafkaTemplate,
                     @Value("${products.commands.topic-name}")
                     String productsCommandTopicName,
                    OrderHistoryService orderHistoryService,
                     @Value("${payments.commands.topic-name}")
                     String paymentsCommandsTopicName,
                     @Value("${orders.commands.topic-name}")
                    String ordersCommandsTopicName
    )
    {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandTopicName = productsCommandTopicName;
        this.orderHistoryService = orderHistoryService;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.ordersCommandsTopicName = ordersCommandsTopicName;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event)
    {
        ReservedProductCommand command = new ReservedProductCommand(
                event.getProductId(),
                event.getOrderId(),
                event.getProductQuantity()
        );

    kafkaTemplate.send(productsCommandTopicName,command);
    orderHistoryService.add(event.getOrderId(), OrderStatus.CREATED);
    }


    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent productReservedEvent)
    {
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                productReservedEvent.getOrderId(),
                productReservedEvent.getProductId(),
                productReservedEvent.getProductQuantity(),
                productReservedEvent.getProductPrice()
        );

        kafkaTemplate.send(paymentsCommandsTopicName,processPaymentCommand);
    }

    @KafkaHandler
    public void handelEvent(@Payload PaymentProcessedEvent paymentProcessedEvent)
    {
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(
                paymentProcessedEvent.getOrderId()
        );
        kafkaTemplate.send(ordersCommandsTopicName,approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event)
    {
//        kafkaTemplate
        orderHistoryService.add(event.getOrderId(),OrderStatus.APPROVED);
    }


    @KafkaHandler
    public void handleEvent(@Payload ProductReservationFailedEvent event)
    {
        System.out.println("Product reservation failed for order: " + event.getOrderId());

        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.REJECTED
        );
    }
    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event)
    {
        orderHistoryService.add(
                event.getOrderId(),
                OrderStatus.REJECTED
        );

        System.out.println("Payment failed for order: " + event.getOrderId());
    }
}
