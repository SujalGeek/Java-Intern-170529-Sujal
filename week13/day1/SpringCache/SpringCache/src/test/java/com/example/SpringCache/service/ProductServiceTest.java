package com.example.SpringCache.service;

import com.example.SpringCache.entities.Product;
import com.example.SpringCache.repository.ProductRepository;
import com.example.SpringCache.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setName("Laptop");
        product.setPrice(70000);
        product.setCategory("Electronics");
        productRepository.save(product);
    }

    @Test
    void testCacheableIntegration() {
        System.out.println("---- TEST: @Cacheable ----");

        // First call → DB hit
        Product firstCall = productService.getProductById(product.getId());
        assertThat(firstCall).isNotNull();

        // Second call → cache hit
        Product secondCall = productService.getProductById(product.getId());
        assertThat(secondCall).isEqualTo(firstCall);

        assertThat(cacheManager.getCache("productCache").get(product.getId())).isNotNull();

        System.out.println("Cache works: 2nd call fetched from cache!");
    }

    @Test
    void testCachePutIntegration() {
        System.out.println("---- TEST: @CachePut ----");

        Product newProduct = new Product("Phone", 45000, "Electronics");
        Product savedProduct = productService.addProduct(newProduct);

        assertThat(savedProduct).isNotNull();
        assertThat(cacheManager.getCache("productCache").get(savedProduct.getId())).isNotNull();

        System.out.println("Cache updated after saving new product!");
    }

    @Test
    void testCacheEvictIntegration() {
        System.out.println("---- TEST: @CacheEvict ----");

        cacheManager.getCache("productCache").put(product.getId(), product);
        assertThat(cacheManager.getCache("productCache").get(product.getId())).isNotNull();

        productService.deleteProduct(product.getId());
        assertThat(cacheManager.getCache("productCache").get(product.getId())).isNull();

        System.out.println(" Cache evicted after deletion!");
    }
}
