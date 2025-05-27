package com.example.app_server.DailyActivities;

import com.example.app_server.UserAccountCreation.User;
import com.example.app_server.UserAccountCreation.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DailyActivityService {

    @Autowired
    private DailyActivityRepository dailyActivityRepository;

    @Autowired
    private UserRepository userRepository;

    public DailyActivity getOrCreateTodayActivity(String mrnId) {
        LocalDate today = LocalDate.now();
        return dailyActivityRepository.findByUserMrnIdAndActivityDate(mrnId, today)
                .orElseGet(() -> {
                    User user = userRepository.findById(mrnId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    DailyActivity activity = new DailyActivity();
                    activity.setUser(user);
                    activity.setActivityDate(today);
                    activity.setWaterGoalMl(1600); // Default goal = 8 glasses (200ml x 8)
                    activity.setWaterIntakeMl(0);
                    activity.setCalorieIntake(0);
                    activity.setSleepDurationMinutes(0);
                    return dailyActivityRepository.save(activity);
                });
    }

    // Increment water intake by number of glasses
    public DailyActivity updateWaterIntake(String mrnId, int glassCountToAdd) {
        DailyActivity activity = getOrCreateTodayActivity(mrnId);
        int currentIntake = activity.getWaterIntakeMl() == null ? 0 : activity.getWaterIntakeMl();
        activity.setWaterIntakeMl(currentIntake + (glassCountToAdd * 200)); // 1 glass = 200ml
        return dailyActivityRepository.save(activity);
    }

    // Increment calorie intake
    public DailyActivity updateCalorieIntake(String mrnId, int caloriesToAdd) {
        DailyActivity activity = getOrCreateTodayActivity(mrnId);
        int currentCalories = activity.getCalorieIntake() == null ? 0 : activity.getCalorieIntake();
        activity.setCalorieIntake(currentCalories + caloriesToAdd);
        return dailyActivityRepository.save(activity);
    }

    // Replace sleep duration (typically set once per day)
    public DailyActivity updateSleep(String mrnId, int minutes) {
        DailyActivity activity = getOrCreateTodayActivity(mrnId);
        activity.setSleepDurationMinutes(minutes);
        return dailyActivityRepository.save(activity);
    }
}
