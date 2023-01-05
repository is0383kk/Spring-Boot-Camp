package com.example.springdicontainer1.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.example.springdicontainer1.data"})
public class AppConfig {
	
}
