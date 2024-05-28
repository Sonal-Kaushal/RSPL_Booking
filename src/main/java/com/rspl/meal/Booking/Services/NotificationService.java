package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.Entites.Notification;
import com.rspl.meal.Booking.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(Notification notification) {
        // Business logic to send and save notification
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
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
