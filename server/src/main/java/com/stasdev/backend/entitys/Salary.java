package com.stasdev.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;
    private BigDecimal amount;

    @JsonIgnore
    @OneToOne(mappedBy = "salary")
    private Employee employee;

    public Salary() {
    }

    public Salary(BigDecimal amount, Employee employee) {
        this.amount = amount;
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Salary setEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public Long getSalaryId() {
        return salaryId;
    }

    public Salary setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Salary setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
}
