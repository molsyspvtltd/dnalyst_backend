package com.example.app_server.DailyActivities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class DailyActivityController {

    @Autowired
    private DailyActivityService dailyActivityService;

    /**
     * Get or create today's activity data for a user
     */
    @GetMapping("/today")
    public DailyActivity getToday(@RequestParam String mrnId) {
        return dailyActivityService.getOrCreateTodayActivity(mrnId);
    }

    /**
     * Increment water intake (1 glass = 200ml)
     */
    @PostMapping("/increment-water")
    public DailyActivity incrementWater(@RequestParam String mrnId, @RequestParam int glassCount) {
        return dailyActivityService.updateWaterIntake(mrnId, glassCount);
    }

    /**
     * Increment calorie intake
     */
    @PostMapping("/add-calories")
    public DailyActivity addCalories(@RequestParam String mrnId, @RequestParam int calories) {
        return dailyActivityService.updateCalorieIntake(mrnId, calories);
    }

    /**
     * Set sleep duration (in minutes)
     */
    @PostMapping("/set-sleep")
    public DailyActivity setSleep(@RequestParam String mrnId, @RequestParam int minutes) {
        return dailyActivityService.updateSleep(mrnId, minutes);
    }
}
