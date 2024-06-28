package com.rspl.meal.Booking.Controller;


import com.rspl.meal.Booking.Entites.Employee;
import com.rspl.meal.Booking.Repositories.EmployeeRepository;
import com.rspl.meal.Booking.Services.AuthService;
import com.rspl.meal.Booking.Services.jwt.UserDetailsServiceImpl;
import com.rspl.meal.Booking.Services.util.JwtUtil;
import com.rspl.meal.Booking.dto.AuthenticationRequest;
import com.rspl.meal.Booking.dto.AuthenticationResponse;
import com.rspl.meal.Booking.dto.EmployeeDto;
import com.rspl.meal.Booking.dto.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@CrossOrigin("**")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, EmployeeRepository employeeRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/home")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok("Welcome to home page!");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupuser(@RequestBody SignupRequest signupRequest ){
        if(employeeRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()){
            return ResponseEntity.status(409).body("User Already Exists");
        }
        try{
            EmployeeDto createdUserDto = authService.createUser(signupRequest);
            return new ResponseEntity<>(createdUserDto,HttpStatus.CREATED);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),authenticationRequest.getPassword()));
        }
        catch(Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Wrong username or password\"}");
            response.getWriter().flush();
            return null;
        }
//        }catch(BadCredentialsException e){
//            throw new BadCredentialsException("Incorrect UserName or Password");
//        }catch(DisabledException disabledException){
//            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User Not Active");
//            return null;
//        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        Optional<Employee> optionalUser = employeeRepository.findFirstByEmail(userDetails.getUsername());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if(optionalUser.isPresent()){
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
            authenticationResponse.setUserId((optionalUser.get().getId()));
        }
        return authenticationResponse;
    }
}