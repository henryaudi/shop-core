package org.supershop.shopcore.mq;


import com.alibaba.fastjson.JSON;
import org.supershop.shopcore.db.dao.OrderDao;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;
import org.supershop.shopcore.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_check", consumerGroup = "pay_check_group")
public class PayStatusCheckListener implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RedisService redisService;


    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Receive order payment verification status: " + message);
        Order order = JSON.parseObject(message, Order.class);
        // 1.Check for the order information.
        Order orderInfo = orderDao.queryOrder(order.getOrderNo());

        if (orderInfo == null) {
            log.error("Order not found for orderNo: {}", order.getOrderNo());
            return;
        }

        // 2. Confirm if payment was made
        if (orderInfo.getOrderStatus() != 2) {
            // 3. Close unpaid order.
            log.info("Closing unpaid order - " + orderInfo.getOrderNo());
            orderInfo.setOrderStatus(99);
            orderDao.updateOrder(orderInfo);
            // 4. Rollback database status for the order.
            seckillActivityDao.revertStock(order.getSeckillActivityId());
            // 5. Rollback redis status
            redisService.revertStock("stock:" + order.getSeckillActivityId());
            // 6. Remove the member from limit list.
            redisService.removeLimitMember(order.getSeckillActivityId(), order.getUserId());
        }
    }
}
