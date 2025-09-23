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
        // Detectar si estamos en Railway
        boolean isRailway = System.getenv("RAILWAY_STATIC_URL") != null;

        // ✅ Inyectar el puerto asignado por Railway si existe
        String port = System.getenv("PORT");
        if (port != null) {
            System.setProperty("server.port", port);
            System.out.println("🌐 Puerto asignado por Railway: " + port);
        } else {
            System.out.println("⚠️ Variable PORT no definida, usando valor por defecto");
        }

        // ✅ Cargar entorno.env solo en local
        if (!isRailway) {
            Dotenv dotenv = Dotenv.configure()
                .filename("entorno.env")
                .ignoreIfMissing()
                .load();

            safeSetProperty("PGHOST", dotenv.get("PGHOST", System.getenv("PGHOST")));
            safeSetProperty("PGPORT", dotenv.get("PGPORT", System.getenv("PGPORT")));
            safeSetProperty("PGDATABASE", dotenv.get("PGDATABASE", System.getenv("PGDATABASE")));
            safeSetProperty("PGUSER", dotenv.get("PGUSER", System.getenv("PGUSER")));
            safeSetProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD", System.getenv("POSTGRES_PASSWORD")));
        }


        SpringApplication.run(MuniprojectApplication.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada en http://localhost:8080/");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MuniprojectApplication.class);
    }
    
    // ✅ Método auxiliar para evitar NullPointerException
    private static void safeSetProperty(String key, String value) {
        if (value != null && !value.isBlank()) {
            System.setProperty(key, value);
        }
    }
}
