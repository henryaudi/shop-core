package org.supershop.shopcore.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.Order;
import org.supershop.shopcore.db.po.SeckillActivity;
import org.supershop.shopcore.mq.RocketMQService;
import org.supershop.shopcore.util.SnowFlake;

import javax.annotation.Resource;

@Service
public class SeckillActivityService {

    @Resource
    private RedisService redisService;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RocketMQService rocketMQService;

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

        return order;
    }
}
