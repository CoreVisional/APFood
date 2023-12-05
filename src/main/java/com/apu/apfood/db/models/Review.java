package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class Review extends BaseModel {
    
    private String feedback;
    private int rating, orderId;
    
    public Review() {
        
    }
    
    public Review(String feedback, int rating, int orderId) {
        this.feedback = feedback;
        this.rating = rating;
        this.orderId = orderId;
    }
    
    public Review(int id, String feedback, int rating, int orderId) {
        setId(id);
        this.feedback = feedback;
        this.rating = rating;
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
    
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
