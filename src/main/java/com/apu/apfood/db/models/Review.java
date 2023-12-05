package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class Review extends BaseModel {
    
    private String feedback;
    private int orderId, rating;
    
    public Review() {
        
    }
    
    public Review(int orderId, String feedback, int rating) {
        this.orderId = orderId;
        this.feedback = feedback;
        this.rating = rating;
    }
    
    public Review(int id, int orderId, String feedback, int rating) {
        setId(id);
        this.orderId = orderId;
        this.feedback = feedback;
        this.rating = rating;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
