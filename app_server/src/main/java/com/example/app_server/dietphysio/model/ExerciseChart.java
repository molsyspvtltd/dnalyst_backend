package com.example.app_server.dietphysio.model;


import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class ExerciseChart {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Subscription subscription;

    @Column(unique = true)
    private int chartNumber;

    @OneToMany(mappedBy = "exerciseChart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ExerciseDetail> exerciseDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public List<ExerciseDetail> getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(List<ExerciseDetail> exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

}
