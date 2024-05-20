package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.Entites.MealType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository

public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
//  List<Booking> findByUserId(String UserId);
//    List<Booking> findByMealType(MealType mealType);




}

