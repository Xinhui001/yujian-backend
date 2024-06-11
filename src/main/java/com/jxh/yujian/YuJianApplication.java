package com.jxh.yujian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 20891
 */
@MapperScan("com.jxh.yujian.mapper")
@SpringBootApplication
@EnableScheduling
public class YuJianApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuJianApplication.class, args);
    }

}
