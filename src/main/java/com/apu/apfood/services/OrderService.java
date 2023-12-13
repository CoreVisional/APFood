package com.apu.apfood.services;

import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.enums.DeliveryFee;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Notification;
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
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;

    public OrderService(OrderDao orderDao, VendorService vendorService, SubscriptionService subscriptionService, NotificationService notificationService) {
        this.orderDao = orderDao;
        this.vendorService = vendorService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
    }
    
    public void addOrders(List<Order> orders, String vendorName, int userId) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        int vendorUserId = vendorService.getVendorUserId(vendorName);

        boolean isUserSubscribed = subscriptionService.isUserSubscribed(userId);

        orders.forEach(order -> {
            order.setDiscountAvailable(isUserSubscribed ? "yes" : "no");
        });

        orderDao.addOrders(orders, vendorName);

        // Send notification to the customer
        int orderId = orders.get(0).getOrderId();
        String customerNotificationContent = "Order has been placed [order id: " + orderId + ", vendor name: " + vendorName + "]";
        notificationService.addNotification(new Notification(userId, customerNotificationContent, NotificationType.TRANSACTIONAL));

        // Send notification to the vendor
        if (vendorUserId != -1) { // Check if vendor user ID was found
            String vendorNotificationContent = "New order received [order id: " + orderId + ", vendor name: " + vendorName + "]";
            notificationService.addNotification(new Notification(vendorUserId, vendorNotificationContent, NotificationType.TRANSACTIONAL));
        }
    }

    public Map<String, List<Order>> getUserOrdersGrouped(int userId, List<OrderStatus> statuses) {
        List<Order> userOrders = orderDao.getUserOrders(userId, statuses);
        return userOrders.stream()
                         .collect(Collectors.groupingBy(order -> order.getOrderId() + "-" + order.getVendorName()));
    }
    
    public double calculateDiscountAmount(double subtotal, int userId) {
        boolean isUserSubscribed = subscriptionService.isUserSubscribed(userId);

        if (isUserSubscribed && subtotal >= 12.00) {
            return subtotal * 0.10;
        }
        return 0.0;
    }

    public double calculateTotalAmountForGroupedOrders(List<Order> groupedOrders, String vendorName, int userId) {
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

        double discountAmount = calculateDiscountAmount(total, userId);

        total -= discountAmount;

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
    
    public List<Order> getOrderDetails(int userId, int orderId, String vendorName, LocalDate orderDate, LocalTime orderTime) {
        return orderDao.getUserOrders(userId, Arrays.asList(OrderStatus.values())).stream()
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
    
    public void cancelOrder(int orderId, String vendorName) {
        List<Order> orders = orderDao.getByOrderIdAndVendorName(orderId, vendorName);
        orders.forEach(order -> {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderDao.update(order);
        });
    }
    
    public List<Order> getByOrderIdAndVendorName(int orderId, String vendorName) {
        return orderDao.getByOrderIdAndVendorName(orderId, vendorName);
    }
    
    public double calculateOrderDeliveryFee(int orderId, String vendorName) {
        List<Order> orders = orderDao.getByOrderIdAndVendorName(orderId, vendorName);
        if (!orders.isEmpty() && "Delivery".equals(orders.get(0).getMode())) {
            return getDeliveryFee(orders.get(0).getDeliveryLocation());
        }
        return 0.0;
    }
}
