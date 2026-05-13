package com.appsdeveloperblog.core.dto.events;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductReservedEvent {

    private UUID orderId;
    private UUID productId;
    private BigDecimal productQuantity;
    private Integer productPrice;

    public ProductReservedEvent() {
    }

    public ProductReservedEvent(UUID orderId, UUID productId, BigDecimal productQuantity, Integer productPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public BigDecimal getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(BigDecimal productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }
}

