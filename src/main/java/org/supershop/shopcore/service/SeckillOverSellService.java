package org.supershop.shopcore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supershop.shopcore.db.dao.SeckillActivityDao;
import org.supershop.shopcore.db.po.SeckillActivity;

@Service
public class SeckillOverSellService {

    @Autowired
    private SeckillActivityDao seckillActivityDao;

    public String processSeckill(long activityId) {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(activityId);
        int availableStock = activity.getAvailableStock();
        String result;

        if (availableStock > 0) {
            result = "Success! Order has been placed";
            System.out.println(result);

            // Update database.
            activity.setAvailableStock(--availableStock);
            seckillActivityDao.updateSeckillActivity(activity);
        } else {
            result = "Error, the item was out of stock!";
            System.out.println(result);
        }

        return result;
    }
}
