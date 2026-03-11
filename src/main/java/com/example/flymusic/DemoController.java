package com.example.flymusic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public String demo() {
        return "Hello from Fly Music Demo Controller!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}