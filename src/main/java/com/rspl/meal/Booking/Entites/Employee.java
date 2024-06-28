package com.rspl.meal.Booking.Entites;


import com.rspl.meal.Booking.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Builder
@Table(name = "user")
@AllArgsConstructor
//@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;

    public Employee() {

    }

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public Employee(Long employeeId) {
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

    public Collection<String> getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Employee( String email, Long id, String name, String password, UserRole userRole) {

        this.email = email;
        this.id = id;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
    }
}