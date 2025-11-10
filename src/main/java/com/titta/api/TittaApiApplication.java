package com.titta.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TittaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TittaApiApplication.class, args);
    }

}
