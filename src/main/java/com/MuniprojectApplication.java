package com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MuniprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuniprojectApplication.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada");
    }

    
    @Bean
    CommandLineRunner dbg(Environment env) {
        return args -> {
            System.out.println(">>> ENV PORT=" + System.getenv("PORT"));
            System.out.println(">>> ENV SERVER_PORT=" + System.getenv("SERVER_PORT"));
            System.out.println(">>> SYS server.port=" + System.getProperty("server.port"));
            System.out.println(">>> RESOLVED server.port=" + env.getProperty("server.port"));
        };
    }

}
