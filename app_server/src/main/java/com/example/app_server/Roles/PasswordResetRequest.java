package com.example.app_server.Roles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String message;

    private LocalDateTime requestedAt = LocalDateTime.now();

    private boolean resolved = false;

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    private RoleUser requestedBy;


}
