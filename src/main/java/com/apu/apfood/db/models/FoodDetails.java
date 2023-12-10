package com.apu.apfood.db.models;

/**
 *
 * @author Bryan
 */
public class FoodDetails {

    private String foodName;
    private String foodId;
    private String quantity;
    private double price;
    
    private String remark="";

    public FoodDetails(String foodName, String foodId, String quantity) {
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
    }
    
    public FoodDetails(String foodName, String foodId, String quantity, String remark) {
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
        this.remark = remark;
    }
    
    public FoodDetails(String foodName, String foodId, String quantity, String remark, double price) {
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
        this.remark = remark;
        this.price = price;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getQuantity() {
        return quantity;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public double getPrice() {
        return price;
    }
}
