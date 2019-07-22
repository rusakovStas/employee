package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Push;
import com.stasdev.backend.exceptions.EmployeeIsAlreadyExists;
import com.stasdev.backend.exceptions.EmployeeNotFound;
import com.stasdev.backend.exceptions.EmployeeWithNullId;
import com.stasdev.backend.repos.EmployeeRepository;
import com.stasdev.backend.services.help.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    private static final String TOPIC_PUSH = "/topic/push";
    private final EmployeeRepository repository = mock(EmployeeRepository.class);
    private final Validator<Employee> validator = (Validator<Employee>) mock(Validator.class);
    private final SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
    private EmployeeServiceImpl employeeService = new EmployeeServiceImpl(repository, validator, simpMessagingTemplate);
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
    void testEmployeeCreate() {
//       Arrange
        String newEmployeeName = "Some name";
        Employee newEmployeeWithoutId = new Employee().setName(newEmployeeName);
        when(repository.findByName(newEmployeeName)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(newEmployeeWithoutId)).thenReturn(newEmployeeWithoutId.setEmployeeId(1L));
//      Act
        Employee employee = employeeService.createEmployee(newEmployeeWithoutId);
//      Assert
        assertThat(employee.getEmployeeId(), is(1L));
        assertThat(employee.getName(), is(newEmployeeName));
        verify(repository, times(1)).findByName(newEmployeeName);
        verify(repository, times(1)).saveAndFlush(newEmployeeWithoutId);
        verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC_PUSH, timeToUpdate);
    }

    @Test
    void testEmployeeCanBeUpdated() {
//    Arrange
        Employee existedEmployee = new Employee()
                .setEmployeeId(1L)
                .setName("Some name");
        Employee updatedEmployee = new Employee()
                .setEmployeeId(1L)
                .setName("Edited name");
        when(repository.findById(1L)).thenReturn(Optional.of(existedEmployee));
//      Act
        employeeService.updateEmployee(updatedEmployee);
//      Assert
        verify(validator,times(1)).validate(updatedEmployee);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(updatedEmployee);
        verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC_PUSH, timeToUpdate);
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