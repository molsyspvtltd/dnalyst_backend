package com.example.app_server.dietphysio.repository;

import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;
import com.example.app_server.dietphysio.model.DietChart;

import com.example.app_server.dietphysio.model.ExerciseChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DietChartRepository extends JpaRepository<DietChart, Long> {
    Optional<DietChart> findByChartNumber(int chartNumber);
    boolean existsBySubscription(Subscription subscription);
    boolean existsBySubscriptionAndChartNumber(Subscription subscription, int chartNumber);
    Optional<DietChart> findBySubscriptionAndChartNumber(Subscription subscription, int chartNumber);
    @Query("SELECT MAX(dc.chartNumber) FROM DietChart dc WHERE dc.subscription = :subscription")
    Optional<Integer> findMaxChartNumberBySubscription(@Param("subscription") Subscription subscription);

}
