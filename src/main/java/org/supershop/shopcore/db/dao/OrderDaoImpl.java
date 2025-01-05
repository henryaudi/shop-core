package org.supershop.shopcore.db.dao;

import org.springframework.stereotype.Repository;
import org.supershop.shopcore.db.mappers.OrderMapper;
import org.supershop.shopcore.db.po.Order;

import javax.annotation.Resource;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Resource
    private OrderMapper mapper;

    @Override
    public void insertOrder(Order order) {
        mapper.insert(order);
    }

    @Override
    public Order queryOrder(String orderNo) {
        return mapper.selectByOrderNo(orderNo);
    }

    @Override
    public void updateOrder(Order order) {
        mapper.updateByPrimaryKey(order);
    }
}
