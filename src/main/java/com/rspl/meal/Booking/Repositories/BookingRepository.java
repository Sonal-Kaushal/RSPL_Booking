package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Corrected query method
    List<Booking> findByEmployeeAndStartDate(Employee employee, LocalDate startDate);

    List<Booking> findByEmployee(Employee employee);

    List<Booking> findByEmployeeAndStartDateBetween(Employee employee, LocalDate today, LocalDate threeMonthsLater);

    List<Booking> findByEmployeeAndMealType(Employee employee, MealType mealType);
}
