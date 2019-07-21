package com.stasdev.backend.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Employee {

    @GetMapping("/hello")
    String getHello(){
        return "Hello world!";
    }
}
