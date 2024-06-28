package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Entites.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findByOtp(String otp);

    Otp findByUser(Employee user);
}
