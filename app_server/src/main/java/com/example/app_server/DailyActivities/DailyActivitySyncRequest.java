package com.example.app_server.DailyActivities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DailyActivitySyncRequest {
    private String mrnId;
    private LocalDate activityDate;

    private int stepCount;
    private int calorieIntake;
    private int waterIntakeMl;
    private int sleepDurationMinutes;
}