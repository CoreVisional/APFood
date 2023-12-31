package com.apu.apfood.db.models;

import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class Notification extends BaseModel {
    private int userId, extractedUserId, transactionId;
    private String orderId, vendorName, content;
    private NotificationStatus notificationStatus = NotificationStatus.UNNOTIFIED;
    private NotificationType notificationType;
    
    public Notification() {
        
    }
    
    public Notification(int userId, String content, NotificationType notificationType) {
        this.userId = userId;
        this.content = content;
        this.notificationType = notificationType;
    }

    public Notification(int id, int userId, String content, NotificationStatus notificationStatus, NotificationType notificationType) {
        setId(id); 
        this.userId = userId;
        this.content = content;
        this.notificationStatus = notificationStatus;
        this.notificationType = notificationType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getExtractedUserId() {
        return extractedUserId;
    }

    public void setExtractedUserId(int extractedUserId) {
        this.extractedUserId = extractedUserId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
