package com.rspl.meal.Booking.dto;

import com.rspl.meal.Booking.Entites.Booking;
import com.rspl.meal.Booking.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class EmployeeDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    public EmployeeDto(Long id, String name, String email) {
    }

    public EmployeeDto() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
