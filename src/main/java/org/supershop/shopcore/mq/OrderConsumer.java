package org.supershop.shopcore.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supershop.shopcore.db.dao.OrderDao;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill_order", consumerGroup = "seckill_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {

    @Resource
    private OrderDao orderDao;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        // 1. Parse order creation request message:JSON.
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Order creation request has been received: " + message);
        Order order = JSON.parseObject(message, Order.class);
        order.setCreateTime(new Date());

        // 2. Decrement the available stock.
        boolean lockStockResult = seckillActivityDao.lockStock(order.getSeckillActivityId());
        if (lockStockResult) {
            /**
             * Order creation SUCCESS
             * 1 = order created, waiting for user payment
             */
            order.setOrderStatus(1);
        } else {
            /**
             * Order creation FAILED
             * 0 = order creation failed, void the order
             */
            order.setOrderStatus(0);
        }

        orderDao.insertOrder(order);
    }
}
