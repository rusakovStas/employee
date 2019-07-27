package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Push;
import com.stasdev.backend.entitys.Salary;
import com.stasdev.backend.exceptions.*;
import com.stasdev.backend.repos.EmployeeRepository;
import com.stasdev.backend.repos.SalaryRepository;
import com.stasdev.backend.services.help.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    private static final String TOPIC_PUSH = "/topic/push";
    private final EmployeeRepository repository = mock(EmployeeRepository.class);
    private final Validator<Employee> validator = (Validator<Employee>) mock(Validator.class);
    private final SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
    private final SalaryRepository salaryRepository = mock(SalaryRepository.class);
    private EmployeeServiceImpl employeeService = new EmployeeServiceImpl(repository,
                                                                            validator,
                                                                            simpMessagingTemplate,
                                                                            salaryRepository);
    private Push timeToUpdate = new Push("Time to update");

    @Test
    void testGetAll() {
//      Act
        employeeService.getAll();

//      Assert
        verify(repository, times(1)).findAll();
    }

    @Test
    void testCantCreateEmployeeWithAlreadyExistedName() {
//      Arrange
        String employeeName = "already created employee";
        Employee employee = new Employee().setName(employeeName);
        when(repository.findByName(employeeName)).thenReturn(Optional.of(employee));
//      Act
        EmployeeIsAlreadyExists employeeIsAlreadyExists = assertThrows(EmployeeIsAlreadyExists.class, () -> employeeService.createEmployee(employee));
//      Assert
        assertThat(employeeIsAlreadyExists.getMessage(), is("Employee with name '" + employeeName + "' is already exists"));
        verify(simpMessagingTemplate, times(0)).convertAndSend(TOPIC_PUSH, timeToUpdate);
        verify(repository, times(0)).saveAndFlush(any());
        verify(repository, times(1)).findByName(employeeName);
    }

    @Test
    void testCantUpdateEmployeeWithIdWhichNotExists() {
//      Arrange
        Employee employeeWhichNotExists = new Employee()
                .setEmployeeId(10L)
                .setName("Some name");
        when(repository.findById(10L)).thenReturn(Optional.empty());
//      Act
        EmployeeNotFound employeeNotFound = assertThrows(EmployeeNotFound.class, () -> employeeService.updateEmployee(employeeWhichNotExists));
//      Assert
        assertThat(employeeNotFound.getMessage(), is("Employee with id '10' not found"));
        verify(repository, times(1)).findById(10L);
        verify(repository, times(0)).saveAndFlush(any());
        verify(simpMessagingTemplate, times(0)).convertAndSend(TOPIC_PUSH, timeToUpdate);
        verify(validator, times(1)).validate(employeeWhichNotExists);
    }

    @Test
    void testCantUpdateEmployeeWithNullId() {
//      Arrange
        Employee employeeWithNullId = new Employee()
                .setEmployeeId(null)
                .setName("Some name");
        doThrow(EmployeeWithNullId.class).when(validator).validate(employeeWithNullId);
//      Act
        assertThrows(EmployeeWithNullId.class, () -> employeeService.updateEmployee(employeeWithNullId));
//      Assert
        verify(repository, times(0)).findById(anyLong());
        verify(repository, times(0)).saveAndFlush(any());
        verify(simpMessagingTemplate, times(0)).convertAndSend(TOPIC_PUSH, timeToUpdate);
        verify(validator, times(1)).validate(employeeWithNullId);
    }

    @Test
    void testCreateEmployeeWhenSalaryWithSameAmountIsExist() {
//      Arrange
        BigDecimal amount = new BigDecimal("1000");
        Salary existedSalary = new Salary().setSalaryId(1L).setAmount(amount);
        String newEmployeeName = "New Employee";
        Employee employeeWithExistedAmount = new Employee().setSalary(new Salary().setAmount(amount)).setName(newEmployeeName);
        Employee employeeWithExistedSalary = new Employee().setSalary(existedSalary).setName(newEmployeeName);
        when(repository.findByName(newEmployeeName)).thenReturn(Optional.empty());
        when(salaryRepository.findByAmount(amount)).thenReturn(Optional.of(existedSalary));
//      Act
        employeeService.createEmployee(employeeWithExistedAmount);
//      Assert
        assertThat(employeeWithExistedAmount, equalTo(employeeWithExistedSalary));
        verify(repository, times(1)).findByName(newEmployeeName);
        verify(salaryRepository, times(1)).findByAmount(amount);
        verify(repository, times(1)).saveAndFlush(employeeWithExistedSalary);
    }

    @Test
    void testCreateEmployeeWhenSalaryWithSameAmountIsNotExist() {
//      Arrange
        BigDecimal amount = new BigDecimal("1000");
        Salary newSalary = new Salary().setSalaryId(null).setAmount(amount);
        String newEmployeeName = "New Employee";
        Employee employeeWithNonExistedAmount = new Employee().setSalary(newSalary).setName(newEmployeeName);
        when(repository.findByName(newEmployeeName)).thenReturn(Optional.empty());
        when(salaryRepository.findByAmount(amount)).thenReturn(Optional.empty());
//      Act
        employeeService.createEmployee(employeeWithNonExistedAmount);
//      Assert
        assertThat(employeeWithNonExistedAmount.getSalary(), equalTo(newSalary));
        verify(repository, times(1)).findByName(newEmployeeName);
        verify(salaryRepository, times(1)).findByAmount(amount);
        verify(repository, times(1)).saveAndFlush(employeeWithNonExistedAmount);
    }

    @Test
    void testUpdateEmployeeAmountWhenSalaryWithSameAmountIsExist() {
//      Arrange
        BigDecimal newAmount = new BigDecimal("1000");
        Salary existedSalaryWithAmount = new Salary().setSalaryId(1L).setAmount(newAmount);
        Salary salaryWithSameAmount = new Salary().setSalaryId(2L).setAmount(newAmount);
        String employeeName = "Employee";
        Employee updatedEmployee = new Employee()
                .setEmployeeId(1L)
                .setSalary(salaryWithSameAmount)
                .setName(employeeName);
        when(repository.findById(1L)).thenReturn(Optional.of(updatedEmployee));
        when(salaryRepository.findByAmount(newAmount)).thenReturn(Optional.of(existedSalaryWithAmount));
//      Act
        employeeService.updateEmployee(updatedEmployee);
//      Assert
        assertThat(updatedEmployee.getSalary(), equalTo(existedSalaryWithAmount));
        verify(validator, times(1)).validate(updatedEmployee);
        verify(salaryRepository, times(1)).findByAmount(newAmount);
        verify(repository, times(1)).saveAndFlush(updatedEmployee);
    }

    @Test
    void testUpdateEmployeeAmountWhenSalaryWithSameAmountIsNotExist() {
//      Arrange
        BigDecimal newAmount = new BigDecimal("1000");
        Salary salaryWithNewAmount = new Salary().setSalaryId(2L).setAmount(newAmount);
        String employeeName = "Employee";
        Employee updatedEmployee = new Employee()
                .setEmployeeId(1L)
                .setSalary(salaryWithNewAmount)
                .setName(employeeName);
        when(repository.findById(1L)).thenReturn(Optional.of(updatedEmployee));
        when(salaryRepository.findByAmount(newAmount)).thenReturn(Optional.empty());
//      Act
        employeeService.updateEmployee(updatedEmployee);
//      Assert
        assertThat(updatedEmployee.getSalary().getSalaryId(), is(nullValue()));
        verify(validator, times(1)).validate(updatedEmployee);
        verify(salaryRepository, times(1)).findByAmount(newAmount);
        verify(repository, times(1)).saveAndFlush(updatedEmployee);
    }


    @Test
    void testDeleteAll() {
//       Act
        employeeService.deleteAll();
//      Assert
        verify(repository, times(1)).deleteAll();
        verify(repository, times(1)).flush();
        timeToUpdate = new Push("Time to update");
        verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC_PUSH, timeToUpdate);
    }
}