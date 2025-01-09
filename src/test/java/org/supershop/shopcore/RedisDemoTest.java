package org.supershop.shopcore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.supershop.shopcore.service.SeckillActivityService;
import org.supershop.shopcore.util.RedisService;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {

    @Resource
    SeckillActivityService seckillActivityService;
    @Resource
    private RedisService redisService;

    @Test
    public void stockTest() {
        String value = redisService
                .setValue("stock:19", 10L)
                .getValue("stock:19");

        Assertions.assertEquals("10", value, "Failed");
    }

    @Test
    public void pushSeckillInfoToRedisTest(){
        seckillActivityService.pushSeckillInfoToRedis(19);
    }

}
