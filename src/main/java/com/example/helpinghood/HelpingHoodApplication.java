package com.example.helpinghood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelpingHoodApplication {

    public static void main(String[] args) {
        // Avvia il contesto Spring, registra i bean e pubblica l'app web.
        SpringApplication.run(HelpingHoodApplication.class, args);
    }

}
