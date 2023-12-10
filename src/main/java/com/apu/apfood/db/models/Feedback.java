/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.models;
import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Maxwell
 */
public class Feedback extends BaseModel{
    private String feedback;
    private int rating;
    private int orderId;
    
    public Feedback() {
        
    }
    
    public Feedback(String feedback, int rating, int orderId) {
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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
    
}
