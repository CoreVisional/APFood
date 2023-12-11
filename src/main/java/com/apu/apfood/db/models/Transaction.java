package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Alex
 */
public class Transaction extends BaseModel {
    
    private int userId;
    private double amount;
    private LocalDate transactionDate = LocalDate.now();
    private LocalTime transactionTime = LocalTime.now();
    private String remarks;
    
    public Transaction() {
        
    }

    public Transaction(int userId, double amount, String remarks) {
        this.userId = userId;
        this.amount = amount;
        this.remarks = remarks;
    }
    
    public Transaction(int id, int userId, double amount, LocalDate transactionDate, LocalTime transactionTime, String remarks) {
        setId(id);
        this.userId = userId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.remarks = remarks;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
