package com.jxh.yujian.service;

import com.jxh.yujian.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUserTest {

    @Resource
    private UserService userService;

    //线程设置
    //I/O密集型 和 CPU密集型
    private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     *批量插入   耗时 1032ms
     */
    @Test
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i ++ ) {
            User user = new User();
            user.setUsername("假人");
            user.setUserAccount("jiaren");
            user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.UBjdZXtfGC3VrnzyT8wTcQHaHa&w=176&h=185&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2");
            user.setGender(0);
            user.setUserPassword("123456789");
            user.setEmail("123456789@qq.com");
            user.setPhone("12345678910");
            user.setTags("[]");
            user.setProfile("这是一条假数据");
            userList.add(user);
        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 5107
     */
    @Test
    public void doConcurrencyInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        //分十组
        int j = 0;
        //批量插入数据大小
        int batchSize = 5000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        //i 要根据数据量和插入批量来计算需要循环的次数
        for (int i = 0; i < INSERT_NUM / batchSize; i ++ ) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j ++;
                User user = new User();
                user.setUsername("假人");
                user.setUserAccount("jiaren");
                user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.UBjdZXtfGC3VrnzyT8wTcQHaHa&w=176&h=185&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2");
                user.setGender(0);
                user.setUserPassword("123456789");
                user.setEmail("123456789@qq.com");
                user.setPhone("12345678910");
                user.setTags("[]");
                user.setProfile("这是一条假数据");
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
                System.out.println("ThreadName：" + Thread.currentThread().getName());
                userService.saveBatch(userList,batchSize);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
