package com.example.app_server.ProductDetails;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Product {

    @Id
    private String productId;

    private String productName;

    private String description;

    @Convert(converter = FeaturesConverter.class)
    private List<String> features;

    private String videoLink;
    private Double monthlyPrice;
    private Double quarterlyPrice;
    private Double oneShotPrice;
    private String image;

    public void generateProductId(ProductRepository productRepository) {
        if (this.productId == null) {
            this.productId = generateUniqueProductId(productRepository);
        }
    }

    private String generateUniqueProductId(ProductRepository productRepository) {
        String lastProductId = productRepository.findLastProductId();
        if (lastProductId == null) {
            lastProductId = "DNLPI00000";
        }
        int nextId = Integer.parseInt(lastProductId.substring(5)) + 1;
        return String.format("DNLPI%05d", nextId);
    }
}
