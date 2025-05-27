package com.example.app_server.Roles;

public class ForgotPasswordRequest {
    private String email;
    private String message;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
