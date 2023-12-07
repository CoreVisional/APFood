package com.apu.apfood.db.models;

import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.common.BaseModel;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Alex
 */
public class Order extends BaseModel {

    private int orderId, userId, menuId, quantity;
    private LocalDate orderDate = LocalDate.now();
    private LocalTime orderTime = LocalTime.now();
    private String vendorName, remarks, mode, deliveryLocation, discountAvailable;
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public Order() {
        
    }

    public Order(int userId, int menuId, int quantity, String remarks, String mode, String deliveryLocation) {
        this.userId = userId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.remarks = remarks;
        this.mode = mode;
        this.deliveryLocation = deliveryLocation;
    }

    public Order(int id, int orderId, int userId, int menuId, int quantity, LocalDate orderDate, LocalTime orderTime, String remarks, String mode, OrderStatus orderStatus, String deliveryLocation, String discountAvailable) {
        setId(id);
        this.orderId = orderId;
        this.userId = userId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.remarks = remarks;
        this.mode = mode;
        this.orderStatus = orderStatus;
        this.deliveryLocation = deliveryLocation;
        this.discountAvailable = discountAvailable;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
    
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalTime orderTime) {
        this.orderTime = orderTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String isDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
