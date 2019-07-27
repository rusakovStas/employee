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
                .getTestRestTemplate()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void anyoneCanGetAllEmployees() {
        ResponseEntity<List> forEntity = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .getForEntity("/employees", List.class);

        assertThat(forEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void anyoneCanCreateEmployee() {
        Salary salary = new Salary().setAmount(new BigDecimal("10000"));
        Employee newEmployee = new Employee().setName("New Employee").setSalary(salary);
        Employee createdEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .create(newEmployee);

        apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .checkExists(createdEmployee);
    }

    @Test
    void anyoneCanEditEmployeeWithNewSalary() {
        BigDecimal amountBeforeUpdate = new BigDecimal("1000");
        BigDecimal amountAfterUpdate = new BigDecimal("3000");

        Salary existedSalary = new Salary()
                .setSalaryId(null)
                .setAmount(amountBeforeUpdate);

        String firstEmployeeName = "Created User";
        Employee firstEmployee = new Employee()
                .setName(firstEmployeeName)
                .setSalary(existedSalary);

        String secondEmployeeName = "Second Created User";
        Employee secondEmployee = new Employee()
                .setName(secondEmployeeName)
                .setSalary(existedSalary);
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(firstEmployee, apiFunctions));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(secondEmployee, apiFunctions));

        Employee nonEditedEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .findByName(firstEmployeeName);

        secondEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .findByName(secondEmployeeName);

        Employee updatedEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .edit(secondEmployee.setSalary(new Salary().setAmount(amountAfterUpdate)));

        assertThat(nonEditedEmployee.getSalary().getAmount(), equalTo(amountBeforeUpdate));
        assertThat(updatedEmployee.getSalary().getSalaryId(), not(equalTo(firstEmployee.getSalary().getSalaryId())));
        assertThat(updatedEmployee.getSalary().getAmount(), equalTo(amountAfterUpdate));
    }

    @Test
    void anyoneCanEditEmployeeWithSalaryEqualsToSalaryAnotherEmployee() {
        Salary existedSalary = new Salary()
                .setSalaryId(null)
                .setAmount(new BigDecimal("100"));
        String createdEmployeeName = "Created User";
        Employee createdEmployee = new Employee()
                .setName(createdEmployeeName)
                .setSalary(existedSalary);
        Salary existedSecondSalary = new Salary()
                .setSalaryId(null)
                .setAmount(new BigDecimal("1001"));
        String createdSecondEmployeeName = "Second Created User";
        Employee createdSecondEmployee = new Employee()
                .setName(createdSecondEmployeeName)
                .setSalary(existedSecondSalary);
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(createdEmployee, apiFunctions));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(createdSecondEmployee, apiFunctions));

        Employee editedEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .findByName(createdSecondEmployeeName);

        Employee nonEditedEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .findByName(createdEmployeeName);

        editedEmployee.setSalary(existedSalary);
        Employee updatedEmployee = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .edit(editedEmployee);

        assertThat(updatedEmployee.getSalary(), equalTo(nonEditedEmployee.getSalary()));
    }

    @Test
    void userCanNotDeleteAllEmployees() {
        ResponseEntity<String> deleteEmployees = apiFunctions
                .authUser()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void anyoneCanNotDeleteAllEmployees() {
        ResponseEntity<String> deleteEmployees = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .exchange("/employees", HttpMethod.DELETE, null, String.class);
        assertThat(deleteEmployees.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void adminCanDeleteAllEmployees() {
        apiFunctions
                .authAdmin()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .deleteAll();

        apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .employeeActions()
                .checkThatEveryOneWereDeleted();
    }

    @Test
    void anyoneCanNotEditEmployeeWhichNotExists() {
        apiFunctions.deleteAllEmployees();
        Employee employeeWhichNotExists = new Employee().setEmployeeId(1L);

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .nonAuth()
                        .restClientWithErrorHandler()
                        .employeeActions()
                        .edit(employeeWhichNotExists));
        assertThat(runtimeException.getMessage(), containsString("Employee with id '"+employeeWhichNotExists.getEmployeeId()+"' not found"));
    }

    @Test
    void anyoneMustSpecifyEmployeeIdToEditEmployee() {
        Employee employeeWithNullId = new Employee().setEmployeeId(null);

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .nonAuth()
                        .restClientWithErrorHandler()
                        .employeeActions()
                        .edit(employeeWithNullId));
        assertThat(runtimeException.getMessage(), containsString("You must specify employee's id to change his property"));
    }
}
