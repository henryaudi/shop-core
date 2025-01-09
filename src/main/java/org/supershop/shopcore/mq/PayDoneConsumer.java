package org.supershop.shopcore.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;
import org.supershop.shopcore.util.RedisService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Transactional
@RocketMQMessageListener(topic = "pay_done", consumerGroup = "pay_done_group")
public class PayDoneConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        // Get request body.
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Received order creation request: " + message);
        Order order = JSON.parseObject(message, Order.class);

        // Deduct stock number.
        seckillActivityDao.deductStock(order.getSeckillActivityId());
        redisService.removeLimitMember(order.getSeckillActivityId(), order.getUserId());
    }
}
