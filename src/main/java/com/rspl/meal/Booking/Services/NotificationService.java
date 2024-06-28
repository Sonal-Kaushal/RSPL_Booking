package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Entites.Notification;
import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import com.rspl.meal.Booking.Repositories.NotificationRepository;
import com.rspl.meal.Booking.dto.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Notification sendNotification(NotificationDto notification) {
        // Business logic to send and save notification
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        return notificationRepository.findByEmployee(employee);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void triggerBookingNotification(String bookingMessage) {
    }

    public void triggerCancellationNotification(String cancellationMessage) {

    }
}
