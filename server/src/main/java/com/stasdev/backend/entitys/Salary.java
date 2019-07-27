package com.stasdev.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Set;

@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;
    private BigDecimal amount;

    @JsonIgnore
    @OneToMany(mappedBy = "salary")
    private Set<Employee> employees;

    public Salary() {
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
        this.amount = amount.setScale(0, RoundingMode.HALF_UP);
        return this;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public Salary setEmployees(Set<Employee> employees) {
        this.employees = employees;
        return this;
    }

    public Salary(BigDecimal amount, Set<Employee> employees) {
        this.amount = amount;
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salary)) return false;
        Salary salary = (Salary) o;
        return Objects.equals(getSalaryId(), salary.getSalaryId()) &&
                Objects.equals(getAmount(), salary.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSalaryId(), getAmount());
    }

    @Override
    public String toString() {
        return "Salary{" +
                "salaryId=" + salaryId +
                ", amount=" + amount +
                '}';
    }
}
