package com.dcuobot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DcuobotApiApplication {
    static void main(String[] args) {
        SpringApplication.run(DcuobotApiApplication.class, args);
    }
}
