package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Coupon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Coupon findByCouponId(String couponId);

    List<Coupon> findByExpirationTimeBefore(LocalDateTime expirationTime);
}
