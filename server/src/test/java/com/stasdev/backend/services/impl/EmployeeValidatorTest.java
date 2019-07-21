package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Salary;
import com.stasdev.backend.exceptions.EmployeeWithNullId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;



class EmployeeValidatorTest {

    EmployeeValidator employeeValidator = new EmployeeValidator();

    @Test
    void testValidateOk() {
//      Arrange
        Employee employeeWithId = new Employee().setEmployeeId(1L);
//      Act
        assertDoesNotThrow(() -> employeeValidator.validate(employeeWithId));
//      Assert
//      no exception
    }

    @Test
    void testValidateFalse() {
//       Arrange
        Employee employeeWithNullId = new Employee()
                .setName("some name")
                .setSalary(new Salary())
                .setEmployeeId(null);
//      Act
        EmployeeWithNullId exception = assertThrows(EmployeeWithNullId.class, () -> employeeValidator.validate(employeeWithNullId));
//      Assert
        assertThat(exception.getMessage(), is("You must specify employee's id to change his property"));
    }
}