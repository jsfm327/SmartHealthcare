package com.healthware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.healthware.mapper")
@EnableScheduling
public class HealthWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthWareApplication.class, args);
    }
}
