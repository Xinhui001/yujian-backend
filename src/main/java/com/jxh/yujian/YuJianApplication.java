package com.jxh.yujian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 20891
 */
@SpringBootApplication
@MapperScan("com.jxh.yujian.mapper")
public class YuJianApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuJianApplication.class, args);
    }

}
