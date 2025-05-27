package com.example.app_server.DailyActivities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {
    Optional<DailyActivity> findByUserMrnIdAndActivityDate(String mrnId, LocalDate activityDate);
}