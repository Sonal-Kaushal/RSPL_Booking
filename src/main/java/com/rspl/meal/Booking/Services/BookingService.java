package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.dto.*;
import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Entites.Notification;
import com.rspl.meal.Booking.enums.BookingStatus;
import com.rspl.meal.Booking.enums.MealType;
import com.rspl.meal.Booking.Repositories.BookingRepository;
import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import com.rspl.meal.Booking.Repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final LocalTime CANCEL_CUTOFF_TIME = LocalTime.of(22, 0);
    private static final LocalTime CUTOFF_TIME = LocalTime.of(20, 0); // 8 PM cutoff
    private static final int BOOKING_PERIOD_MONTHS = 3;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public BookingService(BookingRepository bookingRepository, NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    public ResponseEntity<String> quickBookMeal(MealType mealType) {
        LoggedInUserDTO loggedInUser = getLoggedInUser();
        Long employeeId = loggedInUser.getId();
        if (!isBookingBeforeCutoffTime()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick Booking should be made before 8 PM");
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (isWeekend(tomorrow)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quick booking cannot be done for weekends.");
        }
        if (employeeId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee ID cannot be null.");
        }

        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not found.");
        }

        List<Booking> existingBookings = bookingRepository.findByEmployeeAndStartDate(employee, tomorrow);
        if (!existingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has a booking for this date.");
        }

        Booking booking = new Booking();
        booking.setEmployeeId(employeeId);
        booking.setMealType(mealType);
        booking.setStartDate(tomorrow);
        booking.setEndDate(tomorrow);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        if (savedBooking != null) {
            String notificationMessage = "Quick booking successful for date: " + tomorrow;
            NotificationDto notification = new NotificationDto();
            notification.setEmployeeId(employeeId);
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
            String endDateStr = (String) bookingDetails.get("endDate");
            String mealCategory = (String) bookingDetails.get("mealType");
            String startDateStr = (String) bookingDetails.get("startDate");

            if (bookingType == null || mealCategory == null || startDateStr == null) {
                return ResponseEntity.badRequest().body("Booking details are incomplete");
            }

            LoggedInUserDTO loggedInUser = getLoggedInUser();
            Long employeeId = loggedInUser.getId();
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
                return processSingleBooking(employeeId, mealType, startDate);
            } else if (bookingType.equalsIgnoreCase("bulk")) {
                return processBulkBooking(employeeId, endDate, mealType, startDate);
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

    private ResponseEntity<String> processSingleBooking(Long employeeId, MealType mealType, LocalDate date) {
        if (isDateInvalid(date)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        if (hasBookingForDate(date, employeeId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking already exists for date: " + date);
        }

        Booking booking = createBooking(employeeId, mealType, date, date);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        if (savedBooking != null) {
            String message = "Booking created successfully for date: " + date;
            sendNotification(employeeId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create booking.");
        }
    }

    private ResponseEntity<String> processBulkBooking(Long employeeId, LocalDate endDate, MealType mealType, LocalDate startDate) {
        if (isDateInvalid(startDate) || isDateInvalid(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        if (!startDate.isBefore(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date.");
        }

        List<Booking> bookings = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            if (!isWeekend(date) && !hasBookingForDate(date, employeeId)) {
                bookings.add(createBooking(employeeId, mealType, date, date));
            }
            date = date.plusDays(1);
        }

        bookingRepository.saveAll(bookings);
        String message = "Bulk booking created successfully from " + startDate + " to " + endDate;
        sendNotification(employeeId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    private boolean hasBookingForDate(LocalDate date, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return employee != null && !bookingRepository.findByEmployeeAndStartDate(employee, date).isEmpty();
    }

    private Booking createBooking(Long employeeId, MealType mealType, LocalDate startDate, LocalDate endDate) {
        Booking booking = new Booking();
        booking.setEmployeeId(employeeId);
        booking.setMealType(mealType);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    private void sendNotification(Long employeeId, String message) {
        NotificationDto notification = new NotificationDto();
        notification.setEmployeeId(employeeId);
        notification.setMessage(message);
        notificationService.sendNotification(notification);
    }

    public List<BookingDto> getUserBookingsForNextThreeMonths(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsLater = today.plusMonths(3);

        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        List<Booking> bookings = bookingRepository.findByEmployeeAndStartDateBetween(employee, today, threeMonthsLater);
        return bookings.stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getUserBookingsByMealType(Long employeeId, MealType mealType) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        List<Booking> bookings = bookingRepository.findByEmployeeAndMealType(employee, mealType);
        return bookings.stream()
                .map(this::convertToBookingDTO
                )
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> cancelMealBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
        }

        if (!isCancelAllowed(booking.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cancellation not allowed after 10 PM.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        String message = "Booking cancelled successfully.";
        sendNotification(booking.getEmployeeId(), message);
        return ResponseEntity.ok(message);
    }

    private boolean isCancelAllowed(LocalDate startDate) {
        return LocalTime.now().isBefore(CANCEL_CUTOFF_TIME) || !startDate.equals(LocalDate.now().plusDays(1));
    }

    private BookingDto convertToBookingDTO(Booking booking) {
        BookingDto bookingDTO = new BookingDto();
        bookingDTO.setId(booking.getEmployeeId());
        bookingDTO.setEmployeeId(booking.getEmployeeId());
        bookingDTO.setMealType(booking.getMealType());
        bookingDTO.setStartDate(booking.getStartDate());
        bookingDTO.setEndDate(booking.getEndDate());
        bookingDTO.setStatus(booking.getStatus());
        return bookingDTO;
    }

    private LoggedInUserDTO getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            Employee employee = employeeRepository.findByEmail(email);
            if (employee != null) {
                return new LoggedInUserDTO(employee.getId(), employee.getEmail(), employee.getName());
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
    }


}
