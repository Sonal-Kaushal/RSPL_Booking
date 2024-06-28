package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Employee,Long> {
    Employee findByEmail(String email);

    Optional<Employee> findById(Long id);
    Optional<User> findByUsername(String username);
}