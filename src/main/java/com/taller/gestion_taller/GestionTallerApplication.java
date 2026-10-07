package com.taller.gestion_taller;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionTallerApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        setIfMissing("DB_URL", dotenv.get("DB_URL"));
        setIfMissing("DB_USERNAME", dotenv.get("DB_USERNAME"));
        setIfMissing("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        setIfMissing("JWT_SECRET", dotenv.get("JWT_SECRET"));
        setIfMissing("JWT_EXPIRACION_MS", dotenv.get("JWT_EXPIRACION_MS"));
        setIfMissing("JPA_DDL_AUTO", dotenv.get("JPA_DDL_AUTO"));

        SpringApplication.run(GestionTallerApplication.class, args);
    }

    private static void setIfMissing(String key, String value) {
        if (System.getProperty(key) == null
                && System.getenv(key) == null
                && value != null
                && !value.isBlank()) {

            System.setProperty(key, value);
        }
    }
}


