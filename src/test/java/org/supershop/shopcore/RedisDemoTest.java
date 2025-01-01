package org.supershop.shopcore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.supershop.shopcore.service.RedisService;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {

    @Resource
    private RedisService redisService;

    @Test
    public void stockTest() {
        String value = redisService
                .setValue("stock:19", 10L)
                .getValue("stock:19");

        Assertions.assertEquals("10", value, "Failed");
    }

}
