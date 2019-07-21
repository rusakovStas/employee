package com.stasdev.backend.services;

import com.stasdev.backend.entitys.ApplicationUser;
import com.stasdev.backend.entitys.Role;

import java.util.List;
import java.util.Set;

public interface UsersService {
    ApplicationUser createUser(ApplicationUser user);

    Set<Role> getUserRole(String username);

    List<ApplicationUser> getUsers();

    void deleteUserByUserName(String userName);
}
