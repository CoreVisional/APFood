package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;
import java.time.LocalDate;

/**
 *
 * @author Alex
 */
public class Subscription extends BaseModel {
    
    private int userId;
    private LocalDate subscriptionStartDate, subscriptionEndDate;
    
    public Subscription() {
        
    }
    
    public Subscription(int userId, LocalDate subscriptionStartDate, LocalDate subscriptionEndDate) {
        this.userId = userId;
        this.subscriptionStartDate = subscriptionStartDate;
        this.subscriptionEndDate = subscriptionEndDate;
    }
    
    public Subscription(int id, int userId, LocalDate subscriptionStartDate, LocalDate subscriptionEndDate) {
        setId(id);
        this.userId = userId;
        this.subscriptionStartDate = subscriptionStartDate;
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }
}
