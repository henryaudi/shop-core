package org.supershop.shopcore.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supershop.shopcore.db.dao.OrderDao;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;
import org.supershop.shopcore.util.RedisService;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill_order", consumerGroup = "seckill_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SeckillActivityDao seckillActivityDao;

    @Autowired
    private RedisService redisService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        // 1. Parse order creation request message:JSON.
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Order creation request has been received: " + message);
        Order order = JSON.parseObject(message, Order.class);
        order.setCreateTime(new Date());

        // 2. Lock one item from seckillActivity.
        boolean lockStockResult = seckillActivityDao.lockStock(order.getSeckillActivityId());
        if (lockStockResult) {
            order.setOrderStatus(1);  // Success
            // Modify the user as a limit member.
            redisService.addLimitMember(order.getSeckillActivityId(), order.getUserId());
        } else {
            order.setOrderStatus(0);  // Failed
        }

        orderDao.insertOrder(order);
    }
}
