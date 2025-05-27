package com.example.app_server.dietphysio.dto;

import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;

public class CreateChartRequest {
    private Subscription subscription;
    private int chartNumber;
    private String startDate;
    private String endDate;

    // Getters and setters

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public int getChartNumber() {
        return chartNumber;
    }

    public void setChartNumber(int chartNumber) {
        this.chartNumber = chartNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
