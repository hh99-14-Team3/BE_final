package com.mogakko.be_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BeFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeFinalApplication.class, args);
    }

}
