package com.example.SpringCache.service;

import com.example.SpringCache.entities.Product;
import com.example.SpringCache.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    @Cacheable(value = "productCache", key = "#id")
    public Product getProductById(Long id)
    {
        System.out.println("Fetching from the DB for ID: "+id);
        return productRepository.findById(id).orElse(null);
    }


    @Transactional
    @CachePut(value = "productCache", key = "#product.id")
    public Product addProduct(Product product)
    {
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "productCache", key = "#id")
    public void deleteProduct(Long id)
    {
        productRepository.deleteById(id);
    }


    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}
