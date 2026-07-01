package com.genc.hrms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Table(name = "employees")
@Data // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long EmployeeId;

    @NotBlank(message = "Employee name is mandatory")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Role/Designation is mandatory")
    @Column(nullable = false, length = 50)
    private String role;

    @NotBlank(message = "Department is mandatory")
    @Column(nullable = false, length = 50)
    private String department;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    @Column(nullable = false)
    private Double salary;

    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    // New fields to match service code
    @Column(length = 50)
    private String designation; // e.g. "Software Engineer", "Manager"

    @Column(length = 20)
    private String status; // e.g. "Active", "Inactive", "On Leave"

    // Self-referencing relationship for manager assignment
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
}

