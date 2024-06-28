package com.rspl.meal.Booking.Services.jwt;


import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(EmployeeRepository userRepository) {
        this.employeeRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Write logic to get user from db
        Optional<Employee> optionalUser = employeeRepository.findFirstByEmail(email);

        if(optionalUser.isEmpty()) throw new UsernameNotFoundException("User Not Found",null);
        return new org.springframework.security.core.userdetails.User(optionalUser.get().getEmail(),optionalUser.get().getPassword(),new ArrayList<>());

    }
}
