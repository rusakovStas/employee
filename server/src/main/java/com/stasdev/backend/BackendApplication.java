package com.stasdev.backend;

import com.stasdev.backend.entitys.ApplicationUser;
import com.stasdev.backend.entitys.Employee;
import com.stasdev.backend.entitys.Role;
import com.stasdev.backend.entitys.Salary;
import com.stasdev.backend.repos.ApplicationUserRepository;
import com.stasdev.backend.repos.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Component
	@Profile("firstStart")
	class UserCommandLineRunner implements CommandLineRunner {

		@Autowired
		ApplicationUserRepository repo;
		@Autowired
		PasswordEncoder bCryptPasswordEncoder;

		@Override
		public void run(String... args) throws Exception {
			System.out.println("*********************FIRST START*********************************");
			repo.saveAndFlush(
					new ApplicationUser("admin",
							bCryptPasswordEncoder.encode("pass"),
							Collections.singleton(new Role("admin")))
			);
		}
	}
	/**
	 * Преднастройки для теста
	 * Создается админ и юзер для более правильных и не пересекающихся тестов
	 * */
	@Component
	@Profile("test")
	class UserCommandLineRunnerTest implements CommandLineRunner {

		@Autowired
		EmployeeRepository employeeRepository;
		@Autowired
		ApplicationUserRepository repo;
		@Autowired
		PasswordEncoder bCryptPasswordEncoder;

		@Override
		public void run(String... args) throws Exception {
			System.out.println("*********************TEST*********************************");
			repo.saveAndFlush(
					new ApplicationUser("admin",
							bCryptPasswordEncoder.encode("pass"),
							Collections.singleton(new Role("admin")))
			);

			repo.saveAndFlush(
					new ApplicationUser("user",
							bCryptPasswordEncoder.encode("pass"),
							Collections.singleton(new Role("user")))
			);
		}
	}

	/**
	 * Преднастройки для теста
	 * Создается админ и юзер для более правильных и не пересекающихся тестов
	 * */
	@Component
	@Profile("dev")
	class UserCommandLineRunnerDev implements CommandLineRunner {

		@Autowired
		ApplicationUserRepository repo;
		@Autowired
		PasswordEncoder bCryptPasswordEncoder;

		@Override
		public void run(String... args) throws Exception {
			System.out.println("*********************DEV*********************************");
			repo.saveAndFlush(
					new ApplicationUser("admin",
							bCryptPasswordEncoder.encode("pass"),
							Collections.singleton(new Role("admin")))
			);

			repo.saveAndFlush(
					new ApplicationUser("user",
							bCryptPasswordEncoder.encode("pass"),
							Collections.singleton(new Role("user")))
			);
		}
	}

}
