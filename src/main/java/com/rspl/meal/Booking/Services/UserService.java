package com.rspl.meal.Booking.Services;


import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Employee findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<Employee> getEmployeeById(Long id){
        return userRepository.findById(id);
    }


    public Optional<Employee> findById(Long id){
        return userRepository.findById(id);
    }

    public void updatePassword(Employee employee, String newPassword){
        employee.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(employee);
    }

    public Employee getCurrentEmployee(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserDetails){
            String username = ((UserDetails)principal).getUsername();
            return userRepository.findByEmail(username);
        }
        return null;
    }

}