package org.supershop.shopcore.db.dao;

import org.supershop.shopcore.db.po.Order;

public interface OrderDao {

    public void insertOrder(Order order);

    public Order queryOrder(String orderNo);

    public void updateOrder(Order order);

}
