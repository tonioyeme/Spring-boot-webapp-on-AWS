package com.example.webapp;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${aws.s3.bucket-name}")
    private String s3BucketName;
    private final UserService userService;

    private AmazonS3 s3Client;

    public ProductServiceImpl(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @Override
    public Product createProduct(String email, Product product) {
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be less than 0");
        }
        return productRepository.save(product);
    }


    @Override
    public Product updateProduct(Long userId, Long productId, Product updatedProduct) {
        Product existingProduct = getProductByUserAndProductId(userId, productId);

        if (existingProduct.getUser().getId() != userId) {
            throw new UnauthorizedException("Only the user who added the product can update the product.");
        }

        if (updatedProduct.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be less than 0");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setSku(updatedProduct.getSku());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setDateLastUpdated(LocalDateTime.now());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long userId, Long productId) {
        Product existingProduct = getProductByUserAndProductId(userId, productId);

        if (existingProduct.getUser().getId() != userId) {
            throw new UnauthorizedException("Only the user who added the product can delete the product.");
        }

        productRepository.delete(existingProduct);
    }

    @Override
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return null;
        }
        return productOptional.get();
    }

    @Override
    public Product getProductBySku(int sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public void deleteProduct(Long productId) {

    }

    @Override
    public Product getProductByName(String productName) {
        Optional<Product> productOptional = productRepository.findByName(productName);
        if (!productOptional.isPresent()) {
            return null;
        }
        return productOptional.get();
    }


    private Product getProductByUserAndProductId(Long userId, Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new ResourceNotFoundException("Product with ID " + productId + " not found");
        }

        Product product = productOptional.get();
        if (product.getUser().getId() != userId) {
            throw new UnauthorizedException("Only the user who added the product can access the product.");
        }

        return product;
    }

    public Product getProductByImageUUID(UUID imageUUID) {
        return productRepository.findByImageUUID(imageUUID);
    }

    public void deleteProductImage(UUID imageUUID) {
        Product product = getProductByImageUUID(imageUUID);
        if (product != null) {
            User user = product.getUser();
            String objectKey = product.getImageObjectKey();
            s3Client.deleteObject(s3BucketName, objectKey);
            product.setImageFileName(null);
            product.setImageObjectKey(null);
            product.setImageUUID(null);
            productRepository.save(product);
        }
    }

}
