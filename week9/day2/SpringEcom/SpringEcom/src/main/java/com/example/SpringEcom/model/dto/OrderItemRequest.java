package com.example.SpringEcom.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {
}
