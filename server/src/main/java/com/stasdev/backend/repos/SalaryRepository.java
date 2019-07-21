package com.stasdev.backend.repos;

import com.stasdev.backend.entitys.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
}
