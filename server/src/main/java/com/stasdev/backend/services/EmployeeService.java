package com.stasdev.backend.services;

import com.stasdev.backend.entitys.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAll();

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    void deleteAll();
}
