package com.rspl.meal.Booking.Services;



import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import com.rspl.meal.Booking.dto.EmployeeDto;
import com.rspl.meal.Booking.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    EmployeeDto createUser(SignupRequest signupRequest);
}
