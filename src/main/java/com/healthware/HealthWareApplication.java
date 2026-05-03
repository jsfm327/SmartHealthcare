package com.healthware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.healthware.mapper")
public class HealthWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthWareApplication.class, args);
    }
}
