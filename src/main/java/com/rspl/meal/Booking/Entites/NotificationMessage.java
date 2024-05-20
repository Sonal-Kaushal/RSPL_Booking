package com.rspl.meal.Booking.Entites;

import java.time.LocalDateTime;

public class NotificationMessage {

    private Long bookingID;

    private String UserId;

    public NotificationMessage(Long bookingID, String message, LocalDateTime timestamp, String userId) {
        this.bookingID = bookingID;
        this.message = message;
        this.timestamp = timestamp;
        UserId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getBookingID() {
        return bookingID;
    }

    public void setBookingID(Long bookingID) {
        this.bookingID = bookingID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    private String message;

    private LocalDateTime timestamp;

}
