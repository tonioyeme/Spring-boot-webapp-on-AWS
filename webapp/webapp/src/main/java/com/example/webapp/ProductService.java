package com.example.webapp;

import java.util.UUID;

public interface ProductService {


    Product createProduct(String email, Product product);

    Product updateProduct(Long userId, Long productId, Product updatedProduct);
    void deleteProduct(Long userId, Long productId);

    Product updateProduct(Product product);

    Product getProductById(Long productId);

    void deleteProduct(Long productId);

    Product getProductByName(String productName);

    Product getProductBySku(int sku);

    Product getProductByImageUUID(UUID uuid);

    void deleteProductImage(UUID uuid);
}

