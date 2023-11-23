/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.models;

import com.apu.apfood.db.dao.RunnerTaskDao;
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

    private List<FoodDetails> foodDetailsList = new ArrayList<>();

    public void addFoodDetails(String foodName, String foodId, String quantity) {
        FoodDetails foodDetails = new FoodDetails(foodName, foodId, quantity);
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

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
