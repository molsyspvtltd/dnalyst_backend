package com.example.app_server.Roles;

public class UserSummaryDTO {
    private String mrnId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String occupation;
    private String height;
    private String weight;

    // Constructor
    public UserSummaryDTO(String mrnId, String firstName, String lastName, String email, String phoneNumber, String occupation,
    String height, String weight) {
        this.mrnId = mrnId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.occupation = occupation;
        this.height = height;
        this.weight = weight;

    }

    public String getMrnId() {
        return mrnId;
    }

    public void setMrnId(String mrnId) {
        this.mrnId = mrnId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
