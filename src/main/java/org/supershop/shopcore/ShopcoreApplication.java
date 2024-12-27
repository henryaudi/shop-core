package org.supershop.shopcore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.supershop.shopcore.db.mappers")
@ComponentScan(basePackages = {"org.supershop"})
public class ShopcoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopcoreApplication.class, args);
    }

}
