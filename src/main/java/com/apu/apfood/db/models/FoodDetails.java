package com.apu.apfood.db.models;

/**
 *
 * @author Bryan
 */
public class FoodDetails {

    private String foodName;
    private String foodId;
    private String quantity;

    public FoodDetails(String foodName, String foodId, String quantity) {
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
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
}
