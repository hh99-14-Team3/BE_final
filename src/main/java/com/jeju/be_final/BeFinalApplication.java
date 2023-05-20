package com.jeju.be_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BeFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeFinalApplication.class, args);
    }

}
