package com.example.app_server.Steganography;

import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class StegoImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalImageName;
    private String encryptedImagePath;
    private String uploadedDataName;
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dnl_id", referencedColumnName = "dnlId")
    private Subscription subscription;

    public StegoImage() {
    }

    // Constructors, Getters, Setters
    public StegoImage(String originalImageName, String uploadedDataName, String encryptedImagePath, Subscription subscription) {
        this.originalImageName = originalImageName;
        this.uploadedDataName = uploadedDataName;
        this.encryptedImagePath = encryptedImagePath;
        this.subscription = subscription;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters omitted for brevity

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalImageName() {
        return originalImageName;
    }

    public void setOriginalImageName(String originalImageName) {
        this.originalImageName = originalImageName;
    }

    public String getEncryptedImagePath() {
        return encryptedImagePath;
    }

    public void setEncryptedImagePath(String encryptedImagePath) {
        this.encryptedImagePath = encryptedImagePath;
    }

    public String getUploadedDataName() {
        return uploadedDataName;
    }

    public void setUploadedDataName(String uploadedDataName) {
        this.uploadedDataName = uploadedDataName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}

