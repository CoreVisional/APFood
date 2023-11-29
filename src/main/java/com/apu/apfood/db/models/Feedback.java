/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.models;

import com.apu.apfood.db.enums.Rating;
import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Maxwell
 */
public class Feedback extends BaseModel{
    private String feedback;
    private Rating rating;
    private int orderId;
    
    public Feedback() {
        
    }
    
    public Feedback(String feedback, Rating rating, int orderId) {
        this.feedback = feedback;
        this.rating = rating;
        this.orderId = orderId;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
    
}
