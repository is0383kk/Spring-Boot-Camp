package com.example.springdicontainer2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springdicontainer2.data.Logic;

@Configuration
public class AppConfig {

    @Bean
    Logic logicMethod() {
        return new Logic();
    }

}
