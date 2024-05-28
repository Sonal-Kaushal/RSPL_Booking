package com.rspl.meal.Booking.Entites;

import com.rspl.meal.Booking.enums.BookingStatus;
import com.rspl.meal.Booking.enums.MealType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    private String userId;
    private MealType mealType;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Coupon coupon;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Booking(){}
    public Booking(Long bookingId, Coupon coupon, LocalDate endDate, MealType mealType, LocalDate startDate, BookingStatus status, String userId) {
        this.bookingId = bookingId;
        this.coupon = coupon;
        this.endDate = endDate;
        this.mealType = mealType;
        this.startDate = startDate;
        this.status = status;
        this.userId = userId;
    }
}
