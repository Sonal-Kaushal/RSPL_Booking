package com.rspl.meal.Booking.Services;

import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository; // Inject your EmployeeRepository or data access object

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with username: " + username));

        return buildUserDetails(employee);
    }

    private UserDetails buildUserDetails(User employee) {
        return new org.springframework.security.core.userdetails.User(
                employee.getUsername(),
                employee.getPassword()
                mapRolesToAuthorities(employee.getUserRole())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // Manually add ROLE_CUSTOMER
        authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return authorities;
    }
}
