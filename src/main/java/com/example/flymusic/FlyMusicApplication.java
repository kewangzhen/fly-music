package com.example.flymusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlyMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyMusicApplication.class, args);
    }

}
