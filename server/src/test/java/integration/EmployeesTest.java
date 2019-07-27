package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Salary;
import common.TestProperties;
import common.preconditions.CreateEmployee;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

class EmployeesTest extends CommonUITest {

    void login(String userName, String pass) {
        int appPort = TestProperties.getInstance().getAppPort();
        Configuration.baseUrl = Configuration.baseUrl
                .replace(":8080", "")
                .replace("http://", "");
//      Эта команда откроет только один браузер в одном потоке, т.е. переоткрываться не будет при каждом тесте
        open(String.format("http://%s:%d", Configuration.baseUrl, appPort));

        Selenide.clearBrowserLocalStorage();
        Configuration.timeout = 10_000;

        $("#email").setValue(userName);
        $("#password").setValue(pass);
        $(byText("Login")).click();
        $(byText("Home")).shouldBe(visible);
    }

    @Test
    void anyUserCanCreateEmployee() {
        String newEmployeeName = "New employee";

        login("user", "pass");
        $(byText("Employees")).click();
        $("#name").setValue(newEmployeeName);
        $("#amount").setValue("1000");
        $(byText("Create new employee")).click();

        $$(byText(newEmployeeName)).shouldHaveSize(1);
    }

    @Test
    void anyUserCanEditAmountOfEmployee() {
        String forEditName = "For edit";
        String notEditName = "Not for edit";
        Employee forEdit = new Employee()
                .setName(forEditName)
                .setSalary(new Salary().setAmount(new BigDecimal("1000")));
        Employee notEdit = new Employee()
                .setName(notEditName)
                .setSalary(new Salary().setAmount(new BigDecimal("1000")));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(forEdit, apiFunctions));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(notEdit, apiFunctions));
        login("user", "pass");
        $(byText("Employees")).click();
        SelenideElement forEditEmployee = $$(byText(forEditName)).shouldHaveSize(1).get(0).closest("div");
        SelenideElement notEditEmployee = $$(byText(notEditName)).shouldHaveSize(1).get(0).closest("div");

        notEditEmployee.$("#amountOfEmployee").shouldHave(value("1 000"));
        forEditEmployee.$("#amountOfEmployee").shouldHave(value("1 000"));
        forEditEmployee.$(byText("Change amount")).click();
        forEditEmployee.$("#amountOfEmployee").setValue("4444");
        forEditEmployee.$(byText("Accept")).shouldBe(enabled).click();
        forEditEmployee.$("#amountOfEmployee").shouldHave(value("4 444"));
        notEditEmployee.$("#amountOfEmployee").shouldHave(value("1 000"));
    }

    @Test
    void anyUserGetErrorWhenHeTryToCreateEmployeeWithNameWhichAlreadyExists() {
        String alreadyExistsName = "Already exists";

        Employee alreadyExists = new Employee()
                .setName(alreadyExistsName)
                .setSalary(new Salary().setAmount(new BigDecimal("1000")));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(alreadyExists, apiFunctions));
        login("user", "pass");
        $(byText("Employees")).click();
        $("#name").setValue(alreadyExistsName);
        $("#amount").setValue("3000");
        $(byText("Create new employee")).click();
        $(byText("Employee with name '" + alreadyExistsName + "' is already exists")).shouldBe(visible);
    }

    @Test
    void adminCanDeleteAllEmployees() {
        String nameForDelete = "I'm exist only to be deleted";

        Employee employeeForDelete = new Employee()
                .setName(nameForDelete)
                .setSalary(new Salary().setAmount(new BigDecimal("1000")));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(employeeForDelete, apiFunctions));
        login("admin", "pass");
        $(byText("Employees")).click();
        $$(byText(nameForDelete)).shouldHaveSize(1);
        $(byText("Delete all employees")).shouldBe(enabled).click();
        $$(byText(nameForDelete)).shouldHaveSize(0);
    }

    @Test
    void anyUserCanSeeChangesInEmployeesByAnotherUser() {
        String createdByAnotherUserName = "I'm created by another user";

        login("user", "pass");
        $(byText("Employees")).click();
        $$(byText(createdByAnotherUserName)).shouldHaveSize(0);
        Employee alreadyExists = new Employee()
                .setName(createdByAnotherUserName)
                .setSalary(new Salary().setAmount(new BigDecimal("1000")));
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateEmployee(alreadyExists, apiFunctions));
        $$(byText(createdByAnotherUserName)).shouldHaveSize(1);
    }
}
