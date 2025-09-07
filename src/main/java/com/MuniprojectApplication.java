package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/*
 * @SpringBootApplication public class MuniprojectApplication {
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(MuniprojectApplication.class, args); System.out.
 * println("🚀 Aplicación Spring Boot iniciada en http://localhost:8080/"); }
 * 
 * }
 */

@SpringBootApplication
public class MuniprojectApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MuniprojectApplication.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada en http://localhost:8080/");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MuniprojectApplication.class);
    }
}