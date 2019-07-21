package com.stasdev.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salary)) return false;
        Salary salary = (Salary) o;
        return Objects.equals(getSalaryId(), salary.getSalaryId()) &&
                Objects.equals(getAmount(), salary.getAmount()) &&
                Objects.equals(getEmployee(), salary.getEmployee());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSalaryId(), getAmount(), getEmployee());
    }

    @Override
    public String toString() {
        return "Salary{" +
                "salaryId=" + salaryId +
                ", amount=" + amount +
                ", employee=" + employee +
                '}';
    }
}
