package org.supershop.shopcore.db.dao;

import org.supershop.shopcore.db.po.Order;

public interface OrderDao {

    void insertOrder(Order order);

    Order queryOrder(String orderNo);

    void updateOrder(Order order);

}
