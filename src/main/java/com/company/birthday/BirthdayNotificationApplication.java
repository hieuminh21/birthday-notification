package com.company.birthday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class BirthdayNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BirthdayNotificationApplication.class, args);
    }

}
