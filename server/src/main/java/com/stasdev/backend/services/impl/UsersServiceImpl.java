package com.stasdev.backend.services.impl;

import com.stasdev.backend.entitys.ApplicationUser;
import com.stasdev.backend.entitys.Role;
import com.stasdev.backend.exceptions.AdminDeleteForbidden;
import com.stasdev.backend.exceptions.UserIsAlreadyExist;
import com.stasdev.backend.exceptions.UserNotFound;
import com.stasdev.backend.repos.ApplicationUserRepository;
import com.stasdev.backend.repos.RoleRepository;
import com.stasdev.backend.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UsersServiceImpl implements UsersService {

    private final ApplicationUserRepository repository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UsersServiceImpl(ApplicationUserRepository repository, PasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public ApplicationUser createUser(ApplicationUser user){
        if (repository.findByUsername(user.getUsername()).isPresent()){
            throw new UserIsAlreadyExist(String.format("User with name '%s' already exists!", user.getUsername()));
        }
        Role userRole = roleRepository.findByRole("user").orElse(new Role("user"));
        return repository.saveAndFlush(
                new ApplicationUser(user.getUsername(),
                bCryptPasswordEncoder.encode(user.getPassword()),
                Collections.singleton(userRole))
        );
    }

    @Override
    public Set<Role> getUserRole(String username){
        return repository.findByUsername(username).orElseThrow(UserNotFound::new).getRoles();
    }

    @Override
    public List<ApplicationUser> getUsers(){
        return repository.findAll();
    }

    @Override
    public void deleteUserByUserName(String userName) {
        ApplicationUser byUsername = repository.findByUsername(userName).orElseThrow(() -> new UserNotFound(String.format("User with name '%s' not found!", userName)));
        if (byUsername == null){
            throw new UserNotFound(String.format("User with name '%s' not found!", userName));
        }
        if (byUsername.getUsername().equals("admin")){
            throw new AdminDeleteForbidden("You can not delete admin!");
        }
        repository.deleteById(byUsername.getUser_id());
    }
}
