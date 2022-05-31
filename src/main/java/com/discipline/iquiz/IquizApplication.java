package com.discipline.iquiz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.discipline.iquiz.mapper")
@SpringBootApplication
public class IquizApplication {

    public static void main(String[] args) {
        SpringApplication.run(IquizApplication.class, args);
    }

}
