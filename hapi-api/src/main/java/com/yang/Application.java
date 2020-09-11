package com.yang;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author yg
 * @date 2020/6/11 10:50
 */
@MapperScan("com.yang.mapper")
@ComponentScan(value = {"com.yang", "org.n3r"})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
