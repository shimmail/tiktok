package org.example.tiktok;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@SpringBootApplication
public class TiktokApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiktokApplication.class, args);
    }

}
