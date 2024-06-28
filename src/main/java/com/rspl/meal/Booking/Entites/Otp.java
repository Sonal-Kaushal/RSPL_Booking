package com.rspl.meal.Booking.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otp;

    @OneToOne(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Employee user;

    private Date expiryDate;

    public Otp() {

    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Employee getUser() {
        return user;
    }

    public void setUser(Employee user) {
        this.user = user;
    }

    public Otp(Date expiryDate, Long id, String otp, Employee user) {
        this.expiryDate = expiryDate;
        this.id = id;
        this.otp = otp;
        this.user = user;
    }
}