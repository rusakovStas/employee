package common.preconditions;

import common.ApiFunctions;
import common.PreCondition;

public class CreateUser implements PreCondition{

    private String userName;
    private ApiFunctions apiFunctions;

    public CreateUser(String userName, ApiFunctions apiFunctions) {
        this.apiFunctions = apiFunctions;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public CreateUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public void execute() {
        apiFunctions.createUserByAdmin(userName);
        apiFunctions.checkUserExists(userName);
    }

    @Override
    public void undo() {
        apiFunctions.authAdmin()
                .restClientWithErrorHandler()
                .getTestRestTemplate()
                .delete("/users?username="+userName);
        apiFunctions.checkUserNotExists(userName);
    }
}
