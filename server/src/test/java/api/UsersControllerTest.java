package api;

import common.preconditions.CreateUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


/*
 * Тесты для Users API
 * проверяются защищеннсоть всех ендпоинтов
 * проверяется функционал админа и функционал обычного юзера
 * */
class UsersControllerTest extends CommonApiTest {

    @Test
    void allEndpointsSecured() {
        ResponseEntity<String> allUsers = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .getForEntity("/users/all", String.class);
        assertThat(allUsers.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));

        ResponseEntity<String> createUser = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .postForEntity("/users",null ,String.class);
        assertThat(createUser.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));

        ResponseEntity<String> deleteUser = apiFunctions
                .nonAuth()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .exchange("/users?username=user", HttpMethod.DELETE, null, String.class);
        assertThat(deleteUser.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }


    @Test
    void adminCanNotCreateUserWithSameUsernameMoreThenOneTime() {
        String userName = "UserForTestRestriction";
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateUser(userName, apiFunctions));

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .authAdmin()
                        .restClientWithErrorHandler()
                        .usersActions()
                        .create(userName));
        assertThat(runtimeException.getMessage(), containsString("User with name '"+userName+"' already exists!"));
    }

    @Test
    void adminCanSeeAllUsers() {
        ResponseEntity<List> forEntity = apiFunctions.authAdmin()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .getForEntity("/users/all", List.class);

        assertThat(forEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void userCanNotCreateUser() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions
                        .authUser()
                        .restClientWithErrorHandler()
                        .usersActions()
                        .create( "UserForTestRestriction"));
        assertThat(runtimeException.getMessage(), containsString("Forbidden"));
    }

    @Test
    void userCanNotSeeAnotherUser() {
        ResponseEntity<String> all = apiFunctions.authUser()
                .restClientWithoutErrorHandler()
                .getTestRestTemplate()
                .getForEntity("/users/all", String.class);

        assertThat(all.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void adminCanDeleteUser() {
        String userName = "UserForDelete";

        apiFunctions
                .authAdmin()
                .restClientWithoutErrorHandler()
                .usersActions()
                .create(userName);
        apiFunctions.checkUserExists(userName);

        apiFunctions
                .authAdmin()
                .restClientWithErrorHandler()
                .usersActions()
                .delete(userName);

        apiFunctions.checkUserNotExists(userName);
    }

    @Test
    void adminCanCreateUser() {
        String userName = "UserForCheckCreate";
        preConditionExecutor.executeAndAddToQueueToUndo(new CreateUser(userName, apiFunctions));

        var createdUser = apiFunctions
                .authAdmin()
                .restClientWithoutErrorHandler()
                .usersActions()
                .find(userName);

        assertThat(createdUser.getUsername(), equalTo(userName));
        assertThat(createdUser.getPassword(), notNullValue());//пароль не проверяем потому что зашифровано
        assertThat(createdUser.getUser_id(), notNullValue());
        assertThat(createdUser.getRoles(), hasSize(1));
        assertThat(createdUser.getRoles(), hasItem(hasProperty("role", equalTo("user"))));
    }

    @Test
    void userCanNotDeleteUser() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> apiFunctions.authUser()
                        .restClientWithErrorHandler()
                        .usersActions()
                        .delete("user"));
        assertThat(runtimeException.getMessage(), containsString("Forbidden"));
    }

}
