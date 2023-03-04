package com.example.webapp;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.s3.model.PutObjectRequest;



@RestController
public class ProductController {

    @Value("${aws.s3.bucket-name}")
    private String s3BucketName;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    private AmazonS3 s3Client;

    @Autowired
    ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    @PostMapping(path = "/product", produces = "application/json")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> productMap, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        // Extract product information from request body
        String productName = (String) productMap.get("Name");
        int productQuantity = Integer.parseInt(productMap.get("Quantity").toString());
        int productPrice = Integer.parseInt(productMap.get("Price").toString());
        int sku = Integer.parseInt(productMap.get("SKU").toString());
        String description = (String) productMap.get("Description");
        String manufacturer = (String) productMap.get("Manufacturer");


        // Check if product price is valid
        if (productPrice < 0) {
            return ResponseEntity.badRequest().body(null);
        }

        // Check if product quantity is valid
        if (productQuantity < 0) {
            return ResponseEntity.badRequest().body(null);
        }

        // Check if product SKU is unique
        Product existingProduct = productService.getProductBySku(sku);
        if (existingProduct != null) {
            return ResponseEntity.badRequest().body("Product with SKU " + sku + " already exists.");
        }

        // Create product and set user
        Product product = new Product();
        product.setName(productName);
        product.setQuantity(productQuantity);
        product.setPrice(productPrice);
        product.setUser(user);
        product.setSku(sku);
        product.setDescription(description);
        product.setManufacturer(manufacturer);

        product.setDateAdded(LocalDateTime.now()); // Set the dateAdded property
        Product createdProduct = productService.createProduct(user.getUsername(), product);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

    @PostMapping(path = "/product/image", produces = "application/json")
    public ResponseEntity<?> uploadProductImage(@RequestParam("imageFile") MultipartFile imageFile, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        String imageFileName = imageFile.getOriginalFilename();
        String imageObjectKey = "products/" + user.getId() + "/" + UUID.randomUUID().toString() + "/" + imageFileName;

        // Upload image to S3 bucket
        try {
            byte[] bytes = imageFile.getBytes();
            ObjectMetadata imageMetadata = new ObjectMetadata();
            imageMetadata.setContentType(imageFile.getContentType());
            imageMetadata.setContentLength(bytes.length);
            imageMetadata.addUserMetadata("x-amz-meta-userid", user.getId().toString());

            PutObjectRequest imagePutRequest = new PutObjectRequest(s3BucketName, imageObjectKey, new ByteArrayInputStream(bytes), imageMetadata);
            s3Client.putObject(imagePutRequest);

            return ResponseEntity.ok("Image uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload image to S3 bucket.");
        }
    }

    @DeleteMapping("/product/image/{uuid}")
    public ResponseEntity<?> deleteProductImage(@PathVariable UUID uuid, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        // Get the product associated with the image
        Product product = productService.getProductByImageUUID(uuid);

        // Ensure that the user owns the product
        if (product == null || !product.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Delete the image from the S3 bucket
        String imageObjectKey = "products/" + user.getId() + "/" + uuid.toString();
        try {
            s3Client.deleteObject(s3BucketName, imageObjectKey);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to delete image from S3 bucket.");
        }

        // Delete the image metadata from the database
        productService.deleteProductImage(uuid);

        return ResponseEntity.ok("Image deleted successfully.");
    }







    @PutMapping(path = "/product/{productName}", produces = "application/json")
    public ResponseEntity<Product> updateProduct(@PathVariable String productName, @RequestBody Map<String, Object> productMap,
                                                 Principal principal) {

        User user = userService.getUserByEmail(principal.getName());

        Product product = productService.getProductByName(productName);

        // Check if user who created the product is making the request
        if (!product.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        int productSKU = Integer.parseInt(productMap.get("SKU").toString());

        Product existingProduct = productService.getProductBySku(productSKU);
        if (existingProduct != null
                && existingProduct.getSku() != (product.getSku())
                && existingProduct.getName() != (product.getName())) {
            // If the SKU already exists for another product, return error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        product.setSku(productSKU);


        // Extract product information from request body
        String productNameRetrieved = (String) productMap.get("Product Name");
        int productQuantity = Integer.parseInt(productMap.get("Quantity").toString());
        int productPrice = Integer.parseInt(productMap.get("Price").toString());

        // Check if product quantity is valid
        if (productQuantity < 0) {
            return ResponseEntity.badRequest().body(null);
        }

        // Update product and save
        product.setProductName(productName);
        product.setPrice(productPrice);
        product.setProductQuantity(productQuantity);
        Product updatedProduct = productRepository.save(product);

        return ResponseEntity.ok(updatedProduct);
    }


    @DeleteMapping(path = "/product/{productName}", produces = "application/json")
    public ResponseEntity<?> deleteProduct(@PathVariable String productName, Principal principal) {
        String userEmail = principal.getName();
        User user = userService.getUserByEmail(userEmail);

        // Get product by name
        Product product = productService.getProductByName(productName);

        // Check if user who created the product is making the request
        if (!product.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Long productId = product.getId();

        // Delete product and return success response
        productRepository.deleteById(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/product/{productName}", produces = "application/json")
    public ResponseEntity<Product> getProductByName(@PathVariable String productName, Principal principal) {
        Product product = productService.getProductByName(productName);

        String userEmail = principal.getName();
        User user = userService.getUserByEmail(userEmail);

        // Check if user who created the product is making the request
        if (!product.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }



}
