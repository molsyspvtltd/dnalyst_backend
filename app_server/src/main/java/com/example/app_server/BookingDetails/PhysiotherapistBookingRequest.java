package com.example.app_server.BookingDetails;

import java.time.LocalDateTime;

public class PhysiotherapistBookingRequest {
    private String dnlId;
    private BookingType bookingType; // ONLINE or OFFLINE
    private LocalDateTime bookingTime;
    private String location; // For offline bookings
    private String googleMeetLink; // For online bookings
    private String assignedToId;  // ID of the doctor/dietician/etc. to assign


    // Getters and Setters


    public String getDnlId() {
        return dnlId;
    }

    public void setDnlId(String dnlId) {
        this.dnlId = dnlId;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGoogleMeetLink() {
        return googleMeetLink;
    }

    public void setGoogleMeetLink(String googleMeetLink) {
        this.googleMeetLink = googleMeetLink;
    }


    public String getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }
}

