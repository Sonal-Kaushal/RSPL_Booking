package com.rspl.meal.Booking.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "start_date")
    private LocalDate  startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "user_id")
    private String UserId;

//    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;

    public Booking() {}

    public Booking(LocalDate endDate, Long id, MealType mealType, LocalDate startDate, String userId) {
        this.endDate = endDate;
        this.id = id;
        this.mealType = mealType;
        this.startDate = startDate;
        UserId = userId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


}
