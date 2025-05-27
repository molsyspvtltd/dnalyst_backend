package com.example.app_server.DailyActivities;

import com.example.app_server.UserAccountCreation.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class DailyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate activityDate;

    @Column(nullable = false)
    private Integer waterIntakeMl = 0;  // nullable in case of no activity recorded, default to 0

    @Column(nullable = false)
    private Integer waterGoalMl = 1600; // Default goal = 1600ml (8 glasses x 200ml)

    @Column(nullable = false)
    private Integer calorieIntake = 0;  // nullable in case of no activity recorded, default to 0

    @Column(nullable = false)
    private Integer sleepDurationMinutes = 0; // nullable in case of no activity recorded, default to 0


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mrn_id")
    @JsonBackReference
    private User user;
}
