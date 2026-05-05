package com.example.product_microservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_microservice.rest.CreateProductRestModel;
import com.example.product_microservice.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	ProductService productService;
	
	public ProductController(ProductService productService)
	{
		this.productService = productService;
	}
	
	@PostMapping
	public ResponseEntity<String> createProduct(@RequestBody CreateProductRestModel product){
	String productId = productService.createProduct(product);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(productId);
	}
}
