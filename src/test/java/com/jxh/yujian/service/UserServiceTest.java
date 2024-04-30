package com.jxh.yujian.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jxh.yujian.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("jxh");
        user.setUserAccount("abcde");
        user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.UBjdZXtfGC3VrnzyT8wTcQHaHa&w=176&h=185&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2");
        user.setGender(0);
        user.setUserPassword("123456789");
        user.setEmail("123456789@qq.com");
        user.setUserStatus(0);
        user.setPhone("123456789");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);

        boolean save = userService.save(user);

        Assertions.assertTrue(save);

        System.out.println(user.getId());
    }


    @Test
    void userRegister() {
        String userAccount = "super";
        String userPassword = "";
        String checkPassword = "123456789";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);

        userAccount = "sup";
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-2,result);

        userAccount = "super";
        userPassword = "123";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-4,result);

        userAccount = "abcde";
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-6,result);

        userAccount = "su per";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-3,result);

        userAccount = "super";
        checkPassword = "1234567aaaa";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-5,result);

        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void searchUsersByTags() {
        List<String> list = Arrays.asList("Java","Python");
        List<User> users = userService.searchUsersByTags(list);
        Assertions.assertNotNull(users);
    }
}