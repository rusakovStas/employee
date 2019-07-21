package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.exceptions.EmployeeWithNullId;
import com.stasdev.backend.services.help.Validator;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator implements Validator<Employee> {

    @Override
    public void validate(Employee employee) {
        if (employee.getEmployeeId() == null){
            throw new EmployeeWithNullId("You must specify employee's id to change his property");
        }
    }
}
