package com.example.product_microservice.rest;

import java.math.BigDecimal;

public class ProductCreatedEvent {
	
	
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

	// No args Constructor for desearlization purpose
	public ProductCreatedEvent() {
		
	}
	
	
	public ProductCreatedEvent(String productId, String title, BigDecimal price, Integer quantity) {
//		super();
		this.productId = productId;
		this.title = title;
		this.price = price;
		this.quantity = quantity;
	}


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public BigDecimal getPrice() {
		return price;
	}


	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
