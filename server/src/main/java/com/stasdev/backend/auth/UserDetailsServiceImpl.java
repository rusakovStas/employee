package com.stasdev.backend.auth;

import com.stasdev.backend.entitys.ApplicationUser;
import com.stasdev.backend.entitys.Role;
import com.stasdev.backend.repos.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * В этом сервисе мы преобразовываем наши ентити с юзерами в UsersDetail которые знает спринг
 * */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    final private ApplicationUserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser byUsername = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with name '%s' not found", username)));


        return new User(byUsername.getUsername(),
                byUsername.getPassword(),//попадает сюда в закодированном виде
                byUsername.getRoles().stream()
                        .map(Role::getRole)
                        .map(String::toUpperCase)
                        .map(s -> "ROLE_" + s)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

}
