package com.example.product_microservice.service;

import com.example.product_microservice.rest.CreateProductRestModel;

public interface ProductService {

	String createProduct(CreateProductRestModel productRestModel) throws Exception;
}
