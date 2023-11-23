package com.apu.apfood.services;

import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.models.Order;
import java.util.List;

/**
 *
 * @author Alex
 */
public class OrderService {
    
    private final OrderDao orderDao;
    
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
    
    public void addOrders(List<Order> orders) {
        orderDao.addOrders(orders);
    }
}
