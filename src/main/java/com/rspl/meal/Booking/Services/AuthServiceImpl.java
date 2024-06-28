package com.rspl.meal.Booking.Services;


import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import com.rspl.meal.Booking.dto.EmployeeDto;
import com.rspl.meal.Booking.dto.SignupRequest;
import com.rspl.meal.Booking.enums.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final EmployeeRepository employeeRepository;

    public AuthServiceImpl(EmployeeRepository userRepository) {
        this.employeeRepository = userRepository;
    }

    @Override
    public EmployeeDto createUser(SignupRequest signupRequest) {
        Employee user = new Employee();

        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setUserRole(UserRole.Customer);
        Employee createdUser = employeeRepository.save(user);
        EmployeeDto createdUserDto = new EmployeeDto();
        createdUserDto.setId(createdUser.getId());
        createdUserDto.setName(createdUser.getName());
        createdUserDto.setEmail(createdUser.getEmail());
        createdUserDto.setUserRole(createdUser.getUserRole());

        return createdUserDto;
    }
}
