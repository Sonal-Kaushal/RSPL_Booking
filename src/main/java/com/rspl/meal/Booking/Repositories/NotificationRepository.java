
package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Entites.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEmployee(Employee employee);
}