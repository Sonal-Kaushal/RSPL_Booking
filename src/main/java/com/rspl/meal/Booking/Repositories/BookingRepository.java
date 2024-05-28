package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndStartDate(String userId, LocalDate startDate);

    @Override
    Optional<Booking> findById(Long aLong);

    List<Booking> findByUserId(String userId);
}
