package com.example.app_server.Roles;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionDTO {
    private String dnlId;
    private String productName;
    private UserSummaryDTO user;

    // Constructor
    public SubscriptionDTO(String dnlId, String productName, UserSummaryDTO user) {
        this.dnlId = dnlId;
        this.productName = productName;
        this.user = user;
    }

    public String getDnlId() {
        return dnlId;
    }

    public void setDnlId(String dnlId) {
        this.dnlId = dnlId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }
}

