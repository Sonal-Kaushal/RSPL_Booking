package com.rspl.meal.Booking.Controller;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.enums.MealType;
import com.rspl.meal.Booking.Services.BookingService;
import com.rspl.meal.Booking.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/quick")
    public ResponseEntity<String> quickBookMeal(@RequestParam String userId,
                                                @RequestParam MealType mealType) {
        ResponseEntity<String> response = bookingService.quickBookMeal(userId, mealType);
        return handleBookingResponse(response);
    }

    @PostMapping("/book-meal")
    public ResponseEntity<String> bookMeal(@RequestBody Map<String, Object> bookingDetails) {
        ResponseEntity<String> response = bookingService.bookMeal(bookingDetails);
        return handleBookingResponse(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBookings(@RequestBody List<Long> bookingIds) {
        ResponseEntity<String> response = bookingService.cancelBookings(bookingIds);
        return handleCancellationResponse(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable String userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    private ResponseEntity<String> handleBookingResponse(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            // Booking was successful
            String bookingMessage = response.getBody();
            notificationService.triggerBookingNotification(bookingMessage);
            return ResponseEntity.ok(bookingMessage);
        } else {
            // Booking failed
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }

    private ResponseEntity<String> handleCancellationResponse(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            // Cancellation was successful
            String cancellationMessage = response.getBody();
            notificationService.triggerCancellationNotification(cancellationMessage);
            return ResponseEntity.ok(cancellationMessage);
        } else {
            // Cancellation failed
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }



}
