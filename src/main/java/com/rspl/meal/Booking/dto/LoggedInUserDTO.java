package com.rspl.meal.Booking.dto;

public class LoggedInUserDTO {
    private Long id;
    private String email;
    private String name; // Assuming you have a name field in your Employee entity

    // Constructors
    public LoggedInUserDTO() {}

    public LoggedInUserDTO(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}