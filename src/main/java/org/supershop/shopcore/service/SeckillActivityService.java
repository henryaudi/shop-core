package org.supershop.shopcore.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.supershop.shopcore.db.dao.OrderDao;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;
import org.supershop.shopcore.db.po.SeckillActivity;
import org.supershop.shopcore.mq.RocketMQService;
import org.supershop.shopcore.util.RedisService;
import org.supershop.shopcore.util.SnowFlake;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class SeckillActivityService {

    @Resource
    private RedisService redisService;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RocketMQService rocketMQService;

    @Resource
    private OrderDao orderDao;

    private SnowFlake snowFlake = new SnowFlake(1, 1);

    /**
     * Verify if stock is available from Redis service.
     * @param activityId
     * @return {true} if stock is available, {false} otherwise
     */
    public boolean seckillStockValidator(long activityId) {
        String key = "stock:" + activityId;

        return redisService.stockDeductValidation(key);
    }

    public Order createOrder(long seckillActivityId, long userId) throws Exception {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        Order order = new Order();

        order.setOrderNo(String.valueOf(snowFlake.nextId()));  // Generate SnowFlake ID.
        order.setSeckillActivityId(activity.getId());
        order.setUserId(userId);
        order.setOrderAmount(activity.getSeckillPrice().longValue());

        // Send creation message to MQ.
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));

        // Send message to verify payment after delay.
        // Test some commit.
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 5);

        return order;
    }

    public void payOrderProcess(String orderNo) {
        log.info("Payment completed, order number - " + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        boolean deductStockResult = seckillActivityDao.deductStock(order.getSeckillActivityId());

        if (deductStockResult) {
            order.setPayTime(new Date());

            // 0 - invalid order
            // 1 - Order created pending payment
            // 2 - Payment complete
            order.setOrderStatus(2);
            orderDao.updateOrder(order);
        }
    }
}
