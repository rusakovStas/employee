package com.stasdev.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
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

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getEmployeeId(), employee.getEmployeeId()) &&
                Objects.equals(getName(), employee.getName()) &&
                Objects.equals(getSalary(), employee.getSalary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId(), getName(), getSalary());
    }
}
