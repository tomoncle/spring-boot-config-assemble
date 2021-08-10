/*
 * Copyright 2018 tomoncle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomoncle.test.springboot.redis;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestRedisApplication.class)// 指定spring-boot的启动类
public class TestRW {
    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void testCache() {
        userService.clear();
        // 只有第一次调用时输出日志
        Assert.assertEquals(UserService.username, userService.getMyName());
        // 其余不再输出日志
        Assert.assertEquals(UserService.username, userService.getMyName());
        Assert.assertEquals(UserService.username, userService.getMyName());
        // 赋值并清空用户
        userService.setUsername("sample");
        // 再次调用
        Assert.assertEquals(UserService.username, userService.getMyName());
    }

    /**
     * 压入200w数据耗时：3.668 s.
     * 数据大小：76.2939453125MB
     */
    @Test
    public void pushByOpsForHash() {
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        final String hashName = "hash_test";
        Map<String, Long> map = new HashMap<>();
        Map<String, Long> tempMap = new HashMap<>();
        for (int i = 0; i < 200 * 10000; i++) {
            map.put(DigestUtils.md5DigestAsHex((i + "").getBytes()), System.currentTimeMillis());
        }

        long start = System.currentTimeMillis();
        for (String key : map.keySet()) {
            tempMap.put(key, map.get(key));
            if (tempMap.size() >= 10000) {
                redisTemplate.opsForHash().putAll(hashName, tempMap);
                tempMap.clear();
            }
        }
        if (tempMap.size() > 0) {
            redisTemplate.opsForHash().putAll(hashName, tempMap);
        }
        System.out.println("压入200w数据耗时：" + (System.currentTimeMillis() - start) / 1000.0 + " s.");
        System.out.println("数据大小：" + 40 * 200 * 10000 / 1024 / 1024.0 + "MB");
    }

    /**
     * 压入200w数据耗时：2.07 s.
     * 数据大小：76.2939453125MB
     */
    @Test
    public void pushByPipeline() {
        final String hashName = "hash_test";
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < 200 * 10000; i++) {
            map.put(DigestUtils.md5DigestAsHex((i + "").getBytes()), System.currentTimeMillis());
        }
        long start = System.currentTimeMillis();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            Map<byte[], byte[]> hashes = new HashMap<>();
            for (String key : map.keySet()) {
//                connection.hSet(hashName.getBytes(),key.getBytes(),new byte[]{map.get(key).byteValue()});
                hashes.put(key.getBytes(), map.get(key).toString().getBytes());
                if (hashes.size() >= 10000) {
                    connection.hMSet(hashName.getBytes(), hashes);
                    hashes.clear();
                }
            }
            if (hashes.size() > 0) {
                connection.hMSet(hashName.getBytes(), hashes);
            }
            return null;
        });
        System.out.println("压入200w数据耗时：" + (System.currentTimeMillis() - start) / 1000.0 + " s.");
        System.out.println("数据大小：" + 40 * 200 * 10000 / 1024 / 1024.0 + "MB");
    }

    @Test
    public void get() {
        final String hashName = "hash_test";
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        long start = System.currentTimeMillis();
        Map<String, Long> map = new HashMap<>();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashName);
        System.out.println("加载 " + map.size() + " 条数据，耗时：" + (System.currentTimeMillis() - start) / 1000.0);
        for (Object key : entries.keySet()) {
            map.put(String.valueOf(key), Long.valueOf(String.valueOf(entries.get(key))));
        }
        System.out.println("加载 " + map.size() + " 条数据，耗时：" + (System.currentTimeMillis() - start) / 1000.0);
    }


    /**
     * 使用list 模拟 消息队列， 左端压入，右端获取数据
     */
    @SneakyThrows
    @Test
    public void provider() {
        Runnable runnable1 = () -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                redisTemplate.opsForList().leftPush("test", String.valueOf(System.currentTimeMillis()));
            }
        };
        new Thread(runnable1).start();
        Thread.sleep(1000 * 600);
    }


    @SneakyThrows
    @Test
    public void provider2() {
        Runnable runnable1 = () -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                redisTemplate.executePipelined(new RedisCallback<String>() {

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public String doInRedis(RedisConnection connection) throws DataAccessException {
                        connection.listCommands().lPush("test".getBytes(), "".getBytes());
                        return null;
                    }
                });
            }
        };
        new Thread(runnable1).start();
        Thread.sleep(1000 * 600);
    }

    /**
     * 使用list 模拟 消息队列， 左端压入，右端获取数据
     */
    @Test
    public void consumer() {
        Runnable runnable1 = () -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                String value = redisTemplate.opsForList().rightPop("test", 1, TimeUnit.DAYS);
                System.out.println(Thread.currentThread().getName() + ": " + value);
            }
        };
        Runnable runnable2 = () -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                String value = redisTemplate.opsForList().rightPop("test", 1, TimeUnit.DAYS);
                System.out.println(Thread.currentThread().getName() + ": " + value);
            }
        };
        Runnable runnable3 = () -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Long value = redisTemplate.opsForList().size("test");
                System.out.println(Thread.currentThread().getName() + ": " + value);
            }
        };
        new Thread(runnable1).start();
        new Thread(runnable2).start();
        new Thread(runnable3).start();
        try {
            Thread.sleep(1000 * 600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
