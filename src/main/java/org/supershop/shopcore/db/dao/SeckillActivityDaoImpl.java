package org.supershop.shopcore.db.dao;

import lombok.extern.slf4j.Slf4j;
import org.supershop.shopcore.db.mappers.SeckillActivityMapper;
import org.supershop.shopcore.db.po.SeckillActivity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class SeckillActivityDaoImpl implements SeckillActivityDao {

    @Resource
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    public List<SeckillActivity> querySeckillActivitysByStatus(int activityStatus) {
        return seckillActivityMapper.querySeckillActivitysByStatus(activityStatus);
    }

    @Override
    public void inertSeckillActivity(SeckillActivity seckillActivity) {
        seckillActivityMapper.insert(seckillActivity);
    }

    @Override
    public SeckillActivity querySeckillActivityById(long activityId) {
        return seckillActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateSeckillActivity(SeckillActivity seckillActivity) {
        seckillActivityMapper.updateByPrimaryKey(seckillActivity);
    }

    @Override
    public boolean lockStock(Long seckillActivityId) {
        int result = seckillActivityMapper.lockStock(seckillActivityId);

        if (result < 1) {
            log.error("Failed to lock the order.");
            return false;
        }

        return true;
    }

    @Override
    public boolean deductStock(Long seckillActivityId) {
        int result = seckillActivityMapper.deductStock(seckillActivityId);

        if (result < 1) {
            log.error("Failed to deduct from stock.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void revertStock(Long seckillActivityId) {
        seckillActivityMapper.revertStock(seckillActivityId);
    }
}