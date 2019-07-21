package common.preconditions;

import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Salary;
import common.ApiFunctions;
import common.PreCondition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateEmployee implements PreCondition {

    private Employee employee;
    private ApiFunctions apiFunctions;

    public CreateEmployee(Employee employee, ApiFunctions apiFunctions) {
        this.employee = employee;
        this.apiFunctions = apiFunctions;
    }

    @Override
    public void execute() {
        ResponseEntity<Employee> employeeResponseEntity = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .postForEntity("/employees", employee, Employee.class);
        assertThat(employeeResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        apiFunctions.checkEmployeeExists(employee);
    }

    @Override
    public void undo() {
        apiFunctions
                .authAdmin()
                .restClientWithoutErrorHandler()
                .delete("/employees");
        apiFunctions.checkThatEveryOneEmployeesWasDeleted();
    }
}
