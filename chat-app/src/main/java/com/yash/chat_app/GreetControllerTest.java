package com.yash.chat_app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetControllerTest {
    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }
}
