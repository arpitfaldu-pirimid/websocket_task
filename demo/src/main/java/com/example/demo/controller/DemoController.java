package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
//this is just for testing purpose
    @GetMapping("/api/hello")
    public String hello(Authentication authentication) {
        return "{\"message\":\"JWT is working\", \"user\":\""
                + authentication.getName() + "\"}";
    }
}
