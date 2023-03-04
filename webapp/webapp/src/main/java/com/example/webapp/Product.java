package com.example.webapp;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Entity
@Table(name = "products")
public class Product {

        @jakarta.persistence.Id
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;
        private String description;

        @Column(nullable = false, unique = true)
        private int sku;

        private String manufacturer;

        @Column(nullable = false)
        private int price;

        @Column(nullable = false)
        private Integer quantity;

        @Column(name = "date_added", nullable = false)
        private LocalDateTime dateAdded;

        @Column(name = "date_last_updated")
        private LocalDateTime dateLastUpdated;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        private String fileName;

        private String objectKey;

        private String imageFileName;

        private String imageObjectKey;

        // Add the following field for storing image UUID
        @Column(name = "image_uuid")
        private String imageUUID;



        public Product() {
                this.name = name;
                this.description = description;
                this.sku = sku;
                this.manufacturer = manufacturer;
                this.price = price;
                this.quantity = quantity;
                this.dateAdded = dateAdded;
                this.dateLastUpdated = dateLastUpdated;
                this.user = user;
                this.fileName = fileName;
                this.objectKey = objectKey;
                this.imageFileName = imageFileName;
                this.imageObjectKey = imageObjectKey;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getId() {
                return id;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getName() {
                return name;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public String getDescription() {
                return description;
        }

        public void setSku(int sku) {
                this.sku = sku;
        }

        public int getSku() {
                return sku;
        }

        public void setManufacturer(String manufacturer) {
                this.manufacturer = manufacturer;
        }

        public String getManufacturer() {
                return manufacturer;
        }

        public void setPrice(int price) {
                this.price = price;
        }

        public int getPrice() {
                return price;
        }

        public void setQuantity(Integer quantity) {
                this.quantity = quantity;
        }

        public Integer getQuantity() {
                return quantity;
        }

        public void setDateAdded(LocalDateTime dateAdded) {
                this.dateAdded = dateAdded;
        }

        public LocalDateTime getDateAdded() {
                return dateAdded;
        }

        public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
                this.dateLastUpdated = dateLastUpdated;
        }

        public LocalDateTime getDateLastUpdated() {
                return dateLastUpdated;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public User getUser() {
                return user;
        }

        public void setProductName(String productName) {
                this.name = productName;
        }

        public void setProductQuantity(int productQuantity) {
                this.quantity = productQuantity;
        }

        public void setFileName(String fileName) {
                this.fileName = fileName;
        }

        public String getFileName() {
                return fileName;
        }

        public void setObjectKey(String objectKey) {
                this.objectKey = objectKey;
        }

        public String getObjectKey() {
                return objectKey;
        }

        public void setImageFileName(String imageFileName) {
                this.imageFileName = imageFileName;
        }

        public String getImageFileName() {
                return imageFileName;
        }

        public void setImageObjectKey(String imageObjectKey) {
                this.imageObjectKey = imageObjectKey;
        }

        public String getImageObjectKey() {
                return imageObjectKey;
        }

        public String getImageUUID() {
                return imageUUID;
        }

        public void setImageUUID(String imageUUID) {
                this.imageUUID = imageUUID;
        }

    }


