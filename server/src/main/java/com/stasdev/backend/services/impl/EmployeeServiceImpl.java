package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Push;
import com.stasdev.backend.exceptions.EmployeeIsAlreadyExists;
import com.stasdev.backend.exceptions.EmployeeNotFound;
import com.stasdev.backend.exceptions.EmployeeWithNullId;
import com.stasdev.backend.repos.EmployeeRepository;
import com.stasdev.backend.services.EmployeeService;
import com.stasdev.backend.services.help.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmployeeRepository employeeRepository;
    private final Validator<Employee> validator;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, Validator<Employee> validator, SimpMessagingTemplate simpMessagingTemplate) {
        this.employeeRepository = employeeRepository;
        this.validator = validator;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        employeeRepository
                .findByName(employee.getName())
                .ifPresent(e -> {
                    throw new EmployeeIsAlreadyExists("Employee with name '" + e.getName() + "' is already exists");
                });
        Employee createdEmployee = employeeRepository.saveAndFlush(employee);
        System.out.println(createdEmployee);
        simpMessagingTemplate.convertAndSend("/topic/push", new Push("Time to update"));
        return createdEmployee;
    }

    @Override
    @Transactional
    public Employee updateEmployee(Employee employee) {
        validator.validate(employee);
        employeeRepository
                .findById(employee.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFound("Employee with id '" + employee.getEmployeeId() + "' not found"));
        Employee updatedEmployee = employeeRepository.saveAndFlush(employee);
        simpMessagingTemplate.convertAndSend("/topic/push", new Push("Time to update"));
        return updatedEmployee;
    }

    @Override
    @Transactional
    public void deleteAll() {
        employeeRepository.deleteAll();
        employeeRepository.flush();
        simpMessagingTemplate.convertAndSend("/topic/push", new Push("Time to update"));
    }

}
