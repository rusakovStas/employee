package com.stasdev.backend.repos;

import com.stasdev.backend.entitys.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByAmount(BigDecimal bigDecimal);
}
