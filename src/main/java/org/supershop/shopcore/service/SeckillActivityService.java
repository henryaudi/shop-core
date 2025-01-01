package org.supershop.shopcore.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillActivityService {

    @Resource
    private RedisService redisService;


    /**
     * Verify if stock is available from Redis service.
     * @param activityId
     * @return {true} if stock is available, {false} otherwise
     */
    public boolean seckillStockValidator(long activityId) {
        String key = "stock:" + activityId;

        return redisService.stockDeductValidation(key);
    }
}
