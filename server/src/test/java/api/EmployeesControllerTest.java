package api;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Salary;
import common.preconditions.CreateEmployee;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class EmployeesControllerTest extends CommonApiTest {

    @Test
    void allEndpointsSecured() {
        ResponseEntity<String> deleteEmployees = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void anyoneCanGetAllEmployees() {
        ResponseEntity<List> forEntity = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getForEntity("/employees", List.class);

        assertThat(forEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void anyoneCanCreateEmployee() {
        Salary salary = new Salary().setAmount(new BigDecimal("10000"));
        Employee newEmployee = new Employee().setName("New Employee").setSalary(salary);
        ResponseEntity<Employee> employeeResponseEntity = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .postForEntity("/employees", newEmployee, Employee.class);
        assertThat(employeeResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        apiFunctions.checkEmployeeExists(newEmployee);
    }

    @Test
    void anyoneCanEditEmployee() {
        Salary salaryBeforeEdit = new Salary()
                .setAmount(new BigDecimal("100"));
        String createdUserName = "Created User";
        Employee createdUser = new Employee()
                .setName(createdUserName)
                .setSalary(salaryBeforeEdit);
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(createdUser, apiFunctions));
        Employee employeeByName = apiFunctions.findEmployeeByName(createdUserName);
        Salary salaryAfterEdit = employeeByName.getSalary();
        employeeByName.setSalary(salaryAfterEdit.setAmount(new BigDecimal("5000")));

        HttpEntity<Employee> request = new HttpEntity<>(employeeByName);
        ResponseEntity<Employee> updatedEmployeeRs = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .exchange("/employees", HttpMethod.PUT, request, Employee.class);


        assertThat(updatedEmployeeRs.getStatusCode(), equalTo(HttpStatus.OK));
        Employee body = updatedEmployeeRs.getBody();
        assert body != null;
        assertThat(body.getSalary(), equalTo(salaryAfterEdit));
    }

    @Test
    void userCanNotDeleteAllEmployees() {
        ResponseEntity<String> deleteEmployees = apiFunctions
                .authUser()
                .restClientWithoutErrorHandler()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void anyoneCanNotDeleteAllEmployees() {
        ResponseEntity<String> deleteEmployees = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void adminCanDeleteAllEmployees() {
        apiFunctions
                .authAdmin()
                .restClientWithoutErrorHandler()
                .delete("/employees");
        apiFunctions.checkThatEveryOneEmployeesWasDeleted();
    }

    @Test
    void anyoneCanNotEditEmployeeWhichNotExists() {
        apiFunctions.deleteAllEmployees();
        Employee employeeWhichNotExists = new Employee().setEmployeeId(1L);

        HttpEntity<Employee> request = new HttpEntity<>(employeeWhichNotExists);
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .nonAuth()
                        .restClientWithErrorHandler()
                        .exchange("/employees", HttpMethod.PUT, request, Employee.class));
        assertThat(runtimeException.getMessage(), containsString("Employee with id '"+employeeWhichNotExists.getEmployeeId()+"' not found"));
    }

    @Test
    void anyoneMustSpecifyEmployeeIdToEditEmployee() {
        Employee employeeWithNullId = new Employee().setEmployeeId(null);

        HttpEntity<Employee> request = new HttpEntity<>(employeeWithNullId);
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .nonAuth()
                        .restClientWithErrorHandler()
                        .exchange("/employees", HttpMethod.PUT, request, Employee.class));
        assertThat(runtimeException.getMessage(), containsString("You must specify employee's id to change his property"));
    }
}
