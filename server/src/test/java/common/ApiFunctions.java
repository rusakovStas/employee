package common;

import com.stasdev.backend.auth.SecurityConstants;
import com.stasdev.backend.entitys.ApplicationUser;
import com.stasdev.backend.entitys.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Component
public class ApiFunctions {

    protected int port = TestProperties.getInstance().getAppPort();

    private static final String DEFAULT_PASSWORD = "Password";

    @Autowired
    private TestRestTemplate restClient;

    private void clear(){
        restClient.getRestTemplate().getInterceptors().clear();
        //Устанавливаем "пустой" обработчик ошибок
        restClient.getRestTemplate().setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
    }

    public AccessToRestClient authByUser(String username, String password){

        clear();
        restClient.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    return execution.execute(request, body);
                }));
        var token = restClient.postForEntity(String.format("/authenticate?username=%s&password=%s", username, password), null, Map.class);
        var tokenHeaders = token.getHeaders();
        var access_token = tokenHeaders.getOrDefault(SecurityConstants.TOKEN_HEADER, Collections.singletonList("no token")).get(0);
        assertThat(access_token, not(equalTo("no token")));
        restClient.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add(SecurityConstants.TOKEN_HEADER, "Bearer " + access_token);
                    return execution.execute(request, body);
                }));
        return new AccessToRestClient(restClient);
    }

    public AccessToRestClient authAdmin(){
        return authByUser("admin", "pass");
    }

    public AccessToRestClient authUser(){
        return authByUser("user", "pass");
    }

    public AccessToRestClient nonAuth(){
        clear();
        return new AccessToRestClient(restClient);
    }


    public class AccessToRestClient {

        private TestRestTemplate testRestTemplate;

        private AccessToRestClient(TestRestTemplate  template){
            this.testRestTemplate = template;
        }

        public Actions restClientWithoutErrorHandler() {
            return new Actions(testRestTemplate);
        }

        public Actions restClientWithErrorHandler(){
            restClient.getRestTemplate().setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    return response.getStatusCode().isError();
                }

                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    StringBuilder textBuilder = new StringBuilder();
                    try (Reader reader = new BufferedReader(new InputStreamReader
                            (response.getBody(), Charset.forName(StandardCharsets.UTF_8.name())))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            textBuilder.append((char) c);
                        }
                    }
                    throw new RuntimeException(textBuilder.toString());
                }
            });
            return new Actions(testRestTemplate);
        }
    }

    public class Actions {
        private TestRestTemplate testRestTemplate;

        public Actions(TestRestTemplate testRestTemplate) {
            this.testRestTemplate = testRestTemplate;
        }

        public EmployeesActions employeeActions(){
            return new EmployeesActions(testRestTemplate);
        }

        public UsersActions usersActions(){
            return new UsersActions(testRestTemplate);
        }

        public TestRestTemplate getTestRestTemplate(){
            return testRestTemplate;
        }
    }


    public class EmployeesActions {

        private TestRestTemplate testRestTemplate;

        public EmployeesActions(TestRestTemplate testRestTemplate) {
            this.testRestTemplate = testRestTemplate;
        }

        public void checkExists(Employee employee){
            ResponseEntity<List<Employee>> allEmployeesRs = testRestTemplate
                    .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
            List<Employee> allEmployees = allEmployeesRs.getBody();
            assert allEmployees != null;
            assertThat(allEmployees.stream().anyMatch(em -> em.getName().equals(employee.getName())), is(true));
        }

        public Employee create(Employee employee){
            ResponseEntity<Employee> employeeResponseEntity = testRestTemplate
                    .postForEntity("/employees", employee, Employee.class);
            assertThat(employeeResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));
            checkExists(employee);
            return employeeResponseEntity.getBody();
        }

        public Employee edit(Employee employee){
            ResponseEntity<Employee> editedEmployee = testRestTemplate.exchange("/employees", HttpMethod.PUT, new HttpEntity<>(employee), Employee.class);
            return editedEmployee.getBody();
        }

        public void checkThatEveryOneWereDeleted(){
            ResponseEntity<List<Employee>> allEmployeesRs = testRestTemplate
                    .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
            List<Employee> allEmployees = allEmployeesRs.getBody();
            assertThat(allEmployees.size(), is(0));
        }

        public Employee findByName(String name){
            ResponseEntity<List<Employee>> allEmployeesRs = testRestTemplate
                    .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
            List<Employee> allEmployees = allEmployeesRs.getBody();
            assert allEmployees != null;
            return allEmployees
                    .stream()
                    .filter(em -> em.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Employees with name "+ name + " is not exists"));
        }

        public void deleteAll(){
            testRestTemplate.delete("/employees");
        }

    }


    public class UsersActions {
        private TestRestTemplate testRestTemplate;

        public UsersActions(TestRestTemplate testRestTemplate) {
            this.testRestTemplate = testRestTemplate;
        }

        public ApplicationUser createUser(String name){
            ResponseEntity<ApplicationUser> applicationUserResponseEntity = testRestTemplate
                    .postForEntity("/users", new ApplicationUser(name, DEFAULT_PASSWORD), ApplicationUser.class);
            return applicationUserResponseEntity.getBody();
        }

        public ApplicationUser findUser(String name){
            var allUserRs = testRestTemplate
                    .exchange("/users/all", HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
            var allUsers = allUserRs.getBody();
            assert allUsers != null;
            var foundUsers = allUsers.stream()
                    .filter(u -> u.getUsername().equals(name))
                    .collect(Collectors.toList());
            assertThat(foundUsers.size(), is(1));
            return foundUsers.get(0);
        }

        public List<ApplicationUser> getAllUsers(){
            ResponseEntity<List<ApplicationUser>> allUserRs = testRestTemplate
                    .exchange("/users/all", HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
            List<ApplicationUser> allUsers = allUserRs.getBody();
            assert allUsers != null;
            return allUsers;
        }

        public void checkUserExists(String userName){
            ResponseEntity<List<ApplicationUser>> allUserRs = testRestTemplate
                    .exchange("/users/all", HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
            List<ApplicationUser> allUsers = allUserRs.getBody();
            assert allUsers != null;
            assertThat(allUsers.stream().anyMatch(u -> u.getUsername().equals(userName)), is(true));
        }

        public void checkUserNotExists(String userName){
            ResponseEntity<List<ApplicationUser>> allUserRs = testRestTemplate
                    .exchange("/users/all",HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
            List<ApplicationUser> allUsers = allUserRs.getBody();
            assert allUsers != null;
            assertThat(allUsers.stream().anyMatch(u -> u.getUsername().equals(userName)), is(false));
        }

        public void deleteUser(String name){
            testRestTemplate.delete("/users?username="+name);
        }
    }

    public void createUserByUser(String createdUser){
        authUser()
                .restClientWithErrorHandler()
                .getTestRestTemplate()
                .postForEntity("/users", new ApplicationUser(createdUser, DEFAULT_PASSWORD), ApplicationUser.class);
    }

    public ResponseEntity<ApplicationUser> createUserByAdmin(String userName){
        return authAdmin()
                .restClientWithErrorHandler()
                .getTestRestTemplate()
                .postForEntity("/users", new ApplicationUser(userName, DEFAULT_PASSWORD), ApplicationUser.class);
    }

    public void checkUserExists(String userName){
        ResponseEntity<List<ApplicationUser>> allUserRs = authAdmin().restClientWithoutErrorHandler().getTestRestTemplate()
                .exchange("/users/all", HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
        List<ApplicationUser> allUsers = allUserRs.getBody();
        assert allUsers != null;
        assertThat(allUsers.stream().anyMatch(u -> u.getUsername().equals(userName)), is(true));
    }

    public ApplicationUser findUserByAdmin(String userName){
        var allUserRs = authAdmin()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .exchange("/users/all", HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
        var allUsers = allUserRs.getBody();
        assert allUsers != null;
        var foundUsers = allUsers.stream()
                .filter(u -> u.getUsername().equals(userName))
                .collect(Collectors.toList());
        assertThat(foundUsers.size(), is(1));
        return foundUsers.get(0);
    }

    public void checkUserNotExists(String userName){
        ResponseEntity<List<ApplicationUser>> allUserRs = authAdmin().restClientWithoutErrorHandler().getTestRestTemplate()
                .exchange("/users/all",HttpMethod.GET,null, new ParameterizedTypeReference<List<ApplicationUser>>(){} );
        List<ApplicationUser> allUsers = allUserRs.getBody();
        assert allUsers != null;
        assertThat(allUsers.stream().anyMatch(u -> u.getUsername().equals(userName)), is(false));
    }

    public void checkEmployeeExists(Employee employee){
        ResponseEntity<List<Employee>> allEmployeesRs = nonAuth().restClientWithoutErrorHandler().getTestRestTemplate()
                .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
        List<Employee> allEmployees = allEmployeesRs.getBody();
        assert allEmployees != null;
        assertThat(allEmployees.stream().anyMatch(em -> em.getName().equals(employee.getName())), is(true));
    }

    public void checkThatEveryOneEmployeesWasDeleted(){
        ResponseEntity<List<Employee>> allEmployeesRs = nonAuth().restClientWithoutErrorHandler().getTestRestTemplate()
                .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
        List<Employee> allEmployees = allEmployeesRs.getBody();
        assertThat(allEmployees.size(), is(0));
    }

    public void deleteAllEmployees(){
        authAdmin()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .delete("/employees");
        checkThatEveryOneEmployeesWasDeleted();
    }

    public Employee findEmployeeByName(String name){
        ResponseEntity<List<Employee>> allEmployeesRs = nonAuth().restClientWithoutErrorHandler().getTestRestTemplate()
                .exchange("/employees", HttpMethod.GET,null, new ParameterizedTypeReference<List<Employee>>(){} );
        List<Employee> allEmployees = allEmployeesRs.getBody();
        assert allEmployees != null;
        return allEmployees
                .stream()
                .filter(em -> em.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Employees with name "+ name + " is not exists"));
    }

    public Employee createEmployee(Employee employee){
        ResponseEntity<Employee> employeeResponseEntity = nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .postForEntity("/employees", employee, Employee.class);
        assertThat(employeeResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        checkEmployeeExists(employee);
        return employeeResponseEntity.getBody();
    }

}
