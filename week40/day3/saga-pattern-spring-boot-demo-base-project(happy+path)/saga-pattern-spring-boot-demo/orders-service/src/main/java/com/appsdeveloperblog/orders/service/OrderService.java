package com.appsdeveloperblog.orders.service;
//import static com.appsdeveloperblog.core.*;

import com.appsdeveloperblog.core.dto.Order;

import java.util.UUID;


public interface OrderService {
    Order placeOrder(Order order);
    void approveOrder(UUID orderId);
}
