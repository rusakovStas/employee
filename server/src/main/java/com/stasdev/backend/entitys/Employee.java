package com.stasdev.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="salaryId")
    private Salary salary;

    public Salary getSalary() {
        return salary;
    }

    public Employee setSalary(Salary salary) {
        this.salary = salary;
        return this;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Employee setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Employee setName(String name) {
        this.name = name;
        return this;
    }
}
