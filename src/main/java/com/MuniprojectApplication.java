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

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MuniprojectApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        // Cargar variables desde .env solo si no están definidas en el entorno
    	Dotenv dotenv = Dotenv.configure().filename("entorno.env").load();

        System.setProperty("PGHOST", dotenv.get("PGHOST", System.getenv("PGHOST")));
        System.setProperty("PGPORT", dotenv.get("PGPORT", System.getenv("PGPORT")));
        System.setProperty("PGDATABASE", dotenv.get("PGDATABASE", System.getenv("PGDATABASE")));
        System.setProperty("PGUSER", dotenv.get("PGUSER", System.getenv("PGUSER")));
        System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD", System.getenv("POSTGRES_PASSWORD")));

        System.out.println("🔍 Puerto asignado por Railway (PORT): " + System.getenv("PORT"));
        
        SpringApplication.run(MuniprojectApplication.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada en http://localhost:8080/");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MuniprojectApplication.class);
    }
}
