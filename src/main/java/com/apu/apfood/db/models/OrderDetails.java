package com.apu.apfood.db.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bryan
 */
public class OrderDetails {

    private String accountId;
    private String orderId;
    private String vendorName;
    
    private String customerName;
    private String mode;
    private String orderDate;
    private String orderTime;
    private String deliveryLocation;

    private List<FoodDetails> foodDetailsList = new ArrayList<>();
    
    public OrderDetails(){
        
    }

    public void addFoodDetails(String foodName, String foodId, String quantity) {
        FoodDetails foodDetails = new FoodDetails(foodName, foodId, quantity);
        foodDetailsList.add(foodDetails);
    }
    
    public void addFoodDetails (String foodName, String foodId, String quantity, String remark, double price)
    {
        FoodDetails foodDetails = new FoodDetails(foodName, foodId, quantity, remark, price);
        foodDetailsList.add(foodDetails);
    }
    
    

    public String getAccountId() {
        return accountId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public List<FoodDetails> getFoodDetailsList() {
        return foodDetailsList;
    }
    
    public String getFoodNameList() 
    {
        String list = "";
        for (FoodDetails foodDetails : foodDetailsList)
        {
            list += foodDetails.getFoodName() + "\n";
        }
        return list;
    }

    public String getFoodQuantityList()
    {
        String list = "";
        for (FoodDetails foodDetails : foodDetailsList)
        {
            list += foodDetails.getQuantity() + "\n";
        }
        return list;
    }
    
    public String getFoodRemarkList()
    {
        String list = "";
        for (FoodDetails foodDetails : foodDetailsList)
        {
            list += foodDetails.getRemark() + "\n";
        }
        return list;
    }
    
    public String getFoodPriceList()
    {
        String list = "";
        for (FoodDetails foodDetails : foodDetailsList)
        {
            list += foodDetails.getPrice() + "\n";
        }
        return list;
    }
    
    public String getDeliveryLocation()
    {
        return deliveryLocation;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public void setDeliveryLocation(String deliveryLocation)
    {
        this.deliveryLocation = deliveryLocation;
    }
}
