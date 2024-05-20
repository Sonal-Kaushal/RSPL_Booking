// BookingController.java
package com.rspl.meal.Booking.Controller;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.Entites.MealType;
import com.rspl.meal.Booking.Services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/quick")
    public ResponseEntity<String> quickBookMeal(@RequestParam String userId,
                                                @RequestParam MealType mealType
                                               ) {
        System.out.println(userId);
        System.out.print(mealType);
        return bookingService.quickBookMeal(userId, mealType);
    }

//    @PostMapping("/single")
//    public ResponseEntity<String> singleBookMeal(@RequestParam LocalDate date,
//                                                 @RequestParam String userId,
//                                                 @RequestParam MealType mealType) {
//        return bookingService.singleBookMeal(date, userId, mealType);
//    }

//    @PostMapping("/bulk")
//    public ResponseEntity<String> bulkBookMeals(@RequestParam MealType mealType,
//                                                @RequestParam LocalDate startDate,
//                                                @RequestParam LocalDate endDate,
//                                                @RequestParam String userId) {
//        return bookingService.bulkBookMeals(mealType, startDate,  endDate,userId);
//    }

    @PostMapping("/book-meal")
    public ResponseEntity<String> bookMeal(@RequestBody Map<String, Object> bookingDetails) {
        return bookingService.bookMeal(bookingDetails);
    }

    @DeleteMapping("/cancel")
//    public ResponseEntity<String> cancelBookings(@RequestBody List<Long> bookingIds) {
//        return bookingService.cancelBookings(bookingIds);
//    }
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/bookingsByDateRange")
    public List<Booking> getBookingsByDateRange(@RequestParam("startDate") String startDateStr,
                                                @RequestParam("endDate") String endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        return bookingService.getBookingsByDateRange(startDate, endDate);
    }
}
