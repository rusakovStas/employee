package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.exceptions.EmployeeIsAlreadyExists;
import com.stasdev.backend.exceptions.EmployeeNotFound;
import com.stasdev.backend.repos.EmployeeRepository;
import com.stasdev.backend.repos.SalaryRepository;
import com.stasdev.backend.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee createEmployee(Employee employee) {
        employeeRepository
                .findByName(employee.getName())
                .ifPresent(e -> {
                    throw new EmployeeIsAlreadyExists("Employee with name '" + e.getName() + "' is already exists");
                });
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        employeeRepository
                .findById(employee.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFound("Employee with id '" + employee.getEmployeeId() + "' not found"));
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public void deleteAll() {
        employeeRepository.deleteAll();
        employeeRepository.flush();
    }

}
