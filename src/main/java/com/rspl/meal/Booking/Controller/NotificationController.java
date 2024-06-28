package com.rspl.meal.Booking.Controller;

import com.rspl.meal.Booking.Entites.Notification;
import com.rspl.meal.Booking.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.sendNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<List<Notification>> getNotificationsByEmployeeId(@PathVariable Long bookingId) {
        List<Notification> notifications = notificationService.getNotificationsByEmployeeId(bookingId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
