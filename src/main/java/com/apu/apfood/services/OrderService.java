package com.apu.apfood.services;

import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.enums.DeliveryFee;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Order;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class OrderService {
    
    private final OrderDao orderDao;
    private final VendorService vendorService;

    public OrderService(OrderDao orderDao, VendorService vendorService) {
        this.orderDao = orderDao;
        this.vendorService = vendorService;
    }
    
    public void addOrders(List<Order> orders, String vendorName) {
        orderDao.addOrders(orders, vendorName);
    }

    public Map<String, List<Order>> getUserOrdersGrouped(int userId, List<OrderStatus> statuses) {
        List<Order> userOrders = orderDao.getUserOrders(userId, statuses);
        return userOrders.stream()
                         .collect(Collectors.groupingBy(order -> order.getOrderId() + "-" + order.getVendorName()));
    }

    public double calculateTotalAmountForGroupedOrders(List<Order> groupedOrders, String vendorName) {
        if (groupedOrders.isEmpty()) {
            return 0.0;
        }

        Map<Integer, Double> menuPriceMap = vendorService.getVendorMenuItems(vendorName)
                                                         .stream()
                                                         .collect(Collectors.toMap(Menu::getId, Menu::getPrice));

        double total = 0.0;
        for (Order order : groupedOrders) {
            double itemTotal = menuPriceMap.getOrDefault(order.getMenuId(), 0.0) * order.getQuantity();
            total += itemTotal;
        }

        double deliveryFee = "Delivery".equals(groupedOrders.get(0).getMode()) 
                             ? getDeliveryFee(groupedOrders.get(0).getDeliveryLocation()) 
                             : 0.0;

        return total + deliveryFee;
    }

    public static double getDeliveryFee(String deliveryLocation) {
        if (deliveryLocation == null || deliveryLocation.isEmpty()) {
            return 0.0;
        }

        try {
            DeliveryFee feeEnum = DeliveryFee.valueOf(deliveryLocation.toUpperCase().replace(" ", "_"));
            return feeEnum.getFee();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid delivery location: " + deliveryLocation);
            return 0.0;
        }
    }
    
    public List<Order> getOrderDetails(int orderId, String vendorName, LocalDate orderDate, LocalTime orderTime) {
        return orderDao.getUserOrders(1, Arrays.asList(OrderStatus.values())).stream()
            .filter(order -> order.getOrderId() == orderId &&
                             order.getVendorName().equals(vendorName) &&
                             order.getOrderId() == orderId &&
                             order.getOrderDate().equals(orderDate) &&
                             order.getOrderTime().truncatedTo(ChronoUnit.MINUTES).equals(orderTime.truncatedTo(ChronoUnit.MINUTES)))
            .collect(Collectors.toList());
    }
    
    public void updateOrderMode(int orderId, String newMode, String vendorName) {
        orderDao.updateOrderMode(orderId, newMode, vendorName);
    }
}
