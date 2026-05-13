package com.appsdeveloperblog.core.dto.commands;

import java.util.UUID;

public class ReservedProductCommand {

    private UUID productId;
    private Integer productQuantity;
    private UUID orderId;

    public ReservedProductCommand() {
    }

    public ReservedProductCommand(UUID productId, UUID orderId, Integer productQuantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.productQuantity = productQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

}
