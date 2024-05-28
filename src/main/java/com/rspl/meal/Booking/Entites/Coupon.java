package com.rspl.meal.Booking.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponId;
    private LocalDateTime expirationTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="booking_id", referencedColumnName = "booking_id")
    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coupon(){}
    public Coupon(Booking booking, String couponId, LocalDateTime expirationTime, Long id) {
        this.booking = booking;
        this.couponId = couponId;
        this.expirationTime = expirationTime;
        this.id = id;
    }
}
