package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.enums.MealType;
import com.rspl.meal.Booking.enums.BookingStatus;
import com.rspl.meal.Booking.Repositories.BookingRepository;
import com.rspl.meal.Booking.Entites.Notification;
import com.rspl.meal.Booking.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class BookingService {

    private static final LocalTime CANCEL_CUTOFF_TIME = LocalTime.of(22, 0);
    private static final LocalTime CUTOFF_TIME = LocalTime.of(20, 0); // 8 PM cutoff
    private static final int BOOKING_PERIOD_MONTHS = 3;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    public BookingService(BookingRepository bookingRepository, NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    public ResponseEntity<String> quickBookMeal(String userId, MealType mealType) {
        if (!isBookingBeforeCutoffTime()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick Booking should be made before 8 PM");
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (isWeekend(tomorrow)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick booking cannot be done for weekends.");
        }

        List<Booking> existingBookings = bookingRepository.findByUserIdAndStartDate(userId, tomorrow);
        if (!existingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has a booking for this date.");
        }

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setMealType(mealType);
        booking.setStartDate(tomorrow);
        booking.setEndDate(tomorrow);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        if (savedBooking != null) {
            String notificationMessage = "Quick booking successful for date: " + tomorrow;
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(notificationMessage);
            notificationService.sendNotification(notification);
            return ResponseEntity.ok(notificationMessage);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create booking.");
        }
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        if (hasBookingForDate(date, userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking already exists for date: " + date);
        }

        Booking booking = createBooking(userId, mealType, date, date);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        if (savedBooking != null) {
            String message = "Booking created successfully for date: " + date;
            sendNotification(userId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create booking.");
        }
    }

    private ResponseEntity<String> processBulkBooking(String userId, LocalDate endDate, MealType mealType, LocalDate startDate) {
        if (isDateInvalid(startDate) || isDateInvalid(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        if (!startDate.isBefore(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date.");
        }

        List<Booking> bookings = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isWeekend(date)) {
                endDate = endDate.plusDays(1);  // Extend the end date by one day for every weekend date skipped
                continue;
            }
            if (hasBookingForDate(date, userId)) {
                continue;
            }
            Booking booking = createBooking(userId, mealType, date, date);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookings.add(booking);
        }

        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid dates available for booking.");
        }

        List<Booking> savedBookings = bookingRepository.saveAll(bookings);
        if (!savedBookings.isEmpty()) {
            String message = "Bulk booking created successfully from " + startDate + " to " + endDate;
            sendNotification(userId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create bulk booking.");
        }
    }

    private boolean hasBookingForDate(LocalDate date, String userId) {
        List<Booking> existingBookings = bookingRepository.findByUserIdAndStartDate(userId, date);
        return existingBookings != null && !existingBookings.isEmpty();
    }

    private Booking createBooking(String userId, MealType mealType, LocalDate startDate, LocalDate endDate) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setMealType(mealType);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    private void sendNotification(String userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationService.sendNotification(notification);
    }

    public ResponseEntity<String> cancelBookings(List<Long> bookingIds) {
        // Check if cancellation is allowed based on the current time
        if (!isCancellationAllowed()) {
            return ResponseEntity.badRequest().body("Cancellation is not allowed after 10 PM.");
        }

        List<Notification> notifications = new ArrayList<>();

        try {
            for (Long bookingId : bookingIds) {
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

                // Update the booking status to cancelled
                booking.setStatus(BookingStatus.CANCELED);
                bookingRepository.save(booking);

                // Create a notification for each cancelled booking
                Notification notification = new Notification();
                notification.setMessage("Booking with ID " + bookingId + " cancelled successfully.");
                notificationRepository.save(notification);
                notifications.add(notification);
            }

            return ResponseEntity.ok("Bookings cancelled successfully.");
        } catch (Exception e) {
            // Rollback the status update and notification creation if any error occurs
            for (Notification notification : notifications) {
                notificationRepository.delete(notification);
            }
            return ResponseEntity.badRequest().body("Failed to cancel bookings. Please try again later.");
        }
    }

    public ResponseEntity<String> cancelBooking(Long bookingId) {
        // Check if cancellation is allowed based on the current time
        if (!isCancellationAllowed()) {
            return ResponseEntity.badRequest().body("Cancellation is not allowed after 10 PM.");
        }

        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

            // Update the booking status to cancelled
            booking.setStatus(BookingStatus.CANCELED);
            bookingRepository.save(booking);

            // Create a notification for the cancelled booking
            Notification notification = new Notification();
            notification.setMessage("Booking with ID " + bookingId + " cancelled successfully.");
            notificationRepository.save(notification);

            return ResponseEntity.ok("Booking with Booking ID " + bookingId + " cancelled successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to cancel booking with ID " + bookingId + ". Please try again later.");
        }
    }

    private boolean isCancellationAllowed() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.isBefore(LocalTime.of(22, 0));
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId);
    }
}
