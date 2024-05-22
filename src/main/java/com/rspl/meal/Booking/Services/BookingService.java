package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.Entites.MealType;
import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.Repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private static final LocalTime CANCEL_CUTOFF_TIME = LocalTime.of(22, 0);
    private static final LocalTime CUTOFF_TIME = LocalTime.of(20, 0); // 8 PM cutoff
    private static final int BOOKING_PERIOD_MONTHS = 3;

    @Autowired
    private BookingRepository bookingRepository;

    public ResponseEntity<String> quickBookMeal(String userId, MealType mealType) {
        if (!isBookingBeforeCutoffTime()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick Booking should be made before 8 PM");
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (isWeekend(tomorrow)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick booking cannot be done for weekends.");
        }

        return processSingleBooking(userId, mealType, tomorrow);
    }

    public ResponseEntity<String> bookMeal(Map<String, Object> bookingDetails) {
        try {
            String bookingType = (String) bookingDetails.get("bookingType");
            String userId = extractUserId(bookingDetails.get("userId"));
            String endDateStr = (String) bookingDetails.get("endDate");
            String mealCategory = (String) bookingDetails.get("mealType");
            String startDateStr = (String) bookingDetails.get("startDate");

            if (bookingType == null || userId == null || mealCategory == null || startDateStr == null) {
                return ResponseEntity.badRequest().body("Booking details are incomplete");
            }

            MealType mealType = MealType.valueOf(mealCategory.toUpperCase());
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = bookingType.equalsIgnoreCase("bulk") ? LocalDate.parse(endDateStr) : startDate;

            if (!isBookingBeforeCutoffTime()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking cannot be made after 8 PM.");
            }

            if (isDateInvalid(startDate) || (bookingType.equalsIgnoreCase("bulk") && isDateInvalid(endDate))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid booking date.");
            }

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate maxBookingDate = tomorrow.plusMonths(BOOKING_PERIOD_MONTHS);

            if (!isDateWithinRange(startDate, tomorrow, maxBookingDate) ||
                    (bookingType.equalsIgnoreCase("bulk") && !isDateWithinRange(endDate, tomorrow, maxBookingDate))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking should be within three months from tomorrow.");
            }

            if (bookingType.equalsIgnoreCase("single")) {
                return processSingleBooking(userId, mealType, startDate);
            } else if (bookingType.equalsIgnoreCase("bulk")) {
                return processBulkBooking(userId, endDate, mealType, startDate);
            } else {
                return ResponseEntity.badRequest().body("Invalid booking type: " + bookingType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    private boolean isBookingBeforeCutoffTime() {
        return LocalTime.now().isBefore(CUTOFF_TIME);
    }

    private boolean isDateInvalid(LocalDate date) {
        return date.isBefore(LocalDate.now().plusDays(1)) || isWeekend(date);
    }

    private boolean isDateWithinRange(LocalDate date, LocalDate minDate, LocalDate maxDate) {
        return !date.isBefore(minDate) && !date.isAfter(maxDate);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private String extractUserId(Object userIdObj) {
        if (userIdObj == null) {
            return null;
        }
        return userIdObj instanceof String ? (String) userIdObj : String.valueOf(userIdObj);
    }

    private ResponseEntity<String> processSingleBooking(String userId, MealType mealType, LocalDate date) {
        if (isDateInvalid(date)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date ");
        }
        if (hasBookingForDate(date, userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has a booking for this date.");
        }
        Booking booking = createBooking(userId, mealType, date);
        bookingRepository.save(booking);
        return ResponseEntity.ok("Meal booked for " + date + ".");
    }
    private ResponseEntity<String> processBulkBooking(String userId, LocalDate endDate, MealType mealType, LocalDate startDate) {
        // Check if the start date is before the end date
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be after end date.");
        }

        LocalDate date = startDate;
        List<Booking> bookings = new ArrayList<>();
        int validDateCount = 0; // Counter for valid dates (excluding weekends)

        while (!date.isAfter(endDate)) {
            if (isWeekend(date)) {
                date = date.plusDays(1); // Skip weekends
                continue;
            }

            // Check if the date is invalid (before the current date)
            if (isDateInvalid(date)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid booking date: " + date);
            }

            // Check if the user already has a booking for the date
            if (hasBookingForDate(date, userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has a booking for date: " + date);
            }

            validDateCount++; // Increment the valid date count
            Booking booking = createBooking(userId, mealType, date);
            bookings.add(booking);
            date = date.plusDays(1);
        }

        // Adjust the end date to account for skipped weekends
        LocalDate adjustedEndDate = startDate.plusDays(validDateCount+1);

        // Save the bookings to the repository
        bookingRepository.saveAll(bookings);

        return ResponseEntity.ok("Meals booked from " + startDate + " to " + adjustedEndDate + ".");
    }



    private Booking createBooking(String userId, MealType mealType, LocalDate date) {
        Booking booking = new Booking();
        booking.setStartDate(date);
        booking.setEndDate(date);
        booking.setUserId(userId);
        booking.setMealType(mealType);
        return booking;
    }


   public ResponseEntity<String> cancelBookings(List<Long> bookingIds) {
        if (LocalDateTime.now().isAfter(LocalDateTime.of(LocalDate.now(), CANCEL_CUTOFF_TIME))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bookings cannot be cancelled after 10 PM.");
        }

        for (Long bookingId : bookingIds) {
            ResponseEntity<String> response = cancelBooking(bookingId);
            if (response.getStatusCode() != HttpStatus.OK) {
                return response;
            }
        }
        return ResponseEntity.ok("Bookings cancelled successfully.");
    }

    private ResponseEntity<String> cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            bookingRepository.delete(optionalBooking.get());
            return ResponseEntity.ok("Booking cancelled successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
        }
    }

    private boolean hasBookingForDate(LocalDate date, String userId) {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().anyMatch(booking -> booking.getStartDate().equals(date) && booking.getUserId().equals(userId));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Method to fetch bookings by user ID
//    public List<Booking> getBookingsByUserId(String userId) {
//        return bookingRepository.findByUserId(userId);
//    }
//
//    // Method to fetch bookings by meal type
//    public List<Booking> getBookingsByMealType(MealType mealType) {
//        return bookingRepository.findByMealType(mealType);
//    }

    // Method to fetch bookings by date range
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByStartDateBetween(startDate, endDate);
    }
}
