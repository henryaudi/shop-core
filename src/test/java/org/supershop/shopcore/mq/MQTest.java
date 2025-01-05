package org.supershop.shopcore.mq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
public class MQTest {

    @Resource
    private RocketMQService service;

    @Test
    public void sendMQTest() throws Exception {
        service.sendMessage("test-supershop", "Test sent on " + new Date().toString());
    }
}
