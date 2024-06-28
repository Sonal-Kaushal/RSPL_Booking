package com.rspl.meal.Booking.Repositories;

import com.rspl.meal.Booking.Entites.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findFirstByEmail(String email);
    Optional<Employee> findById(Long employeeId);
    Employee findByname(String name);

    Employee findByEmail(String username);
    Optional<User> findByUsername(String username);
}